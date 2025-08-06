package com.yonchain.ai.security.oauth2.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2 密码模式认证令牌
 * <p>
 * 该类实现了基于密码模式的OAuth2认证令牌，用于处理用户名密码方式的认证请求
 *
 * @see OAuth2AuthorizationGrantAuthenticationToken
 * @since 1.0
 */
public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final String password;
    private final Set<String> scopes;

    /**
     * 构造函数
     *
     * @param authorizationGrantType 授权类型
     * @param clientPrincipal        客户端认证
     * @param username               用户名
     * @param password               密码
     * @param scopes                 作用域
     * @param additionalParameters   额外参数
     */
    public OAuth2PasswordAuthenticationToken(
            AuthorizationGrantType authorizationGrantType,
            Authentication clientPrincipal,
            String username,
            String password,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
        Assert.hasText(username, "username cannot be empty");
        Assert.hasText(password, "password cannot be empty");
        this.username = username;
        this.password = password;
        this.scopes = Collections.unmodifiableSet(
                scopes != null ? scopes : Collections.emptySet());
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return this.password;
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
