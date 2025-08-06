package com.yonchain.ai.security.oauth2.authorization.dify;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ERROR_URI;

public class DifyCodeAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final OAuth2AuthorizationService authorizationService;
    private final DifyJwtTokenGenerator tokenGenerator;

    public DifyCodeAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                          UserDetailsService userDetailsService,
                                          String secretKey) {
        this.authorizationService = authorizationService;
        this.userDetailsService = userDetailsService;
        tokenGenerator = new DifyJwtTokenGenerator(secretKey);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DifyCodeAuthenticationToken codeAuthenticationToken = (DifyCodeAuthenticationToken) authentication;

        // 获取OAuth2客户端认证信息
        OAuth2ClientAuthenticationToken clientPrincipal =
                getAuthenticatedClientElseThrowInvalidClient(codeAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 1. 验证授权码格式
        String code = codeAuthenticationToken.getCode();
        if (!StringUtils.hasText(code)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_GRANT,
                    "授权码不能为空",
                    ERROR_URI));
        }

        // 2. 解析授权码获取用户信息
        // 通过授权码查找授权信息
        OAuth2Authorization authorization = authorizationService.findByToken(
                code,
                new OAuth2TokenType("code"));
        if (authentication == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_GRANT,
                    "授权码不合法",
                    ERROR_URI));
        }

        /*UserDetails userDetails = userDetailsService.loadUserByUsername("{userId}" + authorization.getPrincipalName());
        if (userDetails == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_GRANT,
                    "用户不存在",
                    ERROR_URI));
        }*/

        String userId = authorization.getPrincipalName();

        // 3. 生成访问令牌
        OAuth2AccessToken accessToken = tokenGenerator.generateAccessToken(userId);

        // 4. 生成刷新令牌
        OAuth2RefreshToken refreshToken = tokenGenerator.generateRefreshToken(userId);

        // 保存授权信息
/*
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(userDetails.getUsername())
                .authorizationGrantType(OAuth2AuthorizationGrantTypes.PASSWORD)
                .attribute(Principal.class.getName(), codeAuthenticationToken);

        authorizationBuilder.accessToken(accessToken);
        if (refreshToken != null) {
            authorizationBuilder.refreshToken(refreshToken);
        }
        this.authorizationService.save(authorizationBuilder.build());
*/


        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization);
        authorizationBuilder.token(accessToken);
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        authorizationBuilder.invalidate(authorizationCode.getToken());

        authorization = authorizationBuilder.build();

        this.authorizationService.save(authorization);

        // 3. 返回认证成功的Token
        // 构建额外参数
        Map<String, Object> additionalParameters = new HashMap<>();
        if (refreshToken != null) {
            additionalParameters.put(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        }

        // 返回访问令牌认证结果
        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DifyCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        Class clazz = authentication.getPrincipal().getClass();
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }
}
