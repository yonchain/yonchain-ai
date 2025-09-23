/*
package com.yonchain.ai.model.factory.impl;

import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.model.provider.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.ai.audio.transcription.AudioTranscriptionModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * 默认模型工厂实现
 * 负责根据配置创建不同类型的模型实例
 *//*

@Component
public class DefaultModelFactory implements ModelFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultModelFactory.class);
    
    private final Map<String, ModelProvider> providers = new ConcurrentHashMap<>();
    
    public DefaultModelFactory(List<ModelProvider> providerList) {
        // 注册所有可用的模型提供商
        if (providerList != null) {
            for (ModelProvider provider : providerList) {
                registerProvider(provider.getProviderName(), provider);
            }
        }
        logger.info("DefaultModelFactory initialized with {} providers", providers.size());
    }
    
    @Override
    public ChatModel createChatModel(ModelConfig config) {
        validateConfig(config, ModelType.TEXT);
        
        ModelProvider provider = getProvider(config.getProvider());
        if (!provider.supports(ModelType.TEXT)) {
            throw new IllegalArgumentException("Provider " + config.getProvider() + 
                " does not support TEXT model type");
        }
        
        logger.debug("Creating chat model: {} with provider: {}", config.getName(), config.getProvider());
        return provider.createChatModel(config);
    }
    
    @Override
    public ImageModel createImageModel(ModelConfig config) {
        validateConfig(config, ModelType.IMAGE);
        
        ModelProvider provider = getProvider(config.getProvider());
        if (!provider.supports(ModelType.IMAGE)) {
            throw new IllegalArgumentException("Provider " + config.getProvider() + 
                " does not support IMAGE model type");
        }
        
        logger.debug("Creating image model: {} with provider: {}", config.getName(), config.getProvider());
        return provider.createImageModel(config);
    }
    
    @Override
    public Object createAudioModel(ModelConfig config) {
        validateConfig(config, ModelType.AUDIO);
        
        ModelProvider provider = getProvider(config.getProvider());
        if (!provider.supports(ModelType.AUDIO)) {
            throw new IllegalArgumentException("Provider " + config.getProvider() + 
                " does not support AUDIO model type");
        }
        
        logger.debug("Creating audio model: {} with provider: {}", config.getName(), config.getProvider());
        return provider.createAudioModel(config);
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelConfig config) {
        validateConfig(config, ModelType.EMBEDDING);
        
        ModelProvider provider = getProvider(config.getProvider());
        if (!provider.supports(ModelType.EMBEDDING)) {
            throw new IllegalArgumentException("Provider " + config.getProvider() + 
                " does not support EMBEDDING model type");
        }
        
        logger.debug("Creating embedding model: {} with provider: {}", config.getName(), config.getProvider());
        return provider.createEmbeddingModel(config);
    }
    
    @Override
    public boolean supports(ModelType modelType) {
        if (modelType == null) {
            return false;
        }
        
        return providers.values().stream()
                .anyMatch(provider -> provider.supports(modelType));
    }
    
    @Override
    public boolean supportsProvider(String provider) {
        return provider != null && providers.containsKey(provider);
    }
    
    @Override
    public void registerProvider(String providerName, ModelProvider modelProvider) {
        if (providerName == null || modelProvider == null) {
            logger.warn("Cannot register null provider or provider with null name");
            return;
        }
        
        providers.put(providerName, modelProvider);
        logger.info("Registered model provider: {}", providerName);
    }
    
    @Override
    public void unregisterProvider(String providerName) {
        if (providerName == null) {
            logger.warn("Cannot unregister provider with null name");
            return;
        }
        
        ModelProvider removedProvider = providers.remove(providerName);
        if (removedProvider != null) {
            logger.info("Unregistered model provider: {}", providerName);
        } else {
            logger.warn("Provider not found for unregistration: {}", providerName);
        }
    }
    
    @Override
    public Set<String> getSupportedProviders() {
        return new HashSet<>(providers.keySet());
    }
    
    @Override
    public Set<ModelType> getSupportedModelTypes() {
        Set<ModelType> supportedTypes = EnumSet.noneOf(ModelType.class);
        
        for (ModelProvider provider : providers.values()) {
            for (ModelType type : ModelType.values()) {
                if (provider.supports(type)) {
                    supportedTypes.add(type);
                }
            }
        }
        
        return supportedTypes;
    }
    
    */
/**
     * 获取提供商实例
     * 
     * @param providerName 提供商名称
     * @return 提供商实例
     *//*

    private ModelProvider getProvider(String providerName) {
        if (providerName == null) {
            throw new IllegalArgumentException("Provider name cannot be null");
        }
        
        ModelProvider provider = providers.get(providerName);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerName + 
                ". Available providers: " + providers.keySet());
        }
        
        return provider;
    }
    
    */
/**
     * 验证模型配置
     * 
     * @param config 模型配置
     * @param expectedType 期望的模型类型
     *//*

    private void validateConfig(ModelConfig config, ModelType expectedType) {
        if (config == null) {
            throw new IllegalArgumentException("Model config cannot be null");
        }
        
        if (config.getName() == null) {
            throw new IllegalArgumentException("Model name cannot be null");
        }
        
        if (config.getProvider() == null) {
            throw new IllegalArgumentException("Model provider cannot be null");
        }
        
        if (config.getType() != null && config.getType() != expectedType) {
            logger.warn("Model type mismatch: expected {}, but config has {}", 
                expectedType, config.getType());
        }
        
        // 设置正确的模型类型
        config.setType(expectedType);
        
        // 验证提供商配置
        ModelProvider provider = getProvider(config.getProvider());
        if (!provider.validateConfig(config)) {
            throw new IllegalArgumentException("Invalid config for provider: " + config.getProvider());
        }
    }
    
    */
/**
     * 获取提供商数量
     * 
     * @return 提供商数量
     *//*

    public int getProviderCount() {
        return providers.size();
    }
}
*/
