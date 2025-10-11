package com.yonchain.ai.plugin;

import com.yonchain.ai.plugin.config.PluginConfig;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.exception.PluginException;

/**
 * 插件适配器接口
 * 不同类型的插件需要不同的适配器来处理特定的逻辑
 * 
 * @author yonchain
 */
public interface PluginAdapter {
    
    /**
     * 获取支持的插件类型
     * 
     * @return 插件类型
     */
    PluginType getSupportedType();
    
    /**
     * 插件安装时的回调
     * 
     * @param pluginConfig 插件配置
     */
    void onPluginInstall(PluginConfig pluginConfig);
    
    /**
     * 插件卸载时的回调
     * 
     * @param pluginId 插件ID
     */
    void onPluginUninstall(String pluginId);
    
    /**
     * 插件启用时的回调
     * 
     * @param pluginId 插件ID
     * @throws PluginException 插件异常
     */
    void onPluginEnable(String pluginId) throws PluginException;
    
    /**
     * 插件禁用时的回调
     * 
     * @param pluginId 插件ID
     * @throws PluginException 插件异常
     */
    void onPluginDisable(String pluginId) throws PluginException;
}

