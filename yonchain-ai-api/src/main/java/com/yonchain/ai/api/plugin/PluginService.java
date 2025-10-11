package com.yonchain.ai.api.plugin;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.plugin.dto.PluginInfo;

import java.io.InputStream;
import java.util.Map;

/**
 * 插件服务接口
 * 封装所有插件相关操作，包括管理、安装、卸载、图标处理等
 * 
 * @author yonchain
 */
public interface PluginService {

    // ==================== 插件查询接口 ====================

    /**
     * 获取单个插件信息（基础信息）
     * 
     * @param pluginId 插件ID
     * @return 插件信息，如果不存在返回null
     */
    PluginInfo getPlugin(String pluginId);

    /**
     * 分页查询租户的插件列表（带条件）
     * 
     * @param tenantId 租户ID
     * @param queryParam 查询参数
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页插件信息
     */
    Page<PluginInfo> getPlugins(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);
    
    /**
     * 获取租户对插件的安装信息
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 安装信息Map，包含installationType、installedProvider、installedToolName、installedAt等字段
     */
    Map<String, Object> getTenantPluginInstallation(String tenantId, String pluginId);

    // ==================== 插件管理接口 ====================
    
    /**
     * 预览插件信息（上传插件文件但不安装）
     * 注意：返回Map而不是PluginDescriptor，避免API层依赖插件模块
     * 
     * @param inputStream 插件文件输入流
     * @param fileName 文件名
     * @return 插件描述符信息Map
     */
    Map<String, Object> previewPlugin(InputStream inputStream, String fileName);

    /**
     * 通过文件流安装插件
     * 
     * @param inputStream 插件文件输入流
     * @param fileName 文件名
     * @return 安装结果消息
     */
    String installPlugin(InputStream inputStream, String fileName);

    /**
     * 通过路径安装插件
     * 
     * @param pluginPath 插件路径
     * @return 安装结果消息
     */
    String installPluginByPath(String pluginPath);

    /**
     * 通过URL安装插件
     * 
     * @param url 插件下载URL
     * @return 安装结果消息
     */
    String installPluginByUrl(String url);

    /**
     * 通过插件市场安装插件
     * 
     * @param marketplaceId 插件市场ID
     * @return 安装结果消息
     */
    String installPluginFromMarketplace(String marketplaceId);

    // ==================== 租户插件安装接口 ====================
    
    /**
     * 为租户通过文件流安装插件
     * 
     * @param tenantId 租户ID
     * @param inputStream 插件文件输入流
     * @param fileName 文件名
     * @return 安装结果消息
     */
    String installPluginForTenant(String tenantId, InputStream inputStream, String fileName);

    /**
     * 为租户通过路径安装插件
     * 
     * @param tenantId 租户ID
     * @param pluginPath 插件路径
     * @return 安装结果消息
     */
    String installPluginByPathForTenant(String tenantId, String pluginPath);

    /**
     * 为租户通过URL安装插件
     * 
     * @param tenantId 租户ID
     * @param url 插件下载URL
     * @return 安装结果消息
     */
    String installPluginByUrlForTenant(String tenantId, String url);

    /**
     * 为租户通过插件市场安装插件
     * 
     * @param tenantId 租户ID
     * @param marketplaceId 插件市场ID
     * @return 安装结果消息
     */
    String installPluginFromMarketplaceForTenant(String tenantId, String marketplaceId);

    /**
     * 卸载插件（全局卸载，删除所有租户的安装记录）
     * 
     * @param pluginId 插件ID
     * @return 卸载结果消息
     */
    String uninstallPlugin(String pluginId);

/*
    */
/**
     * 为租户卸载插件（只删除该租户的安装记录）
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 卸载结果消息
     *//*

    String uninstallPluginForTenant(String tenantId, String pluginId);
*/

    /**
     * 启用插件
     * 
     * @param pluginId 插件ID
     * @return 启用结果消息
     */
    String enablePlugin(String pluginId);

    /**
     * 禁用插件
     * 
     * @param pluginId 插件ID
     * @return 禁用结果消息
     */
    String disablePlugin(String pluginId);


    // ==================== 插件图标接口 ====================
    
    /**
     * 获取插件图标数据
     * 
     * @param pluginId 插件ID
     * @return 图标数据，包含contentType和data字段，如果不存在返回null
     */
    Map<String, Object> getPluginIcon(String pluginId);

    /**
     * 获取插件图标URL
     * 
     * @param pluginId 插件ID
     * @return 图标URL，如果不存在返回null
     */
    String getPluginIconUrl(String pluginId);

    /**
     * 获取临时图标数据
     * 
     * @param iconId 临时图标ID
     * @return 图标数据，包含contentType和data字段，如果不存在返回null
     */
    Map<String, Object> getTempIcon(String iconId);

    /**
     * 保存临时图标
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @param iconData 图标数据
     * @return 临时图标访问URL
     */
    String saveTempIcon(String pluginId, String iconFileName, byte[] iconData);
}