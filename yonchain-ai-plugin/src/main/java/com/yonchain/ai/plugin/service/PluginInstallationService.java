package com.yonchain.ai.plugin.service;

import com.yonchain.ai.plugin.entity.PluginInstallation;

import java.util.List;

/**
 * 插件安装服务接口
 * 
 * @author yonchain
 */
public interface PluginInstallationService {

    /**
     * 安装插件
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @param runtimeType 运行时类型
     * @return 插件安装记录
     */
    PluginInstallation installPlugin(String tenantId, String pluginId, String runtimeType);

    /**
     * 安装插件（带唯一标识符）
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @param runtimeType 运行时类型
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 插件安装记录
     */
    PluginInstallation installPlugin(String tenantId, String pluginId, String runtimeType, String pluginUniqueIdentifier);

    /**
     * 安装插件（带元数据）
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @param runtimeType 运行时类型
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @param meta 元数据
     * @return 插件安装记录
     */
    PluginInstallation installPlugin(String tenantId, String pluginId, String runtimeType, String pluginUniqueIdentifier, String meta);

    /**
     * 卸载插件
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 是否成功
     */
    boolean uninstallPlugin(String tenantId, String pluginId);

    /**
     * 根据租户ID获取已安装的插件列表
     * 
     * @param tenantId 租户ID
     * @return 插件安装记录列表
     */
    List<PluginInstallation> getInstalledPlugins(String tenantId);

    /**
     * 根据租户ID获取已安装的插件ID列表
     * 
     * @param tenantId 租户ID
     * @return 插件ID列表
     */
    List<String> getInstalledPluginIds(String tenantId);

    /**
     * 检查插件是否已安装
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 是否已安装
     */
    boolean isPluginInstalled(String tenantId, String pluginId);

    /**
     * 获取插件安装信息
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 插件安装记录，如果未安装返回null
     */
    PluginInstallation getPluginInstallation(String tenantId, String pluginId);

    /**
     * 根据运行时类型获取已安装的插件列表
     * 
     * @param tenantId 租户ID
     * @param runtimeType 运行时类型
     * @return 插件安装记录列表
     */
    List<PluginInstallation> getInstalledPluginsByRuntimeType(String tenantId, String runtimeType);

    /**
     * 更新插件端点数量
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @param endpointsSetups 端点设置数量
     * @param endpointsActive 活跃端点数量
     * @return 是否成功
     */
    boolean updatePluginEndpoints(String tenantId, String pluginId, Long endpointsSetups, Long endpointsActive);

    /**
     * 更新插件元数据
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @param meta 元数据
     * @return 是否成功
     */
    boolean updatePluginMeta(String tenantId, String pluginId, String meta);

    /**
     * 根据租户ID关联查询插件信息
     * 
     * @param tenantId 租户ID
     * @return 租户已安装的插件实体列表
     */
    List<com.yonchain.ai.plugin.entity.PluginEntity> getTenantPluginsByTenantId(String tenantId);

    /**
     * 分页查询租户插件信息（带条件）
     * 
     * @param tenantId 租户ID
     * @param status 插件状态
     * @param type 插件类型
     * @param name 插件名称（模糊查询）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 租户已安装的插件实体列表
     */
    List<com.yonchain.ai.plugin.entity.PluginEntity> getTenantPluginsWithConditions(String tenantId, String status, String type, String name, int pageNum, int pageSize);

    /**
     * 统计租户插件数量（带条件）
     * 
     * @param tenantId 租户ID
     * @param status 插件状态
     * @param type 插件类型
     * @param name 插件名称（模糊查询）
     * @return 插件数量
     */
    long countTenantPluginsWithConditions(String tenantId, String status, String type, String name);
}
