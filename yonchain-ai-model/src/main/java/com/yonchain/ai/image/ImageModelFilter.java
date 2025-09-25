package com.yonchain.ai.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.filter.BaseModelFilter;
import com.yonchain.ai.model.ModelClient;
import com.yonchain.ai.model.request.ImageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageResponse;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 图像模型专用过滤器
 * 处理图像生成请求 /images/generations
 */
public class ImageModelFilter extends BaseModelFilter {
    
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile(".*/images/generations$");
    
    public ImageModelFilter(ModelClient modelClient, ObjectMapper objectMapper) {
        super(modelClient, objectMapper);
    }
    
    @Override
    protected Pattern getEndpointPattern() {
        return ENDPOINT_PATTERN;
    }
    
    @Override
    protected String getModelType() {
        return "image";
    }
    
    @Override
    protected void handleModelRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // 1. 解析请求
            Map<String, Object> requestParams = parseRequestBody(request);
            String modelName = extractModelName(requestParams);
            
            logger.debug("Processing image generation request for model: {}", modelName);
            
            // 2. 转换为ImageRequest
            ImageRequest imageRequest = convertToImageRequest(requestParams);
            
            // 3. 使用ModelClient调用
            ImageResponse imageResponse = modelClient.generateImage(modelName, imageRequest);
            
            // 4. 转换为OpenAI格式
            Map<String, Object> responseData = convertImageResponse(imageResponse);
            
            // 5. 发送响应
            sendSuccessResponse(response, responseData);
            
            logger.debug("Image generation request completed successfully");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid image generation request: {}", e.getMessage());
            sendErrorResponse(response, e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("Error in image generation request", e);
            sendErrorResponse(response, "Failed to generate image: " + e.getMessage(), 500);
        }
    }
    
    /**
     * 转换为ImageRequest
     */
    private ImageRequest convertToImageRequest(Map<String, Object> requestParams) {
        Object promptObj = requestParams.get("prompt");
        if (promptObj == null) {
            throw new IllegalArgumentException("Missing required parameter: prompt");
        }
        
        ImageRequest.Builder builder = ImageRequest.builder()
            .prompt(promptObj.toString());
        
        // 处理可选参数
        if (requestParams.containsKey("size")) {
            builder.size((String) requestParams.get("size"));
        }
        if (requestParams.containsKey("quality")) {
            builder.quality((String) requestParams.get("quality"));
        }
        if (requestParams.containsKey("n")) {
            // 处理生成数量参数
            Object nObj = requestParams.get("n");
            if (nObj instanceof Number) {
                int n = ((Number) nObj).intValue();
                if (n < 1 || n > 10) {
                    throw new IllegalArgumentException("Parameter 'n' must be between 1 and 10");
                }
                // 注意：ImageRequest.Builder 可能需要添加 n() 方法
            }
        }
        if (requestParams.containsKey("response_format")) {
            String responseFormat = (String) requestParams.get("response_format");
            if (!"url".equals(responseFormat) && !"b64_json".equals(responseFormat)) {
                throw new IllegalArgumentException("Invalid response_format. Must be 'url' or 'b64_json'");
            }
            // 注意：ImageRequest.Builder 可能需要添加 responseFormat() 方法
        }
        
        return builder.build();
    }
    
    /**
     * 转换ImageResponse为OpenAI格式
     */
    private Map<String, Object> convertImageResponse(ImageResponse imageResponse) {
        Map<String, Object> response = new HashMap<>();
        response.put("created", System.currentTimeMillis() / 1000);
        
        List<Map<String, Object>> data = new ArrayList<>();
        
        if (imageResponse.getResult() != null && imageResponse.getResult().getOutput() != null) {
            Map<String, Object> imageData = new HashMap<>();
            
            // 获取图像URL
            String imageUrl = imageResponse.getResult().getOutput().getUrl();
            if (imageUrl != null) {
                imageData.put("url", imageUrl);
            }
            
            // 如果有base64数据（根据Spring AI的实际API调整）
            // String base64 = imageResponse.getResult().getOutput().getB64Json();
            // if (base64 != null) {
            //     imageData.put("b64_json", base64);
            // }
            
            // 添加修订版本信息（如果有）
            // String revisedPrompt = imageResponse.getResult().getMetadata().get("revised_prompt");
            // if (revisedPrompt != null) {
            //     imageData.put("revised_prompt", revisedPrompt);
            // }
            
            data.add(imageData);
        }
        
        response.put("data", data);
        
        return response;
    }
}
