package com.yonchain.ai.plugin.util;

import com.yonchain.ai.api.plugin.dto.PluginInfo;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;

import java.time.LocalDateTime;

/**
 * 插件信息转换工具类
 * 
 * @author yonchain
 */
public class PluginInfoConverter {
    
    /**
     * 从插件描述符创建插件信息
     * 
     * @param descriptor 插件描述符
     * @return 插件信息
     */
    public static PluginInfo fromDescriptor(PluginDescriptor descriptor) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(descriptor.getId());
        pluginInfo.setName(descriptor.getName());
        pluginInfo.setVersion(descriptor.getVersion());
        
        // 处理多语言描述，优先使用中文，其次英文
        String description = descriptor.getLocalizedDescription("zh_Hans");
        if (description == null) {
            description = descriptor.getLocalizedDescription("en_US");
        }
        pluginInfo.setDescription(description);
        
        pluginInfo.setAuthor(descriptor.getAuthor());
        
        // 直接设置插件类型字符串
        if (descriptor.getType() != null) {
            pluginInfo.setType(descriptor.getType());
        } else {
            pluginInfo.setType("model"); // 默认为model类型
        }
        
        pluginInfo.setPluginPath(descriptor.getPluginPath() != null ? descriptor.getPluginPath().toString() : null);
        pluginInfo.setMainClass(descriptor.getPluginClass());
        
        return pluginInfo;
    }
}




