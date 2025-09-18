package com.yonchain.ai.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.model.ModelService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.Model;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 模型过滤器基类
 * 提供通用的请求处理逻辑
 * 
 * @param <T> 模型类型
 */
public abstract class BaseModelFilter<T extends Model> extends OncePerRequestFilter {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper;
    
    public BaseModelFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    protected final void doFilterInternal(HttpServletRequest request, 
                                         HttpServletResponse response, 
                                         FilterChain filterChain) throws ServletException, IOException {
        
        // 检查是否匹配当前过滤器的端点模式
        if (!matchesEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 只处理POST请求
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 执行具体的模型请求处理
            handleModelRequest(request, response);
        } catch (Exception e) {
            logger.error("Error processing model request", e);
            sendErrorResponse(response, "Internal server error: " + e.getMessage(), 500);
        }
    }
    
    /**
     * 检查请求URI是否匹配当前过滤器
     * 
     * @param requestURI 请求URI
     * @return 是否匹配
     */
    protected boolean matchesEndpoint(String requestURI) {
        return getEndpointPattern().matcher(requestURI).matches();
    }
    
    /**
     * 获取当前过滤器处理的端点正则模式
     * 
     * @return 端点正则模式
     */
    protected abstract Pattern getEndpointPattern();
    
    /**
     * 获取模型服务实例
     * 
     * @return 模型服务
     */
    protected abstract ModelService<T> getModelService();
    
    /**
     * 处理具体的模型请求
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    protected abstract void handleModelRequest(HttpServletRequest request, 
                                             HttpServletResponse response) throws IOException;
    
    /**
     * 发送错误响应
     * 
     * @param response HTTP响应
     * @param errorMessage 错误消息
     * @param statusCode 状态码
     * @throws IOException IO异常
     */
    protected void sendErrorResponse(HttpServletResponse response, 
                                   String errorMessage, 
                                   int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, "invalid_request_error");
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
    
    /**
     * 发送成功响应
     * 
     * @param response HTTP响应
     * @param responseData 响应数据
     * @throws IOException IO异常
     */
    protected void sendSuccessResponse(HttpServletResponse response, Object responseData) throws IOException {
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = objectMapper.writeValueAsString(responseData);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
    
    /**
     * 错误响应类
     */
    public static class ErrorResponse {
        private final Error error;
        
        public ErrorResponse(String message, String type) {
            this.error = new Error(message, type);
        }
        
        public Error getError() {
            return error;
        }
        
        public static class Error {
            private final String message;
            private final String type;
            
            public Error(String message, String type) {
                this.message = message;
                this.type = type;
            }
            
            public String getMessage() { 
                return message; 
            }
            
            public String getType() { 
                return type; 
            }
        }
    }
    
    /**
     * 解析请求体为Map
     * 
     * @param request HTTP请求
     * @return 请求参数Map
     * @throws IOException IO异常
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> parseRequestBody(HttpServletRequest request) throws IOException {
        try {
            return objectMapper.readValue(request.getInputStream(), Map.class);
        } catch (Exception e) {
            logger.error("Failed to parse request body", e);
            throw new IOException("Invalid JSON in request body", e);
        }
    }
    
    /**
     * 从请求参数中获取模型名称
     * 
     * @param requestParams 请求参数
     * @return 模型名称
     */
    protected String extractModelName(Map<String, Object> requestParams) {
        Object model = requestParams.get("model");
        if (model == null) {
            throw new IllegalArgumentException("Missing required parameter: model");
        }
        return model.toString();
    }
    
    /**
     * 检查请求是否为流式请求
     * 
     * @param requestParams 请求参数
     * @return 是否为流式请求
     */
    protected boolean isStreamRequest(Map<String, Object> requestParams) {
        Object stream = requestParams.get("stream");
        return Boolean.TRUE.equals(stream);
    }
}
