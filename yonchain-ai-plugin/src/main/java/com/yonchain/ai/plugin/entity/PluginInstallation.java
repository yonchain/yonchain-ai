package com.yonchain.ai.plugin.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 插件安装实体
 * 对应 plugin_installation 表
 * 
 * @author yonchain
 */
@Data
public class PluginInstallation {
    
    /**
     * 主键ID
     */
    private String id;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 插件ID
     */
    private String pluginId;

    /**
     * 插件唯一标识符
     */
    private String pluginUniqueIdentifier;

    /**
     * 运行时类型
     */
    private String runtimeType;

    /**
     * 端点设置数量
     */
    private Long endpointsSetups;

    /**
     * 活跃端点数量
     */
    private Long endpointsActive;

    /**
     * 来源
     */
    private String source;

    /**
     * 元数据（JSON格式）
     */
    private String meta;

    /**
     * 构造函数
     */
    public PluginInstallation() {
        this.endpointsSetups = 0L;
        this.endpointsActive = 0L;
    }

    /**
     * 创建新的插件安装记录
     */
    public static PluginInstallation create(String tenantId, String pluginId, String runtimeType) {
        PluginInstallation installation = new PluginInstallation();
        installation.setId(UUID.randomUUID().toString());
        installation.setTenantId(tenantId);
        installation.setPluginId(pluginId);
        installation.setRuntimeType(runtimeType);
        installation.setSource("manual");
        installation.setCreatedAt(LocalDateTime.now());
        installation.setUpdatedAt(LocalDateTime.now());
        return installation;
    }

    /**
     * 创建带唯一标识符的插件安装记录
     */
    public static PluginInstallation create(String tenantId, String pluginId, String runtimeType, String pluginUniqueIdentifier) {
        PluginInstallation installation = create(tenantId, pluginId, runtimeType);
        installation.setPluginUniqueIdentifier(pluginUniqueIdentifier);
        return installation;
    }

    /**
     * 创建带元数据的插件安装记录
     */
    public static PluginInstallation create(String tenantId, String pluginId, String runtimeType, String pluginUniqueIdentifier, String meta) {
        PluginInstallation installation = create(tenantId, pluginId, runtimeType, pluginUniqueIdentifier);
        installation.setMeta(meta);
        return installation;
    }
}
