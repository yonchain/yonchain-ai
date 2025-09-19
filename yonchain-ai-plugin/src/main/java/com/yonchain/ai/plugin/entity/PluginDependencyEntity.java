package com.yonchain.ai.plugin.entity;

/**
 * 插件依赖实体
 * 
 * @author yonchain
 */
public class PluginDependencyEntity {
    
    private Long id;
    private String pluginId;
    private String dependencyId;
    private String minVersion;
    private String maxVersion;
    private Boolean optional = false;
    
    // 默认构造函数
    public PluginDependencyEntity() {}
    
    // 带参数构造函数
    public PluginDependencyEntity(String pluginId, String dependencyId, String minVersion, String maxVersion, Boolean optional) {
        this.pluginId = pluginId;
        this.dependencyId = dependencyId;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.optional = optional != null ? optional : false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
    
    public String getDependencyId() {
        return dependencyId;
    }
    
    public void setDependencyId(String dependencyId) {
        this.dependencyId = dependencyId;
    }
    
    public String getMinVersion() {
        return minVersion;
    }
    
    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }
    
    public String getMaxVersion() {
        return maxVersion;
    }
    
    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }
    
    public Boolean getOptional() {
        return optional;
    }
    
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }
    
    @Override
    public String toString() {
        return "PluginDependencyEntity{" +
                "id=" + id +
                ", dependencyId='" + dependencyId + '\'' +
                ", minVersion='" + minVersion + '\'' +
                ", maxVersion='" + maxVersion + '\'' +
                ", optional=" + optional +
                '}';
    }
}





