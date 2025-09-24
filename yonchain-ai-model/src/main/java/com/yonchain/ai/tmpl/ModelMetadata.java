package com.yonchain.ai.tmpl;

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
    private String modelId;  // 模型唯一标识符
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
    
    public String getModelId() {
        // 如果modelId为空，则自动生成 provider:name 格式的ID
        if (modelId == null || modelId.trim().isEmpty()) {
            if (provider != null && !provider.trim().isEmpty() && name != null && !name.trim().isEmpty()) {
                return ModelNameUtils.buildFullModelName(provider, name);
            }
            return name;
        }
        return modelId;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
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
    
    /**
     * 获取完整的模型名称（provider:model）
     * 
     * @return 完整的模型名称
     * @deprecated 使用 {@link #getModelId()} 替代
     */
    @Deprecated
    public String getFullModelName() {
        return getModelId();
    }
    
    /**
     * 从完整的模型名称创建元数据实例
     * 
     * @param fullModelName 完整的模型名称（provider:model）
     * @param type 模型类型
     * @return 模型元数据实例
     */
    public static ModelMetadata fromFullModelName(String fullModelName, ModelType type) {
        ModelNameUtils.ModelNameInfo nameInfo = ModelNameUtils.parseModelName(fullModelName);
        ModelMetadata metadata = new ModelMetadata();
        metadata.setName(nameInfo.getModelName());
        metadata.setProvider(nameInfo.getProvider());
        metadata.setType(type);
        metadata.setModelId(fullModelName); // 设置modelId
        metadata.setDisplayName(fullModelName);
        return metadata;
    }
    
    /**
     * 从模型ID创建元数据实例
     * 
     * @param modelId 模型ID（provider:model 或 简单名称）
     * @param type 模型类型
     * @return 模型元数据实例
     */
    public static ModelMetadata fromModelId(String modelId, ModelType type) {
        ModelNameUtils.ModelNameInfo nameInfo = ModelNameUtils.parseModelName(modelId);
        ModelMetadata metadata = new ModelMetadata();
        metadata.setName(nameInfo.getModelName());
        metadata.setProvider(nameInfo.getProvider());
        metadata.setType(type);
        metadata.setModelId(modelId);
        metadata.setDisplayName(modelId);
        return metadata;
    }
    
    @Override
    public String toString() {
        return "ModelMetadata{" +
                "name='" + name + '\'' +
                ", modelId='" + getModelId() + '\'' +
                ", type=" + type +
                ", provider='" + provider + '\'' +
                ", available=" + available +
                '}';
    }
}

