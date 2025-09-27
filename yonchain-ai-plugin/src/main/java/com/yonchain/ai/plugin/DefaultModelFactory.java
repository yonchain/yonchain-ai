/*
package com.yonchain.ai.plugin;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.plugin.spi.ModelProvider;
import com.yonchain.ai.tmpl.ModelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultModelFactory implements PluginModelFactory {

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


    */
/**
     * 根据模型定义创建聊天模型
     *
     * @param definition 模型定义
     * @return ChatModel实例
     * @throws UnsupportedOperationException 如果不支持该类型
     *//*

    public ChatModel createChatModel(ModelDefinition definition) {
        ModelConfig config = this.buildConfig(definition);
        ModelProvider provider = getProvider(config.getProvider());
        if (!provider.supports(ModelType.CHAT)) {
            throw new IllegalArgumentException("Provider " + config.getProvider() +
                    " does not support TEXT model type");
        }

        logger.debug("Creating chat model: {} with provider: {}", config.getName(), config.getProvider());
        return provider.createChatModel(config);
    }

    private ModelConfig buildConfig(ModelDefinition definition) {
        ModelConfig modelConfig = new ModelConfig();
        modelConfig.setApiKey(definition.getAuthValue());
        modelConfig.setProvider(definition.getNamespace());
        //TODO
        return modelConfig;
    }

    */
/**
     * 根据模型定义创建图像模型
     *
     * @param definition 模型定义
     * @return ImageModel实例
     * @throws UnsupportedOperationException 如果不支持该类型
     *//*

    public ImageModel createImageModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Image model not supported by " + this.getClass().getSimpleName());
    }

    */
/**
     * 根据模型定义创建嵌入模型
     *
     * @param definition 模型定义
     * @return EmbeddingModel实例
     * @throws UnsupportedOperationException 如果不支持该类型
     *//*

    public EmbeddingModel createEmbeddingModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Embedding model not supported by " + this.getClass().getSimpleName());
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
    public boolean supports(ModelType modelType) {
        return false;
    }


    */
/**
     * 获取提供商实例
     *
     * @param providerName
     * @return
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
}
*/
