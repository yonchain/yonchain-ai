package com.yonchain.ai.model.factory.impl;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.registry.TypeHandlerRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

/**
 * DeepSeek命名空间工厂实现
 * 使用OpenAI兼容的API接口
 */
@Component
public class DeepSeekNamespaceFactory implements ModelFactory {
    
    @Override
    public String namespace() {
        return "deepseek";
    }
    
    @Override
    public boolean supports(String modelType) {
        return "chat".equalsIgnoreCase(modelType) || 
               "embedding".equalsIgnoreCase(modelType);
    }
    
    @Override
    public ChatModel createChatModel(ModelDefinition definition, TypeHandlerRegistry typeHandlerRegistry) {
        // DeepSeek使用OpenAI兼容的API
        OpenAiApi deepSeekApi = createDeepSeekApi(definition);
        return null;//new OpenAiChatModel(deepSeekApi);
    }
    
    @Override
    public ImageModel createImageModel(ModelDefinition definition, TypeHandlerRegistry typeHandlerRegistry) {
        throw new UnsupportedOperationException("DeepSeek does not support image generation models");
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelDefinition definition, TypeHandlerRegistry typeHandlerRegistry) {
        // DeepSeek使用OpenAI兼容的API
        OpenAiApi deepSeekApi = createDeepSeekApi(definition);
        return new OpenAiEmbeddingModel(deepSeekApi);
    }
    
    /**
     * 创建DeepSeek API实例（OpenAI兼容）
     */
    private OpenAiApi createDeepSeekApi(ModelDefinition definition) {
        String baseUrl = definition.getEndpoint();
        String apiKey = definition.getAuthValue();
        
        if (baseUrl == null) {
            baseUrl = "https://api.deepseek.com/v1";
        }
        
        if (apiKey == null) {
            throw new IllegalArgumentException("DeepSeek API key is required");
        }
        
        return null;//new OpenAiApi(baseUrl, apiKey);
    }
}
