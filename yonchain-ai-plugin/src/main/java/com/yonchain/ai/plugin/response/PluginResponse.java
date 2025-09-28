package com.yonchain.ai.plugin.response;

import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.service.PluginIconService;

import java.time.LocalDateTime;

/**
 * 插件信息响应对象
 * 
 * @author yonchain
 */
public class PluginResponse {
    
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
     * 是否可用
     */
    private boolean available;
    
    /**
     * 图标访问URL
     */
    private String iconUrl;
    
    // 私有构造函数，通过静态方法创建
    private PluginResponse() {
    }
    
    /**
     * 从PluginInfo创建响应对象
     * 
     * @param pluginInfo 插件信息
     * @param pluginIconService 图标服务
     * @return 响应对象
     */
    public static PluginResponse fromPluginInfo(PluginInfo pluginInfo, PluginIconService pluginIconService) {
        PluginResponse response = new PluginResponse();
        
        // 基本信息
        response.id = pluginInfo.getId();
        response.pluginId = pluginInfo.getPluginId();
        response.name = pluginInfo.getName();
        response.version = pluginInfo.getVersion();
        response.description = pluginInfo.getDescription();
        response.author = pluginInfo.getAuthor();
        response.vendor = pluginInfo.getVendor();
        response.type = pluginInfo.getType();
        response.status = pluginInfo.getStatus();
        response.enabled = pluginInfo.getEnabled();
        response.installTime = pluginInfo.getInstallTime();
        response.updateTime = pluginInfo.getUpdateTime();
        response.available = pluginInfo.isAvailable();
        
        // 生成图标URL
        response.iconUrl = "/plugins/" + pluginInfo.getPluginId() + "/icon";
        
        return response;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public String getType() {
        return type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public LocalDateTime getInstallTime() {
        return installTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    // Setters (如果需要的话)
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setInstallTime(LocalDateTime installTime) {
        this.installTime = installTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}





