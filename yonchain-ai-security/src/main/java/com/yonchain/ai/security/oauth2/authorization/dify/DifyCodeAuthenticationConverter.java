package com.yonchain.ai.security.oauth2.authorization.dify;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DifyCodeAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        // 1. 验证grant_type
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"dify_authorization_code".equals(grantType)) {
            return null;
        }

        // 2. 获取客户端认证
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                OAuth2ErrorCodes.INVALID_CLIENT,
                "Client authentication required",
                null
            ));
        }

        // 3. 验证授权码
        String code = request.getParameter("code");
        if (!StringUtils.hasText(code)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                OAuth2ErrorCodes.INVALID_REQUEST,
                "Custom authorization code required",
                null
            ));
        }

        // 4. 创建认证Token
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("code", code);

        return new DifyCodeAuthenticationToken(code,
            (OAuth2ClientAuthenticationToken) clientPrincipal,
            additionalParameters
        );
    }
}
