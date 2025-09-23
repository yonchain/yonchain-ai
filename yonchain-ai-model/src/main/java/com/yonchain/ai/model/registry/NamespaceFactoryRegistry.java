package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.factory.ModelFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 命名空间工厂注册中心
 * 
 * 负责管理所有命名空间工厂的注册和查找
 */
public class NamespaceFactoryRegistry {
    
    // namespace -> ModelFactory
    private final Map<String, ModelFactory> factories = new ConcurrentHashMap<>();
    
    /**
     * 注册命名空间工厂
     * 
     * @param factory 模型工厂
     */
    public void registerFactory(ModelFactory factory) {
        factories.put(factory.namespace(), factory);
    }
    
    /**
     * 获取命名空间工厂
     * 
     * @param namespace 命名空间
     * @return 模型工厂
     */
    public Optional<ModelFactory> getFactory(String namespace) {
        return Optional.ofNullable(factories.get(namespace));
    }
    
    /**
     * 检查命名空间工厂是否存在
     * 
     * @param namespace 命名空间
     * @return 是否存在
     */
    public boolean containsFactory(String namespace) {
        return factories.containsKey(namespace);
    }
    
    /**
     * 移除命名空间工厂
     * 
     * @param namespace 命名空间
     * @return 被移除的工厂
     */
    public Optional<ModelFactory> removeFactory(String namespace) {
        return Optional.ofNullable(factories.remove(namespace));
    }
    
    /**
     * 获取所有注册的命名空间
     * 
     * @return 命名空间集合
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
}
