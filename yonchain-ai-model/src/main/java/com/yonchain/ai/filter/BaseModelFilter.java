package com.yonchain.ai.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.model.core.ModelClient;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 基础模型过滤器抽象类
 * 
 * 提供通用的过滤器功能，子类只需实现特定的模型处理逻辑
 */
public abstract class BaseModelFilter implements Filter {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final ModelClient modelClient;
    protected final ObjectMapper objectMapper;
    
    public BaseModelFilter(ModelClient modelClient, ObjectMapper objectMapper) {
        this.modelClient = modelClient;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // 检查是否为当前过滤器处理的请求
        if (!"POST".equals(method) || !shouldHandle(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        try {
            logger.debug("Processing {} request: {}", getModelType(), requestURI);
            handleModelRequest(httpRequest, httpResponse);
        } catch (Exception e) {
            logger.error("Error processing {} request: {}", getModelType(), requestURI, e);
            sendErrorResponse(httpResponse, "Internal server error: " + e.getMessage(), 500);
        }
    }
    
    /**
     * 判断是否应该处理此请求
     */
    protected boolean shouldHandle(String requestURI) {
        return getEndpointPattern().matcher(requestURI).find();
    }
    
    /**
     * 获取端点模式（子类实现）
     */
    protected abstract Pattern getEndpointPattern();
    
    /**
     * 获取模型类型名称（用于日志）
     */
    protected abstract String getModelType();
    
    /**
     * 处理具体的模型请求（子类实现）
     */
    protected abstract void handleModelRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException;
    
    /**
     * 解析请求体
     */
    protected Map<String, Object> parseRequestBody(HttpServletRequest request) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);
            return requestMap;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON request body", e);
        }
    }
    
    /**
     * 提取模型名称
     */
    protected String extractModelName(Map<String, Object> requestParams) {
        Object model = requestParams.get("model");
        if (model == null) {
            throw new IllegalArgumentException("Missing required parameter: model");
        }
        return model.toString();
    }
    
    /**
     * 判断是否为流式请求
     */
    protected boolean isStreamRequest(Map<String, Object> requestParams) {
        Object stream = requestParams.get("stream");
        return stream instanceof Boolean && (Boolean) stream;
    }
    
    /**
     * 发送成功响应
     */
    protected void sendSuccessResponse(HttpServletResponse response, Object responseData) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = objectMapper.writeValueAsString(responseData);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
    
    /**
     * 发送错误响应
     */
    protected void sendErrorResponse(HttpServletResponse response, String message, int status) 
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("type", "error");
        error.put("code", status);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}