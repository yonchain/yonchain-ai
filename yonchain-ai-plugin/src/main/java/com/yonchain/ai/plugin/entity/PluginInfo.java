package com.yonchain.ai.plugin.entity;

import com.yonchain.ai.plugin.descriptor.PluginDescriptor;

import java.time.LocalDateTime;

/**
 * 插件信息实体
 * 用于数据库存储和业务处理
 * 
 * @author yonchain
 */
public class PluginInfo {
    
    /**
     * 数据库主键
     */
    private Long id;
    
    /**
     * 插件唯一标识符
     */
    private String pluginId;
    
    /**
     * 插件名称
     */
    private String name;
    
    /**
     * 插件版本
     */
    private String version;
    
    /**
     * 插件描述
     */
    private String description;
    
    /**
     * 插件作者
     */
    private String author;
    
    /**
     * 插件供应商
     */
    private String vendor;
    
    /**
     * 插件类型
     */
    private String type;
    
    /**
     * 插件状态
     */
    private String status;
    
    /**
     * 插件路径
     */
    private String pluginPath;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 安装时间
     */
    private LocalDateTime installTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 文件校验和
     */
    private String checksum;
    
    /**
     * 主类名
     */
    private String mainClass;
    
    /**
     * 加载优先级
     */
    private Integer priority;
    
    /**
     * 依赖信息（JSON格式）
     */
    private String dependencies;
    
    /**
     * 扩展点信息（JSON格式）
     */
    private String extensions;
    
    /**
     * 其他元数据（JSON格式）
     */
    private String metadata;
    
    /**
     * 插件图标路径
     */
    private String iconPath;
    
    /**
     * 插件描述符（运行时使用，不持久化）
     */
    private transient PluginDescriptor descriptor;
    
    public PluginInfo() {
        this.installTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.enabled = false;
        this.priority = 0;
        this.status = "not_installed";
    }
    
    /**
     * 从插件描述符创建插件信息
     * 
     * @param descriptor 插件描述符
     * @return 插件信息
     */
    public static PluginInfo fromDescriptor(PluginDescriptor descriptor) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(descriptor.getId());
        pluginInfo.setName(descriptor.getName());
        pluginInfo.setVersion(descriptor.getVersion());
        
        // 处理多语言描述，优先使用中文，其次英文
        String description = descriptor.getLocalizedDescription("zh_Hans");
        if (description == null) {
            description = descriptor.getLocalizedDescription("en_US");
        }
        pluginInfo.setDescription(description);
        
        pluginInfo.setAuthor(descriptor.getAuthor());
        
        // 直接设置插件类型字符串
        if (descriptor.getType() != null) {
            pluginInfo.setType(descriptor.getType());
        } else {
            pluginInfo.setType("model"); // 默认为model类型
        }
        
        pluginInfo.setPluginPath(descriptor.getPluginPath() != null ? descriptor.getPluginPath().toString() : null);
        pluginInfo.setMainClass(descriptor.getPluginClass());
        
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

    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
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
