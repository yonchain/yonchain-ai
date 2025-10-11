package com.yonchain.ai.plugin;

import com.yonchain.ai.model.ModelFactory;
import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.model.options.ModelOptionsHandler;
import com.yonchain.ai.business.ModelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;

/**
 * ModelProvider到ModelFactory的适配器
 * 
 * 解决ModelProvider和ModelFactory接口参数不一致的问题：
 * - ModelProvider.createChatModel(ModelConfig config)
 * - ModelFactory.createChatModel(ModelDefinition definition)
 * 
 * @author yonchain
 */
public class PluginModelFactory implements ModelFactory {
    
    private static final Logger log = LoggerFactory.getLogger(PluginModelFactory.class);
    
    private final ModelProvider modelProvider;
    private final String providerName;
    
    public PluginModelFactory(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
        this.providerName = modelProvider.getProviderName();
        log.debug("Created ProviderToFactoryAdapter for provider: {}", providerName);
    }
    
    @Override
    public boolean supports(ModelType modelType) {
        return modelProvider.supports(modelType);
    }
    
    @Override
    public ChatModel createChatModel(ModelDefinition definition) {
        if (!supports(ModelType.CHAT)) {
            throw new UnsupportedOperationException("Chat model not supported by provider: " + providerName);
        }
        
        log.debug("Creating chat model: {} using provider: {}", definition.getFullId(), providerName);
        
        try {
            // 将ModelDefinition转换为ModelConfig
            ModelConfig config = convertToModelConfig(definition);
            
            // 调用ModelProvider创建模型
            ChatModel chatModel = modelProvider.createChatModel(config);
            
            log.info("Successfully created chat model: {} with provider: {}", definition.getFullId(), providerName);
            return chatModel;
            
        } catch (Exception e) {
            log.error("Failed to create chat model: {} with provider: {}", definition.getFullId(), providerName, e);
            throw new RuntimeException("Failed to create chat model: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ImageModel createImageModel(ModelDefinition definition) {
        if (!supports(ModelType.IMAGE)) {
            throw new UnsupportedOperationException("Image model not supported by provider: " + providerName);
        }
        
        log.debug("Creating image model: {} using provider: {}", definition.getFullId(), providerName);
        
        try {
            ModelConfig config = convertToModelConfig(definition);
            ImageModel imageModel = modelProvider.createImageModel(config);
            
            log.info("Successfully created image model: {} with provider: {}", definition.getFullId(), providerName);
            return imageModel;
            
        } catch (Exception e) {
            log.error("Failed to create image model: {} with provider: {}", definition.getFullId(), providerName, e);
            throw new RuntimeException("Failed to create image model: " + e.getMessage(), e);
        }
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelDefinition definition) {
        if (!supports(ModelType.EMBEDDING)) {
            throw new UnsupportedOperationException("Embedding model not supported by provider: " + providerName);
        }
        
        log.debug("Creating embedding model: {} using provider: {}", definition.getFullId(), providerName);
        
        try {
            ModelConfig config = convertToModelConfig(definition);
            EmbeddingModel embeddingModel = modelProvider.createEmbeddingModel(config);
            
            log.info("Successfully created embedding model: {} with provider: {}", definition.getFullId(), providerName);
            return embeddingModel;
            
        } catch (Exception e) {
            log.error("Failed to create embedding model: {} with provider: {}", definition.getFullId(), providerName, e);
            throw new RuntimeException("Failed to create embedding model: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将ModelDefinition转换为ModelConfig
     * 
     * @param definition 模型定义
     * @return 模型配置
     */
    private ModelConfig convertToModelConfig(ModelDefinition definition) {
        ModelConfig config = new ModelConfig();
        
        // 基本信息
        config.setName(definition.getId());
        config.setProvider(definition.getNamespace());
        config.setType(convertModelType(definition.getType()));
        config.setEnabled(true);
        
        // 认证信息
        if (definition.getAuthValue() != null) {
            config.setApiKey(definition.getAuthValue());
        }
        
        // 端点信息
        if (definition.getBaseUrl() != null) {
            config.setEndpoint(definition.getBaseUrl());
        }
        
        // 路径信息
        if (definition.getCompletionsPath() != null) {
            config.setProperty("completionsPath", definition.getCompletionsPath());
        }
        
        // 默认配置
        config.setTimeout(30000); // 30秒超时
        config.setRetryCount(3);   // 重试3次
        
        // 选项信息 - 使用OptionsHandler构建SpringAI选项
        buildSpringAiOptions(definition, config);
        
        log.debug("Converted ModelDefinition to ModelConfig: {} -> {}", definition.getFullId(), config.getName());
        return config;
    }
    
    /**
     * 构建SpringAI选项对象
     * 
     * @param definition 模型定义
     * @param config 模型配置
     */
    private void buildSpringAiOptions(ModelDefinition definition, ModelConfig config) {
        try {
            if (definition.hasOptionsHandler(definition.getModelConfiguration())) {
                ModelOptionsHandler<?> handler = definition.resolveOptionsHandler();
                if (handler != null && definition.getOptions() != null) {
                    // 将选项Map转换为具体的SpringAI Options对象
                    Object springAiOptions = handler.buildOptions(definition.getOptions());
                    config.setProperty("springAiOptions", springAiOptions);
                    
                    log.debug("Built SpringAI options for model: {} using handler: {}", 
                            definition.getFullId(), handler.getClass().getSimpleName());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to build SpringAI options for model: {}, will use default options", 
                    definition.getFullId(), e);
        }
    }
    
    /**
     * 转换模型类型
     * 
     * @param type 字符串类型
     * @return ModelType枚举
     */
    private ModelType convertModelType(String type) {
        if (type == null) {
            return ModelType.CHAT;
        }
        
        switch (type.toLowerCase()) {
            case "chat":
            case "text":
                return ModelType.CHAT;
            case "image":
                return ModelType.IMAGE;
            case "embedding":
                return ModelType.EMBEDDING;
            default:
                log.warn("Unknown model type: {}, defaulting to CHAT", type);
                return ModelType.CHAT;
        }
    }
    
    /**
     * 获取适配的ModelProvider
     * 
     * @return ModelProvider实例
     */
    public ModelProvider getModelProvider() {
        return modelProvider;
    }
    
    /**
     * 获取提供商名称
     * 
     * @return 提供商名称
     */
    public String getProviderName() {
        return providerName;
    }
    
    @Override
    public String toString() {
        return "ProviderToFactoryAdapter{" +
                "providerName='" + providerName + '\'' +
                ", modelProvider=" + modelProvider.getClass().getSimpleName() +
                '}';
    }
}
