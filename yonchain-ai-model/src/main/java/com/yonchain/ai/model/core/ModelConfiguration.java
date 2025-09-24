package com.yonchain.ai.model.core;

import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.model.registry.ModelFactoryRegistry;
import com.yonchain.ai.model.registry.OptionsHandlerRegistry;

import java.util.Map;
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
    private ModelFactoryRegistry modelFactoryRegistry;
    private OptionsHandlerRegistry typeHandlerRegistry;
    private Map<String, Map<String, String>> environments;
    private String defaultEnvironment;
    
    public ModelConfiguration() {
        this.properties = new Properties();
        this.modelRegistry = new ModelRegistry();
        this.modelFactoryRegistry = new ModelFactoryRegistry();
        this.typeHandlerRegistry = new OptionsHandlerRegistry();
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
    public ModelFactoryRegistry getModelFactoryRegistry() {
        return modelFactoryRegistry;
    }
    
    public void setModelFactoryRegistry(ModelFactoryRegistry modelFactoryRegistry) {
        this.modelFactoryRegistry = modelFactoryRegistry;
    }
    
    // Type Handler Registry
    public OptionsHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }
    
    public void setTypeHandlerRegistry(OptionsHandlerRegistry typeHandlerRegistry) {
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
    
    // Environment management
    public Map<String, Map<String, String>> getEnvironments() {
        return environments;
    }
    
    public void setEnvironments(Map<String, Map<String, String>> environments) {
        this.environments = environments;
    }
    
    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }
    
    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }
    
    public Map<String, String> getEnvironmentProperties(String environmentId) {
        return environments != null ? environments.get(environmentId) : null;
    }
}
