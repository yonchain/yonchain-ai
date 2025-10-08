package com.yonchain.ai.console.plugin.response;

import lombok.Data;

/**
 * 插件响应类
 * 包含租户安装状态信息
 * 
 * @author yonchain
 */
@Data
public class PluginResponse {
    
    // 基础插件信息
    private String pluginId;
    private String name;
    private String version;
    private String description;
    private String author;
    private String type;
    private String status;
    private Boolean enabled;
    private Boolean available;
    private String iconUrl;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private java.util.List<String> extensions;
    private java.util.List<String> services;
    private java.util.List<String> dependencies;
    
    /**
     * 当前租户是否已安装此插件
     */
    private boolean installedByTenant;
    
    /**
     * 租户安装状态（如果已安装）
     * - ai_model: AI模型插件已安装
     * - tool: 工具插件已安装
     * - both: 同时安装了AI模型和工具插件
     * - null: 未安装
     */
    private String installationType;
    
    /**
     * 租户安装类型 (e.g., "ai-model", "tool")
     * 别名方法，与installationType相同
     */
    public String getTenantInstallationType() {
        return installationType;
    }
    
    public void setTenantInstallationType(String tenantInstallationType) {
        this.installationType = tenantInstallationType;
    }
    
    /**
     * 租户安装的提供商名称（仅AI模型插件）
     */
    private String installedProvider;
    
    /**
     * 租户安装的工具名称（仅工具插件）
     */
    private String installedToolName;
    
    /**
     * 租户安装时间
     */
    private java.time.LocalDateTime installedAt;
    
    /**
     * 从基础PluginResponse创建响应
     *//*
    public static PluginResponse fromPluginResponse(com.yonchain.ai.plugin.response.PluginResponse pluginResponse) {
        PluginResponse response = new PluginResponse();
        
        // 复制基础属性
        response.setPluginId(pluginResponse.getPluginId());
        response.setName(pluginResponse.getName());
        response.setVersion(pluginResponse.getVersion());
        response.setDescription(pluginResponse.getDescription());
        response.setAuthor(pluginResponse.getAuthor());
        response.setType(pluginResponse.getType());
        response.setStatus(pluginResponse.getStatus());
        response.setEnabled(pluginResponse.getEnabled());
        response.setAvailable(pluginResponse.isAvailable());
        response.setIconUrl(pluginResponse.getIconUrl());
        response.setCreatedAt(pluginResponse.getInstallTime());
        response.setUpdatedAt(pluginResponse.getUpdateTime());
        // 注意：原始PluginResponse没有extensions, services, dependencies字段
        response.setExtensions(null);
        response.setServices(null);
        response.setDependencies(null);
        
        // 初始化租户相关属性
        response.setInstalledByTenant(false);
        response.setInstallationType(null);
        response.setInstalledProvider(null);
        response.setInstalledToolName(null);
        response.setInstalledAt(null);
        
        return response;
    }*/
}