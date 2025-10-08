package com.yonchain.ai.plugin.service;

import com.yonchain.ai.plugin.entity.ToolInstallation;

import java.util.List;

/**
 * 工具插件安装服务接口
 * 
 * @author yonchain
 */
public interface ToolInstallationService {

    /**
     * 安装工具插件
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @param pluginId 插件ID
     * @return 安装记录
     */
    ToolInstallation installToolPlugin(String tenantId, String toolName, String pluginId);

    /**
     * 安装工具插件（带唯一标识符）
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @param pluginId 插件ID
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 安装记录
     */
    ToolInstallation installToolPlugin(String tenantId, String toolName, String pluginId, String pluginUniqueIdentifier);

    /**
     * 安装工具插件（带配置）
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @param pluginId 插件ID
     * @param config 配置信息
     * @param enabled 是否启用
     * @return 安装记录
     */
    ToolInstallation installToolPlugin(String tenantId, String toolName, String pluginId, String config, Boolean enabled);

    /**
     * 卸载工具插件
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 是否成功
     */
    boolean uninstallToolPlugin(String tenantId, String toolName);

    /**
     * 启用工具插件
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 是否成功
     */
    boolean enableToolPlugin(String tenantId, String toolName);

    /**
     * 禁用工具插件
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 是否成功
     */
    boolean disableToolPlugin(String tenantId, String toolName);

    /**
     * 启用/禁用工具插件
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @param enabled 是否启用
     * @return 是否成功
     */
    boolean toggleToolPlugin(String tenantId, String toolName, boolean enabled);

    /**
     * 更新工具配置
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @param config 配置信息
     * @return 更新后的安装记录
     */
    ToolInstallation updateToolConfig(String tenantId, String toolName, String config);

    /**
     * 查询租户的工具插件安装记录
     * 
     * @param tenantId 租户ID
     * @return 安装记录列表
     */
    List<ToolInstallation> getTenantToolInstallations(String tenantId);

    /**
     * 查询租户启用的工具插件
     * 
     * @param tenantId 租户ID
     * @return 启用的工具插件列表
     */
    List<ToolInstallation> getTenantEnabledToolInstallations(String tenantId);

    /**
     * 查询租户已安装的工具名称列表
     * 
     * @param tenantId 租户ID
     * @return 工具名称列表
     */
    List<String> getTenantInstalledToolNames(String tenantId);

    /**
     * 查询租户已启用的工具名称列表
     * 
     * @param tenantId 租户ID
     * @return 启用的工具名称列表
     */
    List<String> getTenantEnabledToolNames(String tenantId);

    /**
     * 检查工具是否已安装
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 是否已安装
     */
    boolean isToolInstalled(String tenantId, String toolName);

    /**
     * 根据ID查询安装记录
     * 
     * @param id 安装记录ID
     * @return 安装记录
     */
    ToolInstallation getById(String id);

    /**
     * 根据租户和工具名称查询安装记录
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 安装记录
     */
    ToolInstallation getByTenantAndTool(String tenantId, String toolName);

    /**
     * 根据插件ID查询安装记录列表
     * 
     * @param pluginId 插件ID
     * @return 安装记录列表
     */
    List<ToolInstallation> getByPluginId(String pluginId);

    /**
     * 根据工具名称查询安装记录列表
     * 
     * @param toolName 工具名称
     * @return 安装记录列表
     */
    List<ToolInstallation> getByToolName(String toolName);

    /**
     * 根据ID删除安装记录
     * 
     * @param id 安装记录ID
     * @return 是否成功
     */
    boolean deleteById(String id);

    /**
     * 统计租户的安装记录数量
     * 
     * @param tenantId 租户ID
     * @return 记录数量
     */
    long countByTenantId(String tenantId);

    /**
     * 统计工具的安装记录数量
     * 
     * @param toolName 工具名称
     * @return 记录数量
     */
    long countByToolName(String toolName);

    /**
     * 统计启用的安装记录数量
     * 
     * @return 启用记录数量
     */
    long countEnabled();

    /**
     * 检查租户和工具名称组合是否存在
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndToolName(String tenantId, String toolName);
}


