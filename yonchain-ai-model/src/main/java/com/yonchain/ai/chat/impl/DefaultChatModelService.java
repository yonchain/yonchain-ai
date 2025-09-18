package com.yonchain.ai.chat.impl;

import com.yonchain.ai.chat.ChatModelService;
import com.yonchain.ai.factory.ModelFactory;
import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.registry.ModelChangeListener;
import com.yonchain.ai.registry.ModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认聊天模型服务实现
 * 专注于模型调用，通过注册中心获取元数据，通过工厂创建模型实例
 */
@Service
public class DefaultChatModelService implements ChatModelService {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultChatModelService.class);
    
    private final Map<String, ChatModel> modelCache = new ConcurrentHashMap<>();
    private final ModelRegistry modelRegistry;
    private final ModelFactory modelFactory;
    
    public DefaultChatModelService(ModelRegistry modelRegistry, ModelFactory modelFactory) {
        this.modelRegistry = modelRegistry;
        this.modelFactory = modelFactory;
        
        // 监听模型变化，清理缓存
        modelRegistry.addModelChangeListener(new ModelChangeListener() {
            @Override
            public void onModelRegistered(ModelMetadata metadata) {
                logger.info("Model registered: {}", metadata.getName());
            }
            
            @Override
            public void onModelUnregistered(String modelName) {
                logger.info("Model unregistered, removing from cache: {}", modelName);
                modelCache.remove(modelName);
            }
            
            @Override
            public void onModelUpdated(ModelMetadata metadata) {
                logger.info("Model updated, removing from cache: {}", metadata.getName());
                modelCache.remove(metadata.getName()); // 强制重新创建
            }
            
            @Override
            public void onModelAvailabilityChanged(String modelName, boolean available) {
                logger.info("Model availability changed: {} -> {}", modelName, available);
                if (!available) {
                    modelCache.remove(modelName); // 移除不可用的模型
                }
            }
        });
        
        logger.info("DefaultChatModelService initialized");
    }
    
    @Override
    public ChatModel getModel(String modelName) {
        if (modelName == null) {
            throw new IllegalArgumentException("Model name cannot be null");
        }
        
        return modelCache.computeIfAbsent(modelName, this::createModel);
    }
    
    /**
     * 创建模型实例
     * 
     * @param modelName 模型名称
     * @return 聊天模型实例
     */
    private ChatModel createModel(String modelName) {
        logger.debug("Creating chat model: {}", modelName);
        
        // 1. 从注册中心获取模型元数据
        Optional<ModelMetadata> metadataOpt = modelRegistry.getModelMetadata(modelName);
        if (metadataOpt.isEmpty()) {
            throw new IllegalArgumentException("Model not found: " + modelName);
        }
        
        ModelMetadata metadata = metadataOpt.get();
        if (!Boolean.TRUE.equals(metadata.getAvailable())) {
            throw new IllegalStateException("Model is not available: " + modelName);
        }
        
        // 2. 获取模型配置
        ModelConfig config = metadata.getConfig();
        if (config == null) {
            throw new IllegalStateException("Model config is null for: " + modelName);
        }
        
        try {
            // 3. 通过工厂创建模型实例
            ChatModel chatModel = modelFactory.createChatModel(config);
            logger.info("Successfully created chat model: {}", modelName);
            return chatModel;
        } catch (Exception e) {
            logger.error("Failed to create chat model: " + modelName, e);
            throw new RuntimeException("Failed to create chat model: " + modelName, e);
        }
    }
    
    /**
     * 获取所有可用的聊天模型名称
     * 
     * @return 模型名称集合
     */
    public Set<String> getAvailableModels() {
        return modelCache.keySet();
    }
    
    /**
     * 检查模型是否可用
     * 
     * @param modelName 模型名称
     * @return 是否可用
     */
    public boolean isModelAvailable(String modelName) {
        return modelRegistry.isModelAvailable(modelName);
    }
    
    /**
     * 清理模型缓存
     * 
     * @param modelName 模型名称，如果为null则清理所有缓存
     */
    public void clearCache(String modelName) {
        if (modelName == null) {
            logger.info("Clearing all chat model cache");
            modelCache.clear();
        } else {
            logger.info("Clearing chat model cache: {}", modelName);
            modelCache.remove(modelName);
        }
    }
    
    /**
     * 获取缓存的模型数量
     * 
     * @return 缓存的模型数量
     */
    public int getCachedModelCount() {
        return modelCache.size();
    }
    
    /**
     * 预热指定模型（提前创建并缓存）
     * 
     * @param modelName 模型名称
     */
    public void warmupModel(String modelName) {
        try {
            logger.info("Warming up chat model: {}", modelName);
            getModel(modelName);
            logger.info("Successfully warmed up chat model: {}", modelName);
        } catch (Exception e) {
            logger.error("Failed to warm up chat model: " + modelName, e);
        }
    }
}
