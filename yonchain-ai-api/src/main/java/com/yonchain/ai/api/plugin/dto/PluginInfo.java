package com.yonchain.ai.api.plugin.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 插件信息DTO
 * API层的独立对象，不依赖其他模块
 * 
 * @author yonchain
 */
public class PluginInfo {
    
    /**
     * 数据库主键
     */
    private Long id;
    
    private String pluginId;
    private String name;
    private String version;
    private String description;
    private String author;
    private String vendor;
    private String type;
    private String status;
    private String pluginPath;
    private Boolean enabled;
    private Boolean available;
    private String iconUrl;
    private String iconPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime installTime;
    private LocalDateTime updateTime;
    private String checksum;
    private String mainClass;
    private Integer priority;
    private String dependencies;
    private String extensions;
    private String metadata;
    
    // 构造函数
    public PluginInfo() {
        this.installTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = false;
        this.priority = 0;
        this.status = "not_installed";
    }
    
    public PluginInfo(String pluginId, String name, String version, String description, 
                     String author, String type, String status, Boolean enabled, 
                     Boolean available, String iconUrl, LocalDateTime createdAt, 
                     LocalDateTime updatedAt) {
        this();
        this.pluginId = pluginId;
        this.name = name;
        this.version = version;
        this.description = description;
        this.author = author;
        this.type = type;
        this.status = status;
        this.enabled = enabled;
        this.available = available;
        this.iconUrl = iconUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.installTime = createdAt;
        this.updateTime = updatedAt;
    }
    
    /**
     * 从插件描述符创建插件信息
     * 注意：这里不能直接引用 PluginDescriptor，所以通过 Map 传递数据
     * 
     * @param descriptorData 插件描述符数据
     * @return 插件信息
     */
    public static PluginInfo fromDescriptorData(java.util.Map<String, Object> descriptorData) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId((String) descriptorData.get("id"));
        pluginInfo.setName((String) descriptorData.get("name"));
        pluginInfo.setVersion((String) descriptorData.get("version"));
        pluginInfo.setDescription((String) descriptorData.get("description"));
        pluginInfo.setAuthor((String) descriptorData.get("author"));
        pluginInfo.setType((String) descriptorData.get("type"));
        pluginInfo.setMainClass((String) descriptorData.get("mainClass"));
        
        String pluginPath = (String) descriptorData.get("pluginPath");
        pluginInfo.setPluginPath(pluginPath);
        
        return pluginInfo;
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
    
    public String getVendor() {
        return vendor;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updateTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPluginPath() {
        return pluginPath;
    }
    
    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
        this.updateTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
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
    
    public LocalDateTime getInstallTime() {
        return installTime;
    }
    
    public void setInstallTime(LocalDateTime installTime) {
        this.installTime = installTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getChecksum() {
        return checksum;
    }
    
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    public String getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }
    
    public String getExtensions() {
        return extensions;
    }
    
    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    /**
     * 检查插件是否可用
     * 
     * @return 是否可用
     */
    public boolean isAvailable() {
        return "enabled".equals(status) && Boolean.TRUE.equals(enabled);
    }
    
    /**
     * 检查插件是否为失败状态
     * 
     * @return 是否为失败状态
     */
    public boolean isFailedState() {
        return status != null && (status.endsWith("_failed") || "install_failed".equals(status) || 
                "enable_failed".equals(status) || "disable_failed".equals(status) || "uninstall_failed".equals(status));
    }
    
    /**
     * 检查插件是否正在进行操作
     * 
     * @return 是否正在进行操作
     */
    public boolean isTransitionState() {
        return status != null && ("installing".equals(status) || "enabling".equals(status) || 
                "disabling".equals(status) || "uninstalling".equals(status));
    }
    
    /**
     * 获取插件的完整标识符
     * 
     * @return 完整标识符
     */
    public String getFullId() {
        StringBuilder sb = new StringBuilder();
        if (vendor != null && !vendor.trim().isEmpty()) {
            sb.append(vendor).append(":");
        }
        sb.append(name);
        if (version != null && !version.trim().isEmpty()) {
            sb.append(":").append(version);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "PluginInfo{" +
                "id=" + id +
                ", pluginId='" + pluginId + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", enabled=" + enabled +
                '}';
    }
}
