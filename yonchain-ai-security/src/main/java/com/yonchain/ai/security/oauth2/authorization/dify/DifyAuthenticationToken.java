package com.yonchain.ai.security.oauth2.authorization.dify;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2 钉钉模式认证令牌
 *
 * @since 1.0
 */
public class DifyAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String token;

    private final Set<String> scopes;

    /**
     * 构造函数
     *
     * @param authorizationGrantType 授权类型
     * @param clientPrincipal        客户端认证
     * @param token                  刷新token
     * @param scopes                 作用域
     * @param additionalParameters   额外参数
     */
    public DifyAuthenticationToken(
            AuthorizationGrantType authorizationGrantType,
            Authentication clientPrincipal,
            String token,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
        Assert.hasText(token, "code cannot be empty");
        this.token = token;
        this.scopes = Collections.unmodifiableSet(
                scopes != null ? scopes : Collections.emptySet());
    }

    /**
     * 获取刷新token
     *
     * @return 刷新token
     */
    public String getToken() {
        return this.token;
    }

    /**
     * 获取作用域
     *
     * @return 作用域集合
     */
    public Set<String> getScopes() {
        return this.scopes;
    }
}
