package com.yonchain.ai.plugin.util;

import com.yonchain.ai.api.plugin.dto.PluginInfo;
import com.yonchain.ai.plugin.config.PluginConfig;


/**
 * 插件信息转换工具类
 * 
 * @author yonchain
 */
public class PluginInfoConverter {
    
    /**
     * 从插件配置创建插件信息
     * 
     * @param pluginConfig 插件配置
     * @return 插件信息
     */
    public static PluginInfo fromPluginConfig(PluginConfig pluginConfig) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(pluginConfig.getId());
        pluginInfo.setName(pluginConfig.getName());
        pluginInfo.setVersion(pluginConfig.getVersion());
        
        // 处理多语言描述，优先使用中文，其次英文
        String description = pluginConfig.getLocalizedDescription("zh_Hans");
        if (description == null) {
            description = pluginConfig.getLocalizedDescription("en_US");
        }
        pluginInfo.setDescription(description);
        
        pluginInfo.setAuthor(pluginConfig.getAuthor());
        
        // 直接设置插件类型字符串
        if (pluginConfig.getType() != null) {
            pluginInfo.setType(pluginConfig.getType());
        } else {
            pluginInfo.setType("model"); // 默认为model类型
        }
        
        pluginInfo.setPluginPath(pluginConfig.getPluginPath() != null ? pluginConfig.getPluginPath().toString() : null);
        pluginInfo.setMainClass(null); // PluginConfig 不再有 pluginClass 字段
        
        return pluginInfo;
    }
}




