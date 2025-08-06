package com.yonchain.ai.security.oauth2.authorization;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Dify4j OAuth2 授权认证类
 * <p>
 * 该类继承自 AbstractAuthenticationToken，实现了 OAuth2 授权认证的核心逻辑
 * 提供了两种构造方式：
 * 1. 未认证构造器 - 用于初始化未认证的令牌
 * 2. 已认证构造器 - 用于初始化已认证的令牌（包含权限集合）
 * <p>
 * 主要功能：
 * - 管理认证主体(principal)和凭证(credentials)
 * - 提供认证状态管理
 * - 实现凭证擦除功能
 * <p>
 * 注意事项：
 * - 认证状态一旦设置为true就不能再修改
 * - 必须通过已认证构造器来创建已认证的令牌
 */
public class YonchainOAuth2AuthorizationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String userId;

    private Object credentials;

    public YonchainOAuth2AuthorizationToken(String userId, Object credentials) {
        super(null);
        this.userId = userId;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public YonchainOAuth2AuthorizationToken(String userId, Object credentials,
                                            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }


    public static YonchainOAuth2AuthorizationToken unauthenticated(String userId, Object credentials) {
        return new YonchainOAuth2AuthorizationToken(userId, credentials);
    }


    public static YonchainOAuth2AuthorizationToken authenticated(String userId, Object credentials,
                                                                 Collection<? extends GrantedAuthority> authorities) {
        return new YonchainOAuth2AuthorizationToken(userId, credentials, authorities);
    }

    public static YonchainOAuth2AuthorizationToken authenticated(String userId,
                                                                 Collection<? extends GrantedAuthority> authorities) {
        return new YonchainOAuth2AuthorizationToken(userId, null, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

}
