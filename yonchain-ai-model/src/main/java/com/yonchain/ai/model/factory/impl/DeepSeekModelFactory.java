package com.yonchain.ai.model.factory.impl;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.optionshandler.OptionsHandler;
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
        // DeepSeek目前只支持聊天模型
        return "chat".equalsIgnoreCase(modelType);
    }
    
    @Override
    public ChatModel createChatModel(ModelDefinition definition) {
        // 创建DeepSeek API实例
        DeepSeekApi deepSeekApi = createDeepSeekApi(definition);
        
/*        // 使用ModelDefinition内部的OptionsHandler解析选项
        DeepSeekChatOptions options = null;
        OptionsHandler<DeepSeekChatOptions> handler = definition.resolveOptionsHandler();
        
        if (handler != null) {
            try {
                options = handler.buildOptions(definition.getOptions());
                System.out.println("DEBUG: Successfully built options using handler for " + definition.getFullId());
            } catch (Exception e) {
                System.err.println("ERROR: Failed to build options using handler for " + definition.getFullId() + ", error: " + e.getMessage());
                options = DeepSeekChatOptions.builder()
                        .model(definition.getId())
                        .build();
            }
        } else {
            System.out.println("DEBUG: No handler found for " + definition.getFullId() + ", using default options");
            options = DeepSeekChatOptions.builder()
                    .model(definition.getId())
                    .build();
        }*/

        // 创建聊天选项
        DeepSeekChatOptions.Builder optionsBuilder = DeepSeekChatOptions.builder()
                .model(definition.getId());
        DeepSeekChatOptions options = optionsBuilder.build();

        // 创建聊天模型
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(options)
                .build();
    }
    
    @Override
    public ImageModel createImageModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("DeepSeek does not support image generation models");
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelDefinition definition) {
        // DeepSeek暂时不支持嵌入模型
        throw new UnsupportedOperationException("DeepSeek does not support embedding models yet");
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
