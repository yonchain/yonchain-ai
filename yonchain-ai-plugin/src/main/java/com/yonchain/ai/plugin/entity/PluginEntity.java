package com.yonchain.ai.plugin.entity;

import com.yonchain.ai.plugin.enums.PluginStatus;
import com.yonchain.ai.plugin.enums.PluginType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件实体
 * 
 * @author yonchain
 */
public class PluginEntity {
    
    private String pluginId;
    private String name;
    private String version;
    private String description;
    private String author;
    private String homepage;
    private PluginType type;
    private PluginStatus status;
    private String pluginPath;
    private String mainClass;
    private String providerSource;
    private String providerInterface;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime installedAt;
    private LocalDateTime enabledAt;
    private LocalDateTime disabledAt;
    private String iconPath;

    // 关联数据
    private List<PluginDependencyEntity> dependencies = new ArrayList<>();
    private List<PluginExtensionEntity> extensions = new ArrayList<>();
    private List<PluginServiceEntity> services = new ArrayList<>();
    
    // 默认构造函数
    public PluginEntity() {}
    
    // 带参数构造函数
    public PluginEntity(String pluginId, String name, String version, PluginType type, PluginStatus status) {
        this.pluginId = pluginId;
        this.name = name;
        this.version = version;
        this.type = type;
        this.status = status;
    }
    
    // Getters and Setters
    public String getPluginId() {
        return pluginId;
    }
    
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getHomepage() {
        return homepage;
    }
    
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
    
    public PluginType getType() {
        return type;
    }
    
    public void setType(PluginType type) {
        this.type = type;
    }
    
    public PluginStatus getStatus() {
        return status;
    }
    
    public void setStatus(PluginStatus status) {
        this.status = status;
    }
    
    public String getPluginPath() {
        return pluginPath;
    }
    
    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }
    
    public String getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getProviderSource() {
        return providerSource;
    }
    
    public void setProviderSource(String providerSource) {
        this.providerSource = providerSource;
    }
    
    public String getProviderInterface() {
        return providerInterface;
    }
    
    public void setProviderInterface(String providerInterface) {
        this.providerInterface = providerInterface;
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
    
    public LocalDateTime getInstalledAt() {
        return installedAt;
    }
    
    public void setInstalledAt(LocalDateTime installedAt) {
        this.installedAt = installedAt;
    }
    
    public LocalDateTime getEnabledAt() {
        return enabledAt;
    }
    
    public void setEnabledAt(LocalDateTime enabledAt) {
        this.enabledAt = enabledAt;
    }
    
    public LocalDateTime getDisabledAt() {
        return disabledAt;
    }
    
    public void setDisabledAt(LocalDateTime disabledAt) {
        this.disabledAt = disabledAt;
    }
    
    public List<PluginDependencyEntity> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<PluginDependencyEntity> dependencies) {
        this.dependencies = dependencies;
    }
    
    public List<PluginExtensionEntity> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(List<PluginExtensionEntity> extensions) {
        this.extensions = extensions;
    }
    
    public List<PluginServiceEntity> getServices() {
        return services;
    }
    
    public void setServices(List<PluginServiceEntity> services) {
        this.services = services;
    }
    
    // 便利方法
    public void addDependency(PluginDependencyEntity dependency) {
        dependencies.add(dependency);
        dependency.setPluginId(this.pluginId);
    }
    
    public void removeDependency(PluginDependencyEntity dependency) {
        dependencies.remove(dependency);
        dependency.setPluginId(null);
    }
    
    public void addExtension(PluginExtensionEntity extension) {
        extensions.add(extension);
        extension.setPluginId(this.pluginId);
    }
    
    public void removeExtension(PluginExtensionEntity extension) {
        extensions.remove(extension);
        extension.setPluginId(null);
    }
    
    public void addService(PluginServiceEntity service) {
        services.add(service);
        service.setPluginId(this.pluginId);
    }
    
    public void removeService(PluginServiceEntity service) {
        services.remove(service);
        service.setPluginId(null);
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String toString() {
        return "PluginEntity{" +
                "pluginId='" + pluginId + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}





