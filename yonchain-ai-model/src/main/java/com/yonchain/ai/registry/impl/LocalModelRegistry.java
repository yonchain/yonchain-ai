package com.yonchain.ai.registry.impl;

import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.registry.ModelChangeListener;
import com.yonchain.ai.registry.ModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 本地内存模型注册表
 * 适用于单机部署或开发环境
 */
@Component
@ConditionalOnProperty(name = "yonchain.ai.registry.type", havingValue = "local", matchIfMissing = true)
public class LocalModelRegistry implements ModelRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalModelRegistry.class);
    
    private final Map<String, ModelMetadata> models = new ConcurrentHashMap<>();
    private final List<ModelChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void registerModel(ModelMetadata metadata) {
        if (metadata == null || metadata.getName() == null) {
            logger.warn("Cannot register null metadata or metadata with null name");
            return;
        }
        
        ModelMetadata old = models.put(metadata.getName(), metadata);
        
        // 通知监听器
        if (old == null) {
            logger.info("Registered new model: {}", metadata.getName());
            notifyModelRegistered(metadata);
        } else {
            logger.info("Updated model: {}", metadata.getName());
            notifyModelUpdated(metadata);
        }
    }
    
    @Override
    public void unregisterModel(String modelName) {
        if (modelName == null) {
            logger.warn("Cannot unregister model with null name");
            return;
        }
        
        ModelMetadata removed = models.remove(modelName);
        if (removed != null) {
            logger.info("Unregistered model: {}", modelName);
            notifyModelUnregistered(modelName);
        }
    }
    
    @Override
    public Optional<ModelMetadata> getModelMetadata(String modelName) {
        if (modelName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(models.get(modelName));
    }
    
    @Override
    public List<ModelMetadata> getModelsByType(ModelType modelType) {
        if (modelType == null) {
            return Collections.emptyList();
        }
        
        return models.values().stream()
                .filter(m -> modelType.equals(m.getType()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ModelMetadata> getAllModels() {
        return new ArrayList<>(models.values());
    }
    
    @Override
    public boolean containsModel(String modelName) {
        return modelName != null && models.containsKey(modelName);
    }
    
    @Override
    public boolean isModelAvailable(String modelName) {
        ModelMetadata metadata = models.get(modelName);
        return metadata != null && Boolean.TRUE.equals(metadata.getAvailable());
    }
    
    @Override
    public void refresh() {
        logger.info("Refreshing local model registry, current models: {}", models.size());
        // 本地注册表的刷新逻辑
        // 可以重新从配置文件加载
    }
    
    @Override
    public void addModelChangeListener(ModelChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
            logger.debug("Added model change listener: {}", listener.getClass().getSimpleName());
        }
    }
    
    /**
     * 移除模型变化监听器
     * 
     * @param listener 监听器
     */
    public void removeModelChangeListener(ModelChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            logger.debug("Removed model change listener: {}", listener.getClass().getSimpleName());
        }
    }
    
    /**
     * 获取当前注册的模型数量
     * 
     * @return 模型数量
     */
    public int getModelCount() {
        return models.size();
    }
    
    /**
     * 清空所有模型
     */
    public void clear() {
        logger.info("Clearing all models from registry");
        models.clear();
    }
    
    private void notifyModelRegistered(ModelMetadata metadata) {
        listeners.forEach(listener -> {
            try {
                listener.onModelRegistered(metadata);
            } catch (Exception e) {
                logger.error("Error notifying model registered event", e);
            }
        });
    }
    
    private void notifyModelUnregistered(String modelName) {
        listeners.forEach(listener -> {
            try {
                listener.onModelUnregistered(modelName);
            } catch (Exception e) {
                logger.error("Error notifying model unregistered event", e);
            }
        });
    }
    
    private void notifyModelUpdated(ModelMetadata metadata) {
        listeners.forEach(listener -> {
            try {
                listener.onModelUpdated(metadata);
            } catch (Exception e) {
                logger.error("Error notifying model updated event", e);
            }
        });
    }
}
