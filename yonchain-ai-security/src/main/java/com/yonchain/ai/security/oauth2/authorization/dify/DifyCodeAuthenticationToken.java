package com.yonchain.ai.security.oauth2.authorization.dify;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DifyCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final String code;
    private final OAuth2ClientAuthenticationToken clientPrincipal;
    private final Map<String, Object> additionalParameters;

    // 用于认证前的构造方法
    public DifyCodeAuthenticationToken(
            String code,
            OAuth2ClientAuthenticationToken clientPrincipal,
            Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        this.code = code;
        this.clientPrincipal = clientPrincipal;
        this.additionalParameters = additionalParameters;
        setAuthenticated(false);
    }

    // 用于认证后的构造方法
    public DifyCodeAuthenticationToken(
            String code,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.code = code;
        this.clientPrincipal = null;
        this.additionalParameters = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return clientPrincipal;
    }

    public String getCode() {
        return this.code;
    }

    public OAuth2ClientAuthenticationToken getClientPrincipal() {
        return this.clientPrincipal;
    }

    public Map<String, Object> getAdditionalParameters() {
        return this.additionalParameters;
    }
}
