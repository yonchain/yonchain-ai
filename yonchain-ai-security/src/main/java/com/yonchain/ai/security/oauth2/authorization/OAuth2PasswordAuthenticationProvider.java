package com.yonchain.ai.security.oauth2.authorization;

import com.yonchain.ai.api.security.SecurityEventPublisher;
import com.yonchain.ai.security.crypto.PasswordEncoderType;
import com.yonchain.ai.security.crypto.PasswordUtil;
import com.yonchain.ai.security.user.Dify4jUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2密码认证提供者
 *
 * <p>
 * 该认证提供者用于处理OAuth2密码授权模式的认证流程。
 * </p>
 * 主要功能包括：</br>
 * 1. 验证客户端是否支持密码授权模式 </br>
 * 2. 验证用户凭据（用户名和密码） </br>
 * 3. 生成访问令牌和刷新令牌 </br>
 * 4. 保存授权信息到OAuth2授权服务 </br>
 * <p>
 * 使用场景：适用于需要用户名密码直接获取访问令牌的OAuth2授权流程
 *
 * @author Cgy
 * @since 1.0.0
 */
public class OAuth2PasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final UserDetailsService userDetailsService;

    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    private final PasswordEncoder passwordEncoder;

    // 配置密码编码器类型，默认为Dify的PBKDF2加密器
    private String passwordEncoderType = PasswordEncoderType.DIFY_PBKDF2;

    // 是否启用密码编码器前缀，默认为true
    private boolean enabledPasswordEncoderPrefix = true;

    public OAuth2PasswordAuthenticationProvider(UserDetailsService userDetailsService,
                                                OAuth2AuthorizationService authorizationService,
                                                OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                                PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2PasswordAuthenticationToken passwordAuthenticationToken = (OAuth2PasswordAuthenticationToken) authentication;

        // 获取OAuth2客户端认证信息
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(passwordAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 验证客户端是否支持密码授权类型
        if (!registeredClient.getAuthorizationGrantTypes().contains(OAuth2AuthorizationGrantTypes.PASSWORD)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, "客户端不支持密码授权类型", ERROR_URI));
        }

        // 验证授权范围
        Set<String> authorizedScopes = registeredClient.getScopes();
        if (!authorizedScopes.containsAll(passwordAuthenticationToken.getScopes())) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE, "无效的授权范围", ERROR_URI));
        }

        // 获取用户名和密码
        String username = passwordAuthenticationToken.getUsername();
        String password = passwordAuthenticationToken.getPassword();

        // 验证用户凭据
        Dify4jUser userDetails = (Dify4jUser) userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "用户名或密码错误", ERROR_URI));
        }

        String encodedPassword = this.getUserPassword(userDetails);

        // 验证密码
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "用户名或密码错误", ERROR_URI));
        }

        if (!userDetails.isEnabled()) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "用户账户已禁用", ERROR_URI));
        }

        // 创建用户认证令牌
        YonchainOAuth2AuthorizationToken dify4jAuthorization = YonchainOAuth2AuthorizationToken.authenticated(userDetails.getUserId(), userDetails.getAuthorities());

        // 准备令牌上下文
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(dify4jAuthorization)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(passwordAuthenticationToken.getScopes())
                .authorizationGrantType(OAuth2AuthorizationGrantTypes.PASSWORD)
                .authorizationGrant(passwordAuthenticationToken);

        // 生成访问令牌
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "生成访问令牌失败", ERROR_URI));
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(),
                generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(),
                tokenContext.getAuthorizedScopes());

        // 生成刷新令牌
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (generatedRefreshToken == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "生成刷新令牌失败", ERROR_URI));
            }
            refreshToken = new OAuth2RefreshToken(generatedRefreshToken.getTokenValue(), generatedRefreshToken.getIssuedAt(), generatedRefreshToken.getExpiresAt());
        }

        // 保存授权信息
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(userDetails.getUsername())
                .authorizationGrantType(OAuth2AuthorizationGrantTypes.PASSWORD)
                .authorizedScopes(tokenContext.getAuthorizedScopes())
                .attribute(Principal.class.getName(), dify4jAuthorization);
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }
        if (refreshToken != null) {
            authorizationBuilder.refreshToken(refreshToken);
        }
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        // 构建额外参数
        Map<String, Object> additionalParameters = new HashMap<>();
        if (refreshToken != null) {
            additionalParameters.put(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        }


        // 发布用户登录事件
       SecurityEventPublisher.publishUserLoginEvent(userDetails.getUserId(),this.getClientIp());
        
        // 返回访问令牌认证结果
        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * 获取用户密码
     *
     * <p>根据安全配置处理用户密码，包括：</p>
     * 1. 如果使用Dify规范的PBKDF2加密器，将密码和盐值进行编码拼接</br>
     * 2. 根据配置决定是否为密码添加加密器类型前缀</br>
     *
     * @param userDetails 用户详情对象
     * @return 处理后的密码字符串
     */
    private String getUserPassword(Dify4jUser userDetails) {
        String password = userDetails.getPassword();
        //判断是否使用dify规范的密码加密器
        if (PasswordEncoderType.DIFY_PBKDF2.equals(passwordEncoderType)) {
            // 按照dify密码校验规范，校验时需通过密码和盐值进行校验，同时为了规范使用Spring passwordEncoder,
            // 因此需编码将密码和盐值进行拼接,校验时再解码获取密码和盐值
            password = PasswordUtil.encode(userDetails.getPassword(), userDetails.getPasswordSalt());
        }
        // 根据安全配置判断是否需要为密码添加前缀
        if (enabledPasswordEncoderPrefix) {
            password = "{" + passwordEncoderType + "}" + password;
        }
        return password;
    }


    /**
     * 设置密码编码器类型
     *
     * @param passwordEncoderType 密码编码器类型
     */
    public void setPasswordEncoderType(String passwordEncoderType) {
        this.passwordEncoderType = passwordEncoderType;
    }

    /**
     * 设置是否启用密码编码器前缀
     *
     * @param enabledPasswordEncoderPrefix 是否启用
     */
    public void setEnabledPasswordEncoderPrefix(boolean enabledPasswordEncoderPrefix) {
        this.enabledPasswordEncoderPrefix = enabledPasswordEncoderPrefix;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        String UNKNOWN_IP = "unknown";
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
