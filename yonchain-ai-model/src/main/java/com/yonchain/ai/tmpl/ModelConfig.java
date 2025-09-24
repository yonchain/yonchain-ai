package com.yonchain.ai.tmpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 模型配置类
 * 包含模型的所有配置信息
 */
public class ModelConfig {
    
    private String name;
    private String provider;
    private ModelType type;
    private String endpoint;
    private String apiKey;
    private Integer maxTokens;
    private Double temperature;
    private Boolean enabled = true;
    private Integer timeout = 30000; // 30秒超时
    private Integer retryCount = 3;
    private Map<String, Object> properties = new HashMap<>();
    
    // 构造方法
    public ModelConfig() {}
    
    public ModelConfig(String name, String provider, ModelType type) {
        this.name = name;
        this.provider = provider;
        this.type = type;
    }
    
    // Getter和Setter方法
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getProvider() { 
        return provider; 
    }
    
    public void setProvider(String provider) { 
        this.provider = provider; 
    }
    
    public ModelType getType() { 
        return type; 
    }
    
    public void setType(ModelType type) { 
        this.type = type; 
    }
    
    public String getEndpoint() { 
        return endpoint; 
    }
    
    public void setEndpoint(String endpoint) { 
        this.endpoint = endpoint; 
    }
    
    public String getApiKey() { 
        return apiKey; 
    }
    
    public void setApiKey(String apiKey) { 
        this.apiKey = apiKey; 
    }
    
    public Integer getMaxTokens() { 
        return maxTokens; 
    }
    
    public void setMaxTokens(Integer maxTokens) { 
        this.maxTokens = maxTokens; 
    }
    
    public Double getTemperature() { 
        return temperature; 
    }
    
    public void setTemperature(Double temperature) { 
        this.temperature = temperature; 
    }
    
    public Boolean getEnabled() { 
        return enabled; 
    }
    
    public void setEnabled(Boolean enabled) { 
        this.enabled = enabled; 
    }
    
    public Integer getTimeout() { 
        return timeout; 
    }
    
    public void setTimeout(Integer timeout) { 
        this.timeout = timeout; 
    }
    
    public Integer getRetryCount() { 
        return retryCount; 
    }
    
    public void setRetryCount(Integer retryCount) { 
        this.retryCount = retryCount; 
    }
    
    public Map<String, Object> getProperties() { 
        return properties; 
    }
    
    public void setProperties(Map<String, Object> properties) { 
        this.properties = properties; 
    }
    
    /**
     * 获取属性值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        return (T) properties.getOrDefault(key, defaultValue);
    }
    
    /**
     * 设置属性值
     * 
     * @param key 属性键
     * @param value 属性值
     */
    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }
    
    /**
     * 获取完整的模型名称（provider:model）
     * 
     * @return 完整的模型名称
     */
    public String getFullModelName() {
        if (provider != null && !provider.trim().isEmpty()) {
            return ModelNameUtils.buildFullModelName(provider, name);
        }
        return name;
    }
    
    @Override
    public String toString() {
        return "ModelConfig{" +
                "name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                ", type=" + type +
                ", enabled=" + enabled +
                '}';
    }
}

