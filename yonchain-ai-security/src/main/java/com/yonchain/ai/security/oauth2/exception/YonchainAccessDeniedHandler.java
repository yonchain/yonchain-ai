package com.yonchain.ai.security.oauth2.exception;

import com.yonchain.ai.web.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 自定义访问拒绝处理器
 * 处理已认证但权限不足的请求，返回统一的错误格式
 */
public class YonchainAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public YonchainAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorResponse errorResponse = new ApiErrorResponse(status.value(),
                "access_denied",
                accessDeniedException.getMessage() != null ?
                        accessDeniedException.getMessage() : "权限不足，无法访问请求的资源");
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
