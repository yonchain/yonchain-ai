package com.yonchain.ai.provider.impl;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.provider.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * DeepSeek模型提供商实现
 * 支持DeepSeek的聊天和嵌入模型
 */
@Component
@ConditionalOnProperty(name = "yonchain.ai.providers.deepseek.enabled", havingValue = "true", matchIfMissing = true)
public class DeepSeekModelProvider implements ModelProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekModelProvider.class);
    
    @Override
    public String getProviderName() {
        return "deepseek";
    }
    
    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.TEXT || modelType == ModelType.EMBEDDING;
    }
    
    @Override
    public ChatModel createChatModel(ModelConfig config) {
        logger.debug("Creating DeepSeek chat model: {}", config.getName());
        
        try {
            // 这里需要根据实际的Spring AI DeepSeek实现来创建模型
            // 由于Spring AI可能还没有官方的DeepSeek实现，这里提供一个示例结构
            
            // 示例代码结构（需要根据实际的Spring AI实现调整）:

            DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                    .apiKey(config.getApiKey())
                    .baseUrl(config.getEndpoint())
                    .build();
                    
            DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                    .model(config.getProperty("model", config.getName()))
                    .temperature(config.getTemperature())
                    .maxTokens(config.getMaxTokens())
                    .build();
                    
            return DeepSeekChatModel.builder()
                    .deepSeekApi(deepSeekApi)
                    .defaultOptions(options)
                    .build();

            
            // 临时返回null，实际使用时需要替换为真实实现
           /* throw new UnsupportedOperationException(
                "DeepSeek ChatModel implementation not available. " +
                "Please add the appropriate Spring AI DeepSeek dependency and implementation.");*/
            
        } catch (Exception e) {
            logger.error("Failed to create DeepSeek chat model: {}", config.getName(), e);
            throw new RuntimeException("Failed to create DeepSeek chat model: " + config.getName(), e);
        }
    }
    
    @Override
    public ImageModel createImageModel(ModelConfig config) {
        // DeepSeek目前不支持图像模型
        throw new UnsupportedOperationException("DeepSeek does not support image models");
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelConfig config) {
        logger.debug("Creating DeepSeek embedding model: {}", config.getName());
        
        try {
            // 示例代码结构（需要根据实际的Spring AI实现调整）:
            /*
            DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                    .apiKey(config.getApiKey())
                    .baseUrl(config.getEndpoint())
                    .build();
                    
            DeepSeekEmbeddingOptions options = DeepSeekEmbeddingOptions.builder()
                    .model(config.getProperty("model", config.getName()))
                    .build();
                    
            return DeepSeekEmbeddingModel.builder()
                    .deepSeekApi(deepSeekApi)
                    .defaultOptions(options)
                    .build();
            */
            
            throw new UnsupportedOperationException(
                "DeepSeek EmbeddingModel implementation not available. " +
                "Please add the appropriate Spring AI DeepSeek dependency and implementation.");
                
        } catch (Exception e) {
            logger.error("Failed to create DeepSeek embedding model: {}", config.getName(), e);
            throw new RuntimeException("Failed to create DeepSeek embedding model: " + config.getName(), e);
        }
    }
    
    @Override
    public boolean validateConfig(ModelConfig config) {
        if (config == null) {
            logger.warn("DeepSeek model config is null");
            return false;
        }
        
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            logger.warn("DeepSeek API key is missing for model: {}", config.getName());
            return false;
        }
        
        if (config.getEndpoint() == null || config.getEndpoint().trim().isEmpty()) {
            logger.warn("DeepSeek endpoint is missing for model: {}", config.getName());
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean testConnection(ModelConfig config) {
        logger.debug("Testing DeepSeek connection for model: {}", config.getName());
        
        if (!validateConfig(config)) {
            return false;
        }
        
        try {
            // 这里可以实现实际的连接测试逻辑
            // 例如发送一个简单的请求来验证API密钥和端点是否有效
            
            logger.info("DeepSeek connection test passed for model: {}", config.getName());
            return true;
            
        } catch (Exception e) {
            logger.error("DeepSeek connection test failed for model: {}", config.getName(), e);
            return false;
        }
    }
}
