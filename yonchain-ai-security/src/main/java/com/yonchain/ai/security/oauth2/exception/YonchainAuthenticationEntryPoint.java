package com.yonchain.ai.security.oauth2.exception;

import com.yonchain.ai.web.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 自定义认证入口点
 * 处理未认证的请求，返回统一的错误格式
 */
public class YonchainAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(YonchainAuthenticationEntryPoint.class);
    
    private final ObjectMapper objectMapper;

    public YonchainAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * 处理认证异常
     * @param request HTTP请求
     * @param response HTTP响应
     * @param authException 认证异常
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 记录认证失败的日志
        String requestUri = request.getRequestURI();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        log.debug("认证失败 - URI: {}, IP: {}, User-Agent: {}", requestUri, clientIp, userAgent);
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String errorCode = "";
        String msg = "";
        //JWT验证异常
        if (authException.getCause() instanceof JwtValidationException jwtValidationException) {
            if (jwtValidationException.getErrors().stream()
                    .anyMatch(error -> error.getDescription() != null && 
                             (error.getDescription().contains("Jwt expired") || 
                              error.getDescription().contains("expired")))) {
                errorCode = "token_expired";
                msg = "Token已过期";
                log.info("JWT Token已过期 - {}", jwtValidationException.getMessage());
            } else if (jwtValidationException.getErrors().stream()
                    .anyMatch(error -> error.getDescription() != null && 
                             (error.getDescription().contains("refresh_token") || 
                              error.getDescription().contains("Refresh token")))) {
                errorCode = "invalid_refresh_token";
                msg = "无效的Refresh Token，请重新登录获取新Token";
                log.warn("无效的Refresh Token - {}", jwtValidationException.getMessage());
            } else {
                errorCode = "invalid_token";
                msg = "无效的Token，请检查Token格式或重新登录";
                log.warn("无效的JWT Token - {}", jwtValidationException.getMessage());
            }
        } 
        // 处理刷新token异常
        else if (authException.getMessage() != null && 
                (authException.getMessage().contains("refresh_token") || 
                 authException.getMessage().contains("Refresh token"))) {
            errorCode = "invalid_refresh_token";
            msg = "无效的Refresh Token，请重新登录获取新Token";
            log.warn("无效的Refresh Token - {}", authException.getMessage());
        }
        // 处理OAuth2认证异常
        else if (authException instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
            OAuth2Error error = oAuth2AuthenticationException.getError();
            errorCode =  error.getErrorCode();
            if (StringUtils.hasText(error.getDescription())) {
                msg =  error.getDescription();
            }
            log.info("OAuth2认证异常 - 错误码: {}, 描述: {}", errorCode, msg);
        }else {
            errorCode = "unauthorized";
            msg = authException.getMessage();
            log.warn("未授权访问 - 异常类型: {}, 消息: {}", authException.getClass().getName(), msg);
        }

        ApiErrorResponse errorResponse = new ApiErrorResponse(status.value(), errorCode, msg);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
        
        // 记录完整的错误响应
        log.debug("返回认证错误响应: {}", errorResponse);
    }
}
