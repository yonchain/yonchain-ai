package com.yonchain.ai.security.oauth2.authorization;

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
public class OAuth2DingtalkAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String code;

    private final Set<String> scopes;

    /**
     * 构造函数
     *
     * @param authorizationGrantType 授权类型
     * @param clientPrincipal        客户端认证
     * @param code                   钉钉授权码
     * @param scopes                 作用域
     * @param additionalParameters   额外参数
     */
    public OAuth2DingtalkAuthenticationToken(
            AuthorizationGrantType authorizationGrantType,
            Authentication clientPrincipal,
            String code,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
        Assert.hasText(code, "code cannot be empty");
        this.code = code;
        this.scopes = Collections.unmodifiableSet(
                scopes != null ? scopes : Collections.emptySet());
    }

    /**
     * 获取授权码
     *
     * @return 授权码
     */
    public String getCode() {
        return this.code;
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
