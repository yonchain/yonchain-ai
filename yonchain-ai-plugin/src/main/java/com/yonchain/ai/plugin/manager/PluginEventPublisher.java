package com.yonchain.ai.plugin.manager;

import com.yonchain.ai.plugin.enums.PluginLifecycleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 插件生命周期事件发布器
 * 
 * @author yonchain
 */
@Component
public class PluginEventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(PluginEventPublisher.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public PluginEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * 发布插件已安装事件
     * 
     * @param pluginId 插件ID
     */
    public void publishInstalled(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.INSTALLED, "Plugin installed");
    }
    
    /**
     * 发布插件已卸载事件
     * 
     * @param pluginId 插件ID
     */
    public void publishUninstalled(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.UNINSTALLED, "Plugin uninstalled");
    }
    
    /**
     * 发布插件已启用事件
     * 
     * @param pluginId 插件ID
     */
    public void publishEnabled(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.ENABLED, "Plugin enabled");
    }
    
    /**
     * 发布插件已禁用事件
     * 
     * @param pluginId 插件ID
     */
    public void publishDisabled(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.DISABLED, "Plugin disabled");
    }
    
    /**
     * 发布插件已初始化事件
     * 
     * @param pluginId 插件ID
     */
    public void publishInitialized(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.INITIALIZED, "Plugin initialized");
    }
    
    /**
     * 发布插件已销毁事件
     * 
     * @param pluginId 插件ID
     */
    public void publishDestroyed(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.DESTROYED, "Plugin destroyed");
    }
    
    /**
     * 发布插件配置已更新事件
     * 
     * @param pluginId 插件ID
     */
    public void publishConfigUpdated(String pluginId) {
        publishEvent(pluginId, PluginLifecycleType.CONFIG_UPDATED, "Plugin config updated");
    }
    
    /**
     * 发布插件错误事件
     * 
     * @param pluginId 插件ID
     * @param error 错误信息
     */
    public void publishError(String pluginId, String error) {
        publishEvent(pluginId, PluginLifecycleType.ERROR, error);
    }
    
    /**
     * 发布插件错误事件
     * 
     * @param pluginId 插件ID
     * @param throwable 异常
     */
    public void publishError(String pluginId, Throwable throwable) {
        String error = throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
        publishEvent(pluginId, PluginLifecycleType.ERROR, error, throwable);
    }
    
    /**
     * 发布生命周期事件
     * 
     * @param pluginId 插件ID
     * @param lifecycleType 生命周期类型
     * @param message 消息
     */
    private void publishEvent(String pluginId, PluginLifecycleType lifecycleType, String message) {
        publishEvent(pluginId, lifecycleType, message, null);
    }
    
    /**
     * 发布生命周期事件
     * 
     * @param pluginId 插件ID
     * @param lifecycleType 生命周期类型
     * @param message 消息
     * @param throwable 异常（可选）
     */
    private void publishEvent(String pluginId, PluginLifecycleType lifecycleType, String message, Throwable throwable) {
        try {
            PluginEvent event = new PluginEvent(this, pluginId, lifecycleType, message, throwable);
            eventPublisher.publishEvent(event);
            
            log.debug("Published plugin lifecycle event: {} - {} - {}", pluginId, lifecycleType, message);
        } catch (Exception e) {
            log.error("Failed to publish plugin lifecycle event: {} - {} - {}", pluginId, lifecycleType, message, e);
        }
    }
}

