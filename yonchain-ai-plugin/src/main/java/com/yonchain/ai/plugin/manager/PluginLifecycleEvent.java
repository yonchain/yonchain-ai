package com.yonchain.ai.plugin.manager;

import com.yonchain.ai.plugin.enums.PluginLifecycleType;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 插件生命周期事件
 * 
 * @author yonchain
 */
public class PluginLifecycleEvent extends ApplicationEvent {
    
    private final String pluginId;
    private final PluginLifecycleType lifecycleType;
    private final String message;
    private final Throwable throwable;
    private final LocalDateTime timestamp;
    
    public PluginLifecycleEvent(Object source, String pluginId, PluginLifecycleType lifecycleType, String message) {
        this(source, pluginId, lifecycleType, message, null);
    }
    
    public PluginLifecycleEvent(Object source, String pluginId, PluginLifecycleType lifecycleType, String message, Throwable throwable) {
        super(source);
        this.pluginId = pluginId;
        this.lifecycleType = lifecycleType;
        this.message = message;
        this.throwable = throwable;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public PluginLifecycleType getLifecycleType() {
        return lifecycleType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Throwable getThrowable() {
        return throwable;
    }
    
    public LocalDateTime getEventTimestamp() {
        return timestamp;
    }
    
    /**
     * 检查是否为成功事件
     * 
     * @return 是否为成功事件
     */
    public boolean isSuccessEvent() {
        return lifecycleType.isSuccessEvent();
    }
    
    /**
     * 检查是否为状态变更事件
     * 
     * @return 是否为状态变更事件
     */
    public boolean isStatusChangeEvent() {
        return lifecycleType.isStatusChangeEvent();
    }
    
    /**
     * 检查是否为错误事件
     * 
     * @return 是否为错误事件
     */
    public boolean isErrorEvent() {
        return lifecycleType == PluginLifecycleType.ERROR;
    }
    
    /**
     * 检查是否有异常信息
     * 
     * @return 是否有异常信息
     */
    public boolean hasThrowable() {
        return throwable != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PluginLifecycleEvent{");
        sb.append("pluginId='").append(pluginId).append('\'');
        sb.append(", lifecycleType=").append(lifecycleType);
        sb.append(", message='").append(message).append('\'');
        sb.append(", timestamp=").append(timestamp);
        if (throwable != null) {
            sb.append(", throwable=").append(throwable.getClass().getSimpleName());
        }
        sb.append('}');
        return sb.toString();
    }
}
