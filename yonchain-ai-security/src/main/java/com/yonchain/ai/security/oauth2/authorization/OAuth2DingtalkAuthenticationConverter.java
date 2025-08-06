package com.yonchain.ai.security.oauth2.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ERROR_URI;

/**
 * OAuth2密码认证转换器
 *
 * <p>
 * 实现AuthenticationConverter接口，用于将HTTP请求转换为OAuth2用户名密码认证令牌</br>
 * </p>
 * 主要功能：</br>
 * 1. 验证授权类型是否为password模式</br>
 * 2. 提取用户名和密码等必需参数</br>
 * 3. 处理scope参数</br>
 * 4. 构建包含额外参数的认证令牌</br>
 * <p>
 * 异常情况：
 * 当用户名或密码为空时抛出OAuth2AuthenticationException
 *
 * @author Cgy
 * @since 1.0.0
 */
public class OAuth2DingtalkAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        // 授权类型必须是password
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!OAuth2AuthorizationGrantTypes.DINGTALK.getValue().equals(grantType)) {
            //为空则不会走密码模式授权
            return null;
        }

        // 从请求中提取参数
        MultiValueMap<String, String> parameters = getParameters(request);

        // 验证必需参数
        String code = parameters.getFirst(OAuth2ParameterNames.CODE);
        if (!StringUtils.hasText(code))  {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    "钉钉授权码不能为空",
                    ERROR_URI));
        }

        // 提取作用域
        Set<String> scopes = new HashSet<>();
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope)) {
            scopes.addAll(Arrays.asList(scope.split(" ")));
        }

        // 获取当前客户端认证
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        // 构建认证令牌
        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(OAuth2ParameterNames.CODE) &&
                    !key.equals(OAuth2ParameterNames.SCOPE)) {
                additionalParameters.put(key, value.get(0));
            }
        });

        return new OAuth2DingtalkAuthenticationToken(
                OAuth2AuthorizationGrantTypes.DINGTALK,
                clientPrincipal,
                code,
                scopes,
                additionalParameters);
    }

    private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        return parameters;
    }
}
