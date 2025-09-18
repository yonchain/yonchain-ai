package com.yonchain.ai.provider.impl;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.provider.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collections;

/**
 * 模拟聊天模型提供商
 * 用于测试和演示目的
 */
@Component
@ConditionalOnProperty(name = "yonchain.ai.providers.mock.enabled", havingValue = "true", matchIfMissing = false)
public class MockChatModelProvider implements ModelProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(MockChatModelProvider.class);
    
    @Override
    public String getProviderName() {
        return "mock";
    }
    
    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.TEXT;
    }
    
    @Override
    public ChatModel createChatModel(ModelConfig config) {
        logger.debug("Creating mock chat model: {}", config.getName());
        return new MockChatModel(config);
    }
    
    @Override
    public ImageModel createImageModel(ModelConfig config) {
        throw new UnsupportedOperationException("Mock provider does not support image models");
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelConfig config) {
        throw new UnsupportedOperationException("Mock provider does not support embedding models");
    }
    
    @Override
    public boolean validateConfig(ModelConfig config) {
        return config != null && config.getName() != null;
    }
    
    @Override
    public boolean testConnection(ModelConfig config) {
        logger.debug("Mock connection test always passes for model: {}", config.getName());
        return true;
    }
    
    /**
     * 模拟聊天模型实现
     */
    private static class MockChatModel implements ChatModel {
        
        private final ModelConfig config;
        
        public MockChatModel(ModelConfig config) {
            this.config = config;
        }
        
        @Override
        public ChatResponse call(Prompt prompt) {
            String responseText = "Mock response from " + config.getName() + 
                " for prompt: " + prompt.getContents();
                
            AssistantMessage message = new AssistantMessage(responseText);
            Generation generation = new Generation(message);
            
            return new ChatResponse(Collections.singletonList(generation));
        }
        
        @Override
        public Flux<ChatResponse> stream(Prompt prompt) {
            // 模拟流式响应
            String[] words = ("Mock streaming response from " + config.getName()).split(" ");
            
            return Flux.fromArray(words)
                .map(word -> {
                    AssistantMessage message = new AssistantMessage(word + " ");
                    Generation generation = new Generation(message);
                    return new ChatResponse(Collections.singletonList(generation));
                });
        }
    }
}
