/*
package com.yonchain.ai.image.impl;

import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.image.ImageModelService;
import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.registry.ModelChangeListener;
import com.yonchain.ai.model.registry.ModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * 默认图像模型服务实现
 * 专注于图像模型调用
 *//*

@Service
public class DefaultImageModelService implements ImageModelService {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultImageModelService.class);
    
    private final Map<String, ImageModel> modelCache = new ConcurrentHashMap<>();
    private final ModelRegistry modelRegistry;
    private final ModelFactory modelFactory;
    
    public DefaultImageModelService(ModelRegistry modelRegistry, ModelFactory modelFactory) {
        this.modelRegistry = modelRegistry;
        this.modelFactory = modelFactory;
        
        // 监听模型变化，清理缓存
        modelRegistry.addModelChangeListener(new ModelChangeListener() {
            @Override
            public void onModelRegistered(ModelMetadata metadata) {
                logger.info("Image model registered: {}", metadata.getName());
            }
            
            @Override
            public void onModelUnregistered(String modelName) {
                logger.info("Image model unregistered, removing from cache: {}", modelName);
                modelCache.remove(modelName);
            }
            
            @Override
            public void onModelUpdated(ModelMetadata metadata) {
                logger.info("Image model updated, removing from cache: {}", metadata.getName());
                modelCache.remove(metadata.getName());
            }
            
            @Override
            public void onModelAvailabilityChanged(String modelName, boolean available) {
                logger.info("Image model availability changed: {} -> {}", modelName, available);
                if (!available) {
                    modelCache.remove(modelName);
                }
            }
        });
        
        logger.info("DefaultImageModelService initialized");
    }
    
    @Override
    public ImageModel getModel(String modelName) {
        if (modelName == null) {
            throw new IllegalArgumentException("Model name cannot be null");
        }
        
        return modelCache.computeIfAbsent(modelName, this::createModel);
    }
    
    */
/**
     * 创建图像模型实例
     * 
     * @param modelName 模型名称
     * @return 图像模型实例
     *//*

    private ImageModel createModel(String modelName) {
        logger.debug("Creating image model: {}", modelName);
        
        // 1. 从注册中心获取模型元数据
        Optional<ModelMetadata> metadataOpt = modelRegistry.getModelMetadata(modelName);
        if (metadataOpt.isEmpty()) {
            throw new IllegalArgumentException("Image model not found: " + modelName);
        }
        
        ModelMetadata metadata = metadataOpt.get();
        if (!Boolean.TRUE.equals(metadata.getAvailable())) {
            throw new IllegalStateException("Image model is not available: " + modelName);
        }
        
        // 2. 获取模型配置
        ModelConfig config = metadata.getConfig();
        if (config == null) {
            throw new IllegalStateException("Image model config is null for: " + modelName);
        }
        
        try {
            // 3. 通过工厂创建模型实例
            ImageModel imageModel = modelFactory.createImageModel(config);
            logger.info("Successfully created image model: {}", modelName);
            return imageModel;
        } catch (Exception e) {
            logger.error("Failed to create image model: " + modelName, e);
            throw new RuntimeException("Failed to create image model: " + modelName, e);
        }
    }
    
    */
/**
     * 获取所有可用的图像模型名称
     * 
     * @return 模型名称集合
     *//*

    public Set<String> getAvailableModels() {
        return modelCache.keySet();
    }
    
    */
/**
     * 检查图像模型是否可用
     * 
     * @param modelName 模型名称
     * @return 是否可用
     *//*

    public boolean isModelAvailable(String modelName) {
        return modelRegistry.isModelAvailable(modelName);
    }
    
    */
/**
     * 清理模型缓存
     * 
     * @param modelName 模型名称，如果为null则清理所有缓存
     *//*

    public void clearCache(String modelName) {
        if (modelName == null) {
            logger.info("Clearing all image model cache");
            modelCache.clear();
        } else {
            logger.info("Clearing image model cache: {}", modelName);
            modelCache.remove(modelName);
        }
    }
    
    */
/**
     * 获取缓存的模型数量
     * 
     * @return 缓存的模型数量
     *//*

    public int getCachedModelCount() {
        return modelCache.size();
    }
    
    */
/**
     * 预热指定模型（提前创建并缓存）
     * 
     * @param modelName 模型名称
     *//*

    public void warmupModel(String modelName) {
        try {
            logger.info("Warming up image model: {}", modelName);
            getModel(modelName);
            logger.info("Successfully warmed up image model: {}", modelName);
        } catch (Exception e) {
            logger.error("Failed to warm up image model: " + modelName, e);
        }
    }
}
*/
