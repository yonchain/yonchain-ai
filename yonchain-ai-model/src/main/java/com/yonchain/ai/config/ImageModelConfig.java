package com.yonchain.ai.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图像模型配置类
 */
public class ImageModelConfig {
    
    private String name;
    private String provider;
    private String endpoint;
    private String apiKey;
    private List<String> supportedSizes = Arrays.asList("1024x1024", "512x512", "256x256");
    private Boolean enabled = true;
    private Integer timeout = 60000; // 图像生成通常需要更长时间
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
    
    public List<String> getSupportedSizes() {
        return supportedSizes;
    }
    
    public void setSupportedSizes(List<String> supportedSizes) {
        this.supportedSizes = supportedSizes;
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
