package com.yonchain.ai.model.core;

import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.model.registry.NamespaceFactoryRegistry;
import com.yonchain.ai.model.registry.TypeHandlerRegistry;

import java.util.Properties;

/**
 * 模型配置管理中心
 * 
 * 负责管理整个模型系统的配置信息，包括：
 * - 全局配置属性
 * - 模型注册中心
 * - 命名空间工厂注册中心
 * - 类型处理器注册中心
 */
public class ModelConfiguration {
    
    private Properties properties;
    private ModelRegistry modelRegistry;
    private NamespaceFactoryRegistry namespaceFactoryRegistry;
    private TypeHandlerRegistry typeHandlerRegistry;
    
    public ModelConfiguration() {
        this.properties = new Properties();
        this.modelRegistry = new ModelRegistry();
        this.namespaceFactoryRegistry = new NamespaceFactoryRegistry();
        this.typeHandlerRegistry = new TypeHandlerRegistry();
    }
    
    // Properties
    public Properties getProperties() {
        return properties;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    // Model Registry
    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }
    
    public void setModelRegistry(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
    }
    
    // Namespace Factory Registry
    public NamespaceFactoryRegistry getNamespaceFactoryRegistry() {
        return namespaceFactoryRegistry;
    }
    
    public void setNamespaceFactoryRegistry(NamespaceFactoryRegistry namespaceFactoryRegistry) {
        this.namespaceFactoryRegistry = namespaceFactoryRegistry;
    }
    
    // Type Handler Registry
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }
    
    public void setTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistry) {
        this.typeHandlerRegistry = typeHandlerRegistry;
    }
    
    // Convenience methods
    public boolean isCacheEnabled() {
        return Boolean.parseBoolean(getProperty("cache.enabled", "true"));
    }
    
    public int getDefaultTimeout() {
        return Integer.parseInt(getProperty("default.timeout", "30"));
    }
    
    public boolean isOpenAiCompatibilityEnabled() {
        return Boolean.parseBoolean(getProperty("openai.compatibility.enabled", "true"));
    }
}
