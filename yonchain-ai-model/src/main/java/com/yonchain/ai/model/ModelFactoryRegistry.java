package com.yonchain.ai.model;

import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.enums.ModelType;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 模型工厂注册中心
 * 
 * 负责管理所有模型工厂的注册和查找
 */
public class ModelFactoryRegistry {
    
    // namespace -> ModelFactory
    private final Map<String, ModelFactory> factories = new ConcurrentHashMap<>();
    
    /**
     * 注册模型工厂
     * 
     * @param namespace 命名空间名称
     * @param factory 模型工厂
     */
    public void registerFactory(String namespace, ModelFactory factory) {
        factories.put(namespace, factory);
    }
    
    /**
     * 获取模型工厂
     * 
     * @param namespace 模型
     * @return 模型工厂
     */
    public Optional<ModelFactory> getFactory(String namespace) {
        return Optional.ofNullable(factories.get(namespace));
    }
    
    /**
     * 检查模型工厂是否存在
     * 
     * @param namespace 模型
     * @return 是否存在
     */
    public boolean containsFactory(String namespace) {
        return factories.containsKey(namespace);
    }
    
    /**
     * 移除模型工厂
     * 
     * @param namespace 模型
     * @return 被移除的工厂
     */
    public Optional<ModelFactory> removeFactory(String namespace) {
        return Optional.ofNullable(factories.remove(namespace));
    }
    
    /**
     * 获取所有注册的模型
     * 
     * @return 模型集合
     */
    public Set<String> getRegisteredNamespaces() {
        return factories.keySet();
    }
    
    /**
     * 清空所有工厂
     */
    public void clear() {
        factories.clear();
    }
    
    /**
     * 获取工厂数量
     * 
     * @return 工厂数量
     */
    public int size() {
        return factories.size();
    }
    
    // 移除了专门的工厂获取方法，统一使用getFactory()方法
    // 因为现在ModelFactory基类已经包含了所有创建方法
    
    /**
     * 检查指定命名空间是否支持指定类型的模型
     * 
     * @param namespace 命名空间
     * @param modelType 模型类型枚举
     * @return 是否支持
     */
    public boolean supportsModelType(String namespace, ModelType modelType) {
        return getFactory(namespace)
                .map(factory -> factory.supports(modelType))
                .orElse(false);
    }
    
    /**
     * 检查指定命名空间是否支持指定类型的模型（字符串版本）
     * 
     * @param namespace 命名空间
     * @param modelType 模型类型字符串
     * @return 是否支持
     */
    public boolean supportsModelType(String namespace, String modelType) {
        ModelType type = ModelType.fromCode(modelType);
        return type != null && supportsModelType(namespace, type);
    }
}
