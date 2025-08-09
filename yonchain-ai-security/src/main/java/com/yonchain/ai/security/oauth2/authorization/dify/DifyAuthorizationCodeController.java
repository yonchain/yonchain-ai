package com.yonchain.ai.security.oauth2.authorization.dify;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.security.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

/**
 * 自定义授权码生成控制器
 * 不需要用户认证，直接生成授权码
 */
@RestController
public class DifyAuthorizationCodeController {

 /*   private final OAuth2AuthorizationService authorizationService;
    private final RegisteredClientRepository registeredClientRepository;
    private final SecureRandom secureRandom;
    private final SecurityService securityService;

    public DifyAuthorizationCodeController(OAuth2AuthorizationService authorizationService,
                                           RegisteredClientRepository registeredClientRepository,
                                           SecurityService securityService) {
        this.authorizationService = authorizationService;
        this.registeredClientRepository = registeredClientRepository;
        this.secureRandom = new SecureRandom();
        this.securityService = securityService;
    }

    *//**
     * 自定义授权码生成端点
     * 支持GET和POST请求，符合OAuth2标准
     *//*
    @GetMapping("/oauth/authorize")
    public ResponseEntity<?> authorizeGet(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "response_type", defaultValue = "code") String responseType) {

        return authorize(clientId, redirectUri, scope, state, responseType);
    }

    @PostMapping("/oauth/authorize")
    public ResponseEntity<?> authorizePost(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "response_type", defaultValue = "code") String responseType) {

        return authorize(clientId, redirectUri, scope, state, responseType);
    }

    private ResponseEntity<?> authorize(String clientId, String redirectUri, String scope, String state, String responseType) {
        try {
            // 1. 验证客户端
            RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
            if (registeredClient == null) {
                // 如果客户端无效，直接返回错误（不重定向）
                return errorResponse("invalid_client", "Client not found: " + clientId);
            }

            // 2. 验证重定向URI
            if (!registeredClient.getRedirectUris().contains(redirectUri)) {
                // 如果重定向URI无效，直接返回错误（不重定向）
                return errorResponse("invalid_redirect_uri", "Redirect URI not registered for client");
            }

            // 验证响应类型（现在我们有了有效的redirect_uri，可以通过重定向返回错误）
            if (!"code".equals(responseType)) {
                return redirectError(redirectUri, "unsupported_response_type",
                        "Only 'code' response type is supported", state);
            }

            // 3. 验证作用域
            Set<String> requestedScopes = new HashSet<>();
            if (StringUtils.hasText(scope)) {
                for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
                    requestedScopes.add(requestedScope);
                }
            }

            if (requestedScopes.isEmpty()) {
                // 如果未指定作用域，使用客户端的默认作用域
                requestedScopes.addAll(registeredClient.getScopes());
            } else {
                // 验证请求的作用域是否在客户端允许的作用域内
                for (String requestedScope : requestedScopes) {
                    if (!registeredClient.getScopes().contains(requestedScope)) {
                        return errorResponse("invalid_scope", "Scope not allowed: " + requestedScope);
                    }
                }
            }

            // 4. 生成安全的授权码
            byte[] bytes = new byte[32]; // 256 bits
            this.secureRandom.nextBytes(bytes);
            String authorizationCode = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plusSeconds(300); // 5分钟有效期

            OAuth2AuthorizationCode code = new OAuth2AuthorizationCode(
                    authorizationCode,
                    issuedAt,
                    expiresAt
            );

            // 从当前线程的请求上下文中获取OAuth2 token
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");
            if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
                throw new YonchainException("未获取到有效的OAuth2 token");
            }
            token = token.substring(7);
            String userId = securityService.getUserIdFromToken(token);

            OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                    .principalName(userId)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizedScopes(requestedScopes)
                    .attribute(OAuth2ParameterNames.CODE, authorizationCode)
                    .attribute(OAuth2ParameterNames.REDIRECT_URI, redirectUri);

            if (state != null) {
                builder.attribute(OAuth2ParameterNames.STATE, state);
            }

            // 确保授权码可以被token端点正确识别
            OAuth2Authorization authorization = builder
                    .token(code, (metadata) -> {
                        metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, new HashMap<>());
                        metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
                        // 添加必要的元数据，确保与标准OAuth2兼容
                        metadata.put(OAuth2ParameterNames.CLIENT_ID, clientId);
                        metadata.put(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
                        if (StringUtils.hasText(scope)) {
                            metadata.put(OAuth2ParameterNames.SCOPE, scope);
                        }
                    })
                    .build();

            // 6. 保存授权信息
            authorizationService.save(authorization);

            // 7. 构建重定向URL
            StringBuilder redirectUrl = new StringBuilder(redirectUri);
            redirectUrl.append(redirectUri.contains("?") ? "&" : "?");
            redirectUrl.append("code=").append(authorizationCode);
            if (state != null) {
                redirectUrl.append("&state=").append(state);
            }

            // 返回302重定向
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl.toString())
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return errorResponse("server_error", ex.getMessage());
        }
    }

    *//**
     * 验证授权码状态
     * @param code 授权码
     * @return 授权码信息
     *//*
    @GetMapping("/verify-code")
    public Map<String, Object> verifyAuthorizationCode(@RequestParam("code") String code) {
        Map<String, Object> result = new HashMap<>();

        // 通过授权码查找授权信息
        OAuth2Authorization authorization = authorizationService.findByToken(
                code,
                new OAuth2TokenType("code"));

        if (authorization == null) {
            result.put("valid", false);
            result.put("message", "Authorization code not found or expired");
            return result;
        }

        result.put("valid", true);
        result.put("client_id", authorization.getRegisteredClientId());
        result.put("principal", authorization.getPrincipalName());
        result.put("scopes", authorization.getAuthorizedScopes());
        result.put("redirect_uri", authorization.getAttribute("redirect_uri"));

        return result;
    }

    *//**
     * 验证访问令牌状态
     * @param token 访问令牌
     * @return 令牌信息
     *//*
    @GetMapping("/verify-token")
    public Map<String, Object> verifyAccessToken(@RequestParam("token") String token) {
        Map<String, Object> result = new HashMap<>();

        // 通过访问令牌查找授权信息
        OAuth2Authorization authorization = authorizationService.findByToken(
                token,
                OAuth2TokenType.ACCESS_TOKEN);

        if (authorization == null) {
            result.put("valid", false);
            result.put("message", "Access token not found or expired");
            return result;
        }

        result.put("valid", true);
        result.put("client_id", authorization.getRegisteredClientId());
        result.put("principal", authorization.getPrincipalName());
        result.put("scopes", authorization.getAuthorizedScopes());

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            result.put("expires_at", accessToken.getToken().getExpiresAt());
            result.put("issued_at", accessToken.getToken().getIssuedAt());
        }

        return result;
    }

    private ResponseEntity<Map<String, String>> errorResponse(String error, String description) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("error_description", description);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private ResponseEntity<?> redirectError(String redirectUri, String error, String description, String state) {
        StringBuilder redirectUrl = new StringBuilder(redirectUri);
        redirectUrl.append(redirectUri.contains("?") ? "&" : "?");
        redirectUrl.append("error=").append(error);
        redirectUrl.append("&error_description=").append(description);
        if (state != null) {
            redirectUrl.append("&state=").append(state);
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl.toString())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return errorResponse("server_error", ex.getMessage());
    }*/
}
