package com.yonchain.ai.plugin.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AI模型插件安装实体
 * 对应 ai_model_installations 表
 * 
 * @author yonchain
 */
@Data
public class AiModelInstallation {
    
    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 提供商名称（与模型系统的provider_name对应）
     */
    private String provider;

    /**
     * 插件唯一标识符
     */
    private String pluginUniqueIdentifier;

    /**
     * 插件ID
     */
    private String pluginId;

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
    public AiModelInstallation() {
    }

    /**
     * 创建新的AI模型安装记录
     */
    public static AiModelInstallation create(String tenantId, String provider, String pluginId) {
        AiModelInstallation installation = new AiModelInstallation();
        installation.setId(UUID.randomUUID().toString());
        installation.setTenantId(tenantId);
        installation.setProvider(provider);
        installation.setPluginId(pluginId);
        installation.setCreatedAt(LocalDateTime.now());
        installation.setUpdatedAt(LocalDateTime.now());
        return installation;
    }

    /**
     * 创建带唯一标识符的AI模型安装记录
     */
    public static AiModelInstallation create(String tenantId, String provider, String pluginId, String pluginUniqueIdentifier) {
        AiModelInstallation installation = create(tenantId, provider, pluginId);
        installation.setPluginUniqueIdentifier(pluginUniqueIdentifier);
        return installation;
    }

    /**
     * 更新插件唯一标识符
     */
    public void updatePluginUniqueIdentifier(String pluginUniqueIdentifier) {
        this.pluginUniqueIdentifier = pluginUniqueIdentifier;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新提供商
     */
    public void updateProvider(String provider) {
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 刷新更新时间
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
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
            return String.format("%s:%s", provider, pluginUniqueIdentifier);
        }
        return String.format("%s:%s", provider, pluginId);
    }
}


