package com.yonchain.ai.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 模型元数据类
 * 包含模型的描述信息和能力特性
 */
public class ModelMetadata {
    
    private String name;
    private String displayName;
    private ModelType type;
    private String provider;
    private String description;
    private String version;
    private Integer maxTokens;
    private Set<String> supportedFeatures = new HashSet<>();
    private ModelConfig config;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean available = true;
    
    // 构造方法
    public ModelMetadata() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ModelMetadata(String name, ModelType type, String provider) {
        this();
        this.name = name;
        this.type = type;
        this.provider = provider;
        this.displayName = name;
    }
    
    // Getter和Setter方法
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDisplayName() { 
        return displayName; 
    }
    
    public void setDisplayName(String displayName) { 
        this.displayName = displayName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public ModelType getType() { 
        return type; 
    }
    
    public void setType(ModelType type) { 
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getProvider() { 
        return provider; 
    }
    
    public void setProvider(String provider) { 
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getVersion() { 
        return version; 
    }
    
    public void setVersion(String version) { 
        this.version = version;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getMaxTokens() { 
        return maxTokens; 
    }
    
    public void setMaxTokens(Integer maxTokens) { 
        this.maxTokens = maxTokens;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<String> getSupportedFeatures() { 
        return supportedFeatures; 
    }
    
    public void setSupportedFeatures(Set<String> supportedFeatures) { 
        this.supportedFeatures = supportedFeatures;
        this.updatedAt = LocalDateTime.now();
    }
    
    public ModelConfig getConfig() { 
        return config; 
    }
    
    public void setConfig(ModelConfig config) { 
        this.config = config;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    public Boolean getAvailable() { 
        return available; 
    }
    
    public void setAvailable(Boolean available) { 
        this.available = available;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加支持的特性
     * 
     * @param feature 特性名称
     */
    public void addSupportedFeature(String feature) {
        this.supportedFeatures.add(feature);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查是否支持指定特性
     * 
     * @param feature 特性名称
     * @return 是否支持
     */
    public boolean supportsFeature(String feature) {
        return this.supportedFeatures.contains(feature);
    }
    
    @Override
    public String toString() {
        return "ModelMetadata{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", provider='" + provider + '\'' +
                ", available=" + available +
                '}';
    }
}
