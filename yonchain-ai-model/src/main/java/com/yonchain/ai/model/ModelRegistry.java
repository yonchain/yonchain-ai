package com.yonchain.ai.model;

import com.yonchain.ai.model.definition.ModelDefinition;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 模型注册中心
 * 
 * 负责管理所有模型定义的注册和查找
 */
public class ModelRegistry {
    
    // namespace:modelId -> ModelDefinition
    private final Map<String, ModelDefinition> models = new ConcurrentHashMap<>();
    
    /**
     * 注册模型定义
     * 
     * @param definition 模型定义
     */
    public void registerModel(ModelDefinition definition) {
        String key = buildKey(definition.getNamespace(), definition.getId());
        models.put(key, definition);
    }
    
    /**
     * 获取模型定义
     * 
     * @param namespace 命名空间
     * @param modelId 模型ID
     * @return 模型定义
     */
    public Optional<ModelDefinition> getModelDefinition(String namespace, String modelId) {
        String key = buildKey(namespace, modelId);
        return Optional.ofNullable(models.get(key));
    }
    
    /**
     * 获取指定命名空间下的所有模型
     * 
     * @param namespace 命名空间
     * @return 模型定义列表
     */
    public List<ModelDefinition> getModelsByNamespace(String namespace) {
        return models.values().stream()
            .filter(model -> namespace.equals(model.getNamespace()))
            .toList();
    }
    
    /**
     * 获取指定类型的所有模型
     * 
     * @param modelType 模型类型
     * @return 模型定义列表
     */
    public List<ModelDefinition> getModelsByType(String modelType) {
        return models.values().stream()
            .filter(model -> modelType.equals(model.getType()))
            .toList();
    }
    
    /**
     * 获取所有模型定义
     * 
     * @return 所有模型定义列表
     */
    public List<ModelDefinition> getAllModels() {
        return new ArrayList<>(models.values());
    }
    
    /**
     * 检查模型是否存在
     * 
     * @param namespace 命名空间
     * @param modelId 模型ID
     * @return 是否存在
     */
    public boolean containsModel(String namespace, String modelId) {
        String key = buildKey(namespace, modelId);
        return models.containsKey(key);
    }
    
    /**
     * 移除模型定义
     * 
     * @param namespace 命名空间
     * @param modelId 模型ID
     * @return 被移除的模型定义
     */
    public Optional<ModelDefinition> removeModel(String namespace, String modelId) {
        String key = buildKey(namespace, modelId);
        return Optional.ofNullable(models.remove(key));
    }
    
    /**
     * 清空所有模型定义
     */
    public void clear() {
        models.clear();
    }
    
    /**
     * 获取模型数量
     * 
     * @return 模型数量
     */
    public int size() {
        return models.size();
    }
    
    private String buildKey(String namespace, String modelId) {
        return namespace + ":" + modelId;
    }
}