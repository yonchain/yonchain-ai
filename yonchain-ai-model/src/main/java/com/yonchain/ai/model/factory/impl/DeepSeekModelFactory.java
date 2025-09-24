package com.yonchain.ai.model.factory.impl;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.registry.OptionsHandlerRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.stereotype.Component;

/**
 * DeepSeek命名空间工厂实现
 * 使用OpenAI兼容的API接口
 */
@Component
public class DeepSeekModelFactory implements ModelFactory {
    
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
    public ChatModel createChatModel(ModelDefinition definition, OptionsHandlerRegistry typeHandlerRegistry) {
        // DeepSeek使用OpenAI兼容的API
        DeepSeekApi deepSeekApi = createDeepSeekApi(definition);

        // 创建聊天选项
        DeepSeekChatOptions.Builder optionsBuilder = DeepSeekChatOptions.builder()
                .model(definition.getId());
        DeepSeekChatOptions options = optionsBuilder.build();

        // 创建聊天模型
        DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(options)
                .build();
        return chatModel;
    }
    
    @Override
    public ImageModel createImageModel(ModelDefinition definition, OptionsHandlerRegistry typeHandlerRegistry) {
        throw new UnsupportedOperationException("DeepSeek does not support image generation models");
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelDefinition definition, OptionsHandlerRegistry typeHandlerRegistry) {
        // DeepSeek使用OpenAI兼容的API
        DeepSeekApi deepSeekApi = createDeepSeekApi(definition);
        return null;//new OpenAiEmbeddingModel(deepSeekApi);
    }
    
    /**
     * 创建DeepSeek API实例（OpenAI兼容）
     */
    private DeepSeekApi createDeepSeekApi(ModelDefinition definition) {
        String baseUrl = definition.getBaseUrl();
        String apiKey = definition.getAuthValue();
        
        if (baseUrl == null) {
            baseUrl = "https://api.deepseek.com/v1";
        }
        
        if (apiKey == null) {
            throw new IllegalArgumentException("DeepSeek API key is required");
        }
        
        return DeepSeekApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }
}
