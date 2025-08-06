package com.yonchain.ai.security.oauth2.authorization.dify;


import com.yonchain.ai.security.oauth2.authorization.OAuth2AuthorizationGrantTypes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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
public class DifyAuthenticationProvider implements AuthenticationProvider {

    private final Log logger = LogFactory.getLog(getClass());

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final UserDetailsService userDetailsService;

    private final OAuth2AuthorizationService authorizationService;

    private final JwtDecoder jwtDecoder;

    private final DifyJwtTokenGenerator tokenGenerator;

    public DifyAuthenticationProvider(UserDetailsService userDetailsService,
                                      OAuth2AuthorizationService authorizationService,
                                      JwtDecoder jwtDecoder,
                                      String secretKey) {
        this.userDetailsService = userDetailsService;
        this.authorizationService = authorizationService;
        this.jwtDecoder = jwtDecoder;
        tokenGenerator = new DifyJwtTokenGenerator(secretKey);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DifyAuthenticationToken difyAuthenticationToken = (DifyAuthenticationToken) authentication;

        // 获取OAuth2客户端认证信息
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(difyAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 验证客户端是否支持钉钉授权码授权类型
        if (!registeredClient.getAuthorizationGrantTypes().contains(OAuth2AuthorizationGrantTypes.DIFY)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT,
                    "客户端不支持该授权类型", ERROR_URI));
        }

        // 获取token
        String token = difyAuthenticationToken.getToken();

        //校验token,获取用户id
        Jwt jwt = this.getJwt(token);
        Map<String, Object> claims = jwt.getClaims();
        String userId = (String) claims.get("user_id");


        // 3. 生成访问令牌
        OAuth2AccessToken accessToken = tokenGenerator.generateAccessToken(userId);

        // 4. 生成刷新令牌
        OAuth2RefreshToken refreshToken = tokenGenerator.generateRefreshToken(userId);

        // 保存授权信息
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(userId)
                .authorizationGrantType(OAuth2AuthorizationGrantTypes.DINGTALK)
                .attribute(Principal.class.getName(), UsernamePasswordAuthenticationToken.authenticated(userId, null,null));
        //authorizationBuilder.accessToken(accessToken);
        authorizationBuilder.token(accessToken, (metadata) ->
                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claims));
        authorizationBuilder.refreshToken(refreshToken);
        OAuth2Authorization authorization = authorizationBuilder.build();
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
        return DifyAuthenticationToken.class.isAssignableFrom(authentication);
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

    private Jwt getJwt(String token) {
        try {
            return this.jwtDecoder.decode(token);
        }
        catch (BadJwtException failed) {
            this.logger.debug("Failed to authenticate since the JWT was invalid");
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    failed.getMessage(), ERROR_URI),failed);
        }
        catch (JwtException failed) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    failed.getMessage(), ERROR_URI),failed);
        }
    }
}
