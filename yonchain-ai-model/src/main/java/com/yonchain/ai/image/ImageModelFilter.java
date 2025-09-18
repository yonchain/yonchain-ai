package com.yonchain.ai.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.filter.BaseModelFilter;
import com.yonchain.ai.model.ModelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 图像模型过滤器
 * 处理图像生成请求 /images/generations
 */
@Component
public class ImageModelFilter extends BaseModelFilter<ImageModel> {
    
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile(".*/images/generations$");
    
    private final ImageModelService imageModelService;
    
    public ImageModelFilter(ImageModelService imageModelService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.imageModelService = imageModelService;
    }
    
    @Override
    protected Pattern getEndpointPattern() {
        return ENDPOINT_PATTERN;
    }
    
    @Override
    protected ModelService<ImageModel> getModelService() {
        return imageModelService;
    }
    
    @Override
    protected void handleModelRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // 1. 解析图像生成请求
            Map<String, Object> requestParams = parseRequestBody(request);
            String modelName = extractModelName(requestParams);
            
            logger.debug("Processing image generation request for model: {}", modelName);
            
            // 2. 获取模型实例
            ImageModel imageModel = imageModelService.getModel(modelName);
            if (imageModel == null) {
                sendErrorResponse(response, "Model not found: " + modelName, 404);
                return;
            }
            
            // 3. 构建图像提示
            ImagePrompt prompt = buildImagePrompt(requestParams);
            
            // 4. 调用模型生成图像
            ImageResponse imageResponse = imageModel.call(prompt);
            
            // 5. 转换响应格式
            Map<String, Object> responseData = convertImageResponse(imageResponse);
            
            // 6. 发送响应
            sendSuccessResponse(response, responseData);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            sendErrorResponse(response, e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("Error processing image generation request", e);
            sendErrorResponse(response, "Failed to generate image: " + e.getMessage(), 500);
        }
    }
    
    /**
     * 构建图像提示
     * 
     * @param requestParams 请求参数
     * @return 图像提示
     */
    private ImagePrompt buildImagePrompt(Map<String, Object> requestParams) {
        // 获取提示文本
        Object promptObj = requestParams.get("prompt");
        if (promptObj == null) {
            throw new IllegalArgumentException("Missing required parameter: prompt");
        }
        String promptText = promptObj.toString();
        
        // 构建图像提示选项
        // 这里可以根据需要添加更多的选项，如尺寸、数量等
        
        return new ImagePrompt(promptText);
    }
    
    /**
     * 转换图像响应为OpenAI格式
     * 
     * @param imageResponse Spring AI图像响应
     * @return OpenAI格式的响应数据
     */
    private Map<String, Object> convertImageResponse(ImageResponse imageResponse) {
        Map<String, Object> response = new HashMap<>();
        
        // 设置基本信息
        response.put("created", System.currentTimeMillis() / 1000);
        
        // 转换图像数据
        List<Map<String, Object>> data = new ArrayList<>();
        if (imageResponse.getResult() != null && imageResponse.getResult().getOutput() != null) {
            Map<String, Object> imageData = new HashMap<>();
            
            // 获取图像URL或base64数据
            String imageUrl = imageResponse.getResult().getOutput().getUrl();
            if (imageUrl != null) {
                imageData.put("url", imageUrl);
            }
            
            // 如果有base64数据
            // String base64 = imageResponse.getResult().getOutput().getB64Json();
            // if (base64 != null) {
            //     imageData.put("b64_json", base64);
            // }
            
            data.add(imageData);
        }
        
        response.put("data", data);
        
        return response;
    }
}