package com.yonchain.ai.embedding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.filter.BaseModelFilter;
import com.yonchain.ai.model.ModelClient;
import com.yonchain.ai.model.request.EmbeddingRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 嵌入模型专用过滤器
 * 处理嵌入请求 /embeddings
 */
public class EmbeddingModelFilter extends BaseModelFilter {
    
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile(".*/embeddings$");
    
    public EmbeddingModelFilter(ModelClient modelClient, ObjectMapper objectMapper) {
        super(modelClient, objectMapper);
    }
    
    @Override
    protected Pattern getEndpointPattern() {
        return ENDPOINT_PATTERN;
    }
    
    @Override
    protected String getModelType() {
        return "embedding";
    }
    
    @Override
    protected void handleModelRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // 1. 解析请求
            Map<String, Object> requestParams = parseRequestBody(request);
            String modelName = extractModelName(requestParams);
            
            logger.debug("Processing embedding request for model: {}", modelName);
            
            // 2. 转换为EmbeddingRequest
            EmbeddingRequest embeddingRequest = convertToEmbeddingRequest(requestParams);
            
            // 3. 使用ModelClient调用
            EmbeddingResponse embeddingResponse = modelClient.embedding(modelName, embeddingRequest);
            
            // 4. 转换为OpenAI格式
            Map<String, Object> responseData = convertEmbeddingResponse(embeddingResponse);
            
            // 5. 发送响应
            sendSuccessResponse(response, responseData);
            
            logger.debug("Embedding request completed successfully");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid embedding request: {}", e.getMessage());
            sendErrorResponse(response, e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("Error in embedding request", e);
            sendErrorResponse(response, "Failed to process embedding: " + e.getMessage(), 500);
        }
    }
    
    /**
     * 转换为EmbeddingRequest
     */
    private EmbeddingRequest convertToEmbeddingRequest(Map<String, Object> requestParams) {
        Object input = requestParams.get("input");
        if (input == null) {
            throw new IllegalArgumentException("Missing required parameter: input");
        }
        
        EmbeddingRequest.Builder builder = EmbeddingRequest.builder();
        
        // 处理输入（可以是字符串或字符串数组）
        if (input instanceof String) {
            builder.text((String) input);
        } else if (input instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> texts = (List<String>) input;
            if (texts.isEmpty()) {
                throw new IllegalArgumentException("Input list cannot be empty");
            }
            builder.texts(texts);
        } else {
            throw new IllegalArgumentException("Input must be a string or array of strings");
        }
        
        // 处理可选参数
        if (requestParams.containsKey("encoding_format")) {
            String encodingFormat = (String) requestParams.get("encoding_format");
            if (!"float".equals(encodingFormat) && !"base64".equals(encodingFormat)) {
                throw new IllegalArgumentException("Invalid encoding_format. Must be 'float' or 'base64'");
            }
            // 注意：EmbeddingRequest.Builder 可能需要添加 encodingFormat() 方法
        }
        
        if (requestParams.containsKey("dimensions")) {
            Object dimensionsObj = requestParams.get("dimensions");
            if (dimensionsObj instanceof Number) {
                int dimensions = ((Number) dimensionsObj).intValue();
                if (dimensions <= 0) {
                    throw new IllegalArgumentException("Dimensions must be a positive integer");
                }
                // 注意：EmbeddingRequest.Builder 可能需要添加 dimensions() 方法
            }
        }
        
        if (requestParams.containsKey("user")) {
            String user = (String) requestParams.get("user");
            // 注意：EmbeddingRequest.Builder 可能需要添加 user() 方法
        }
        
        return builder.build();
    }
    
    /**
     * 转换EmbeddingResponse为OpenAI格式
     */
    private Map<String, Object> convertEmbeddingResponse(EmbeddingResponse embeddingResponse) {
        Map<String, Object> response = new HashMap<>();
        response.put("object", "list");
        response.put("model", "unknown");
        
        List<Map<String, Object>> data = new ArrayList<>();
        
        if (embeddingResponse.getResult() != null && embeddingResponse.getResult().getOutput() != null) {
            Map<String, Object> embeddingData = new HashMap<>();
            embeddingData.put("object", "embedding");
            embeddingData.put("index", 0);
            
            // 获取嵌入向量
            float[] embedding = embeddingResponse.getResult().getOutput();
            
            // 转换为List<Double>（OpenAI格式）
            List<Double> embeddingList = new ArrayList<>();
            for (float value : embedding) {
                embeddingList.add((double) value);
            }
            embeddingData.put("embedding", embeddingList);
            
            data.add(embeddingData);
        }
        
        response.put("data", data);
        
        // 使用信息
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", calculateTokens(embeddingResponse));
        usage.put("total_tokens", calculateTokens(embeddingResponse));
        response.put("usage", usage);
        
        return response;
    }
    
    /**
     * 计算token数量（简单估算）
     */
    private int calculateTokens(EmbeddingResponse embeddingResponse) {
        // 这里可以根据实际需要实现token计算逻辑
        // 简单估算：假设每个字符约0.75个token
        try {
            // 从metadata中获取token信息，如果有的话
            // return embeddingResponse.getMetadata().getUsage().getTotalTokens();
            return 0; // 占位符
        } catch (Exception e) {
            return 0;
        }
    }
}
