package com.yonchain.ai.plugin.deepseek;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.model.provider.ModelProvider;
import com.yonchain.ai.model.provider.ProviderMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;

/**
 * DeepSeek模型提供商实现
 * 
 * @author yonchain
 */
public class DeepSeekModelProvider implements ModelProvider {
    
    private static final Logger log = LoggerFactory.getLogger(DeepSeekModelProvider.class);
    
    private static final String PROVIDER_NAME = "deepseek";
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean supports(ModelType modelType) {
       return modelType == ModelType.TEXT || modelType == ModelType.EMBEDDING;
    }


    @Override
    public ChatModel createChatModel(ModelConfig modelConfig) {
        try {
            // 从环境变量或配置中获取API Key
            String apiKey = modelConfig.getApiKey();
            if (apiKey == null) {
                apiKey = System.getProperty("deepseek.api.key");
            }

            if (apiKey == null) {
                String errorMsg = "DEEPSEEK_API_KEY not found in environment variables or system properties";
                log.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }

            // 获取自定义的base URL（如果有）
            String baseUrl = System.getenv("DEEPSEEK_BASE_URL");
            if (baseUrl == null) {
                baseUrl = System.getProperty("deepseek.base.url");
                if (baseUrl == null) {
                    baseUrl = "https://api.deepseek.com/v1"; // 默认URL
                }
            }

            // 创建聊天选项
            DeepSeekChatOptions.Builder optionsBuilder = DeepSeekChatOptions.builder()
                    .model(modelConfig.getName());

            DeepSeekChatOptions options = optionsBuilder.build();

            // 创建DeepSeek API实例
            DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();

            // 创建聊天模型
            DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
                    .deepSeekApi(deepSeekApi)
                    .defaultOptions(options)
                    .build();

            log.info("Successfully created DeepSeek chat model: {}", modelConfig.getName());
            return chatModel;

        } catch (Exception e) {
            String errorMsg = String.format("Failed to create DeepSeek chat model: %s", modelConfig.getName());
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Model config cannot be null");
        }
        
        log.info("Creating DeepSeek embedding model: {}", config.getName());
        
        try {
            // DeepSeek目前主要提供对话模型，这里可以根据实际情况实现
            // 暂时抛出不支持异常
            throw new UnsupportedOperationException("DeepSeek embedding model not yet supported");
            
        } catch (Exception e) {
            log.error("Failed to create DeepSeek embedding model: {}", config.getName(), e);
            throw new RuntimeException("Failed to create DeepSeek embedding model: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ImageModel createImageModel(ModelConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Model config cannot be null");
        }
        
        log.info("Creating DeepSeek image model: {}", config.getName());
        
        try {
            // DeepSeek目前主要提供对话模型，这里可以根据实际情况实现
            // 暂时抛出不支持异常
            throw new UnsupportedOperationException("DeepSeek image model not yet supported");
            
        } catch (Exception e) {
            log.error("Failed to create DeepSeek image model: {}", config.getName(), e);
            throw new RuntimeException("Failed to create DeepSeek image model: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Object createAudioModel(ModelConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Model config cannot be null");
        }
        
        log.info("Creating DeepSeek audio model: {}", config.getName());
        
        try {
            // DeepSeek目前主要提供对话模型，这里可以根据实际情况实现
            // 暂时抛出不支持异常
            throw new UnsupportedOperationException("DeepSeek audio model not yet supported");
            
        } catch (Exception e) {
            log.error("Failed to create DeepSeek audio model: {}", config.getName(), e);
            throw new RuntimeException("Failed to create DeepSeek audio model: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查是否支持指定的模型类型
     * 
     * @param modelName 模型名称
     * @return 是否支持
     */
    public boolean supportsModel(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return false;
        }
        
        // DeepSeek支持的模型
        return modelName.startsWith("deepseek-chat") || 
               modelName.startsWith("deepseek-reasoner") || 
               modelName.startsWith("deepseek-coder");
    }
    
    /**
     * 获取默认的API基础URL
     * 
     * @return API基础URL
     */
    public String getDefaultApiUrl() {
        return "https://api.deepseek.com/v1";
    }
    
    /**
     * 验证配置是否有效
     * 
     * @param config 模型配置
     * @return 是否有效
     */
    public boolean validateConfig(ModelConfig config) {
        if (config == null) {
            return false;
        }
        
        // 检查必需的配置项
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            log.warn("DeepSeek model config missing API key");
            return false;
        }
        
        if (config.getName() == null || config.getName().trim().isEmpty()) {
            log.warn("DeepSeek model config missing model name");
            return false;
        }
        
        if (!supportsModel(config.getName())) {
            log.warn("Unsupported DeepSeek model: {}", config.getName());
            return false;
        }
        
        return true;
    }

    @Override
    public boolean testConnection(ModelConfig config) {
        return false;
    }

}

