package com.yonchain.ai.plugin.service;

import com.yonchain.ai.plugin.entity.AiModelInstallation;

import java.util.List;

/**
 * AI模型插件安装服务接口
 * 
 * @author yonchain
 */
public interface AiModelInstallationService {

    /**
     * 安装AI模型插件
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @param pluginId 插件ID
     * @return 安装记录
     */
    AiModelInstallation installAiModelPlugin(String tenantId, String provider, String pluginId);

    /**
     * 安装AI模型插件（带唯一标识符）
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @param pluginId 插件ID
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 安装记录
     */
    AiModelInstallation installAiModelPlugin(String tenantId, String provider, String pluginId, String pluginUniqueIdentifier);

    /**
     * 卸载AI模型插件
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return 是否成功
     */
    boolean uninstallAiModelPlugin(String tenantId, String provider);

    /**
     * 查询租户的AI模型插件安装记录
     * 
     * @param tenantId 租户ID
     * @return 安装记录列表
     */
    List<AiModelInstallation> getTenantAiModelInstallations(String tenantId);

    /**
     * 查询租户已安装的提供商列表
     * 
     * @param tenantId 租户ID
     * @return 提供商列表
     */
    List<String> getTenantInstalledProviders(String tenantId);

    /**
     * 检查提供商是否已安装
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return 是否已安装
     */
    boolean isProviderInstalled(String tenantId, String provider);

    /**
     * 根据ID查询安装记录
     * 
     * @param id 安装记录ID
     * @return 安装记录
     */
    AiModelInstallation getById(String id);

    /**
     * 根据租户和提供商查询安装记录
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return 安装记录
     */
    AiModelInstallation getByTenantAndProvider(String tenantId, String provider);

    /**
     * 根据插件ID查询安装记录列表
     * 
     * @param pluginId 插件ID
     * @return 安装记录列表
     */
    List<AiModelInstallation> getByPluginId(String pluginId);

    /**
     * 根据提供商查询安装记录列表
     * 
     * @param provider 提供商名称
     * @return 安装记录列表
     */
    List<AiModelInstallation> getByProvider(String provider);

    /**
     * 更新插件唯一标识符
     * 
     * @param id 安装记录ID
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 更新后的安装记录
     */
    AiModelInstallation updatePluginUniqueIdentifier(String id, String pluginUniqueIdentifier);

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
     * 统计提供商的安装记录数量
     * 
     * @param provider 提供商名称
     * @return 记录数量
     */
    long countByProvider(String provider);

    /**
     * 检查租户和提供商组合是否存在
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return 是否存在
     */
    boolean existsByTenantIdAndProvider(String tenantId, String provider);
}


