package com.yonchain.ai.plugin.registry;

import com.yonchain.ai.plugin.entity.PluginInfo;

import java.util.List;
import java.util.Optional;

/**
 * 插件注册中心接口
 * 
 * @author yonchain
 */
public interface PluginRegistry {
    
    /**
     * 保存插件信息
     * 
     * @param pluginInfo 插件信息
     * @return 保存后的插件信息
     */
    PluginInfo save(PluginInfo pluginInfo);
    
    /**
     * 根据插件ID查找插件信息
     * 
     * @param pluginId 插件ID
     * @return 插件信息
     */
    Optional<PluginInfo> findByPluginId(String pluginId);
    
    /**
     * 根据插件名称查找插件信息
     * 
     * @param name 插件名称
     * @return 插件信息列表
     */
    List<PluginInfo> findByName(String name);
    
    /**
     * 根据插件类型查找插件信息
     * 
     * @param type 插件类型
     * @return 插件信息列表
     */
    List<PluginInfo> findByType(String type);
    
    /**
     * 根据插件状态查找插件信息
     * 
     * @param status 插件状态
     * @return 插件信息列表
     */
    List<PluginInfo> findByStatus(String status);
    
    /**
     * 根据启用状态查找插件信息
     * 
     * @param enabled 是否启用
     * @return 插件信息列表
     */
    List<PluginInfo> findByEnabled(boolean enabled);
    
    /**
     * 根据插件类型和状态查找插件信息
     * 
     * @param type 插件类型
     * @param status 插件状态
     * @return 插件信息列表
     */
    List<PluginInfo> findByTypeAndStatus(String type, String status);
    
    /**
     * 查找所有插件信息
     * 
     * @return 插件信息列表
     */
    List<PluginInfo> findAll();
    
    /**
     * 检查插件是否存在
     * 
     * @param pluginId 插件ID
     * @return 是否存在
     */
    boolean existsByPluginId(String pluginId);
    
    /**
     * 根据插件ID删除插件信息
     * 
     * @param pluginId 插件ID
     */
    void deleteByPluginId(String pluginId);
    
    /**
     * 删除插件信息
     * 
     * @param pluginInfo 插件信息
     */
    void delete(PluginInfo pluginInfo);
    
    /**
     * 删除所有插件信息
     */
    void deleteAll();
    
    /**
     * 统计插件总数
     * 
     * @return 插件总数
     */
    long count();
    
    /**
     * 根据插件类型统计插件数量
     * 
     * @param type 插件类型
     * @return 插件数量
     */
    long countByType(String type);
    
    /**
     * 根据插件状态统计插件数量
     * 
     * @param status 插件状态
     * @return 插件数量
     */
    long countByStatus(String status);
    
    /**
     * 统计启用的插件数量
     * 
     * @return 启用的插件数量
     */
    long countByEnabled();
}

