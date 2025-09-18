package com.yonchain.ai.model.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * 嵌入模型配置类
 */
public class EmbeddingModelConfig {
    
    private String name;
    private String provider;
    private String endpoint;
    private String apiKey;
    private Integer dimensions = 1536; // 默认维度
    private Boolean enabled = true;
    private Integer timeout = 30000;
    private Integer retryCount = 3;
    private Map<String, Object> properties = new HashMap<>();
    
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
    
    public Integer getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(Integer dimensions) {
        this.dimensions = dimensions;
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
}
