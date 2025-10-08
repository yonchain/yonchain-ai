package com.yonchain.ai.plugin.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 工具插件安装实体
 * 对应 tool_installations 表
 * 
 * @author yonchain
 */
@Data
public class ToolInstallation {
    
    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 插件唯一标识符
     */
    private String pluginUniqueIdentifier;

    /**
     * 插件ID
     */
    private String pluginId;

    /**
     * 配置信息（JSON格式）
     */
    private String config;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 构造函数
     */
    public ToolInstallation() {
        this.enabled = true;
    }

    /**
     * 创建新的工具安装记录
     */
    public static ToolInstallation create(String tenantId, String toolName, String pluginId) {
        ToolInstallation installation = new ToolInstallation();
        installation.setId(UUID.randomUUID().toString());
        installation.setTenantId(tenantId);
        installation.setToolName(toolName);
        installation.setPluginId(pluginId);
        installation.setEnabled(true);
        installation.setCreatedAt(LocalDateTime.now());
        installation.setUpdatedAt(LocalDateTime.now());
        return installation;
    }

    /**
     * 创建带唯一标识符的工具安装记录
     */
    public static ToolInstallation create(String tenantId, String toolName, String pluginId, String pluginUniqueIdentifier) {
        ToolInstallation installation = create(tenantId, toolName, pluginId);
        installation.setPluginUniqueIdentifier(pluginUniqueIdentifier);
        return installation;
    }

    /**
     * 创建带配置的工具安装记录
     */
    public static ToolInstallation create(String tenantId, String toolName, String pluginId, String config, Boolean enabled) {
        ToolInstallation installation = create(tenantId, toolName, pluginId);
        installation.setConfig(config);
        installation.setEnabled(enabled != null ? enabled : true);
        return installation;
    }

    /**
     * 更新配置
     */
    public void updateConfig(String config) {
        this.config = config;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 启用工具
     */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 禁用工具
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置启用状态
     */
    public void setEnabledStatus(Boolean enabled) {
        this.enabled = enabled != null ? enabled : true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新插件唯一标识符
     */
    public void updatePluginUniqueIdentifier(String pluginUniqueIdentifier) {
        this.pluginUniqueIdentifier = pluginUniqueIdentifier;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新工具名称
     */
    public void updateToolName(String toolName) {
        this.toolName = toolName;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 刷新更新时间
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查是否启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /**
     * 检查是否有配置
     */
    public boolean hasConfig() {
        return config != null && !config.trim().isEmpty();
    }

    /**
     * 检查是否有插件唯一标识符
     */
    public boolean hasPluginUniqueIdentifier() {
        return pluginUniqueIdentifier != null && !pluginUniqueIdentifier.trim().isEmpty();
    }

    /**
     * 获取完整标识符（用于日志和调试）
     */
    public String getFullIdentifier() {
        if (hasPluginUniqueIdentifier()) {
            return String.format("%s:%s", toolName, pluginUniqueIdentifier);
        }
        return String.format("%s:%s", toolName, pluginId);
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        return isEnabled() ? "启用" : "禁用";
    }
}


