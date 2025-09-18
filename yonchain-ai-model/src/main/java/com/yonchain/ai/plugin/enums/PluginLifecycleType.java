package com.yonchain.ai.plugin.enums;

/**
 * 插件生命周期事件类型枚举
 * 
 * @author yonchain
 */
public enum PluginLifecycleType {
    
    /**
     * 插件已安装
     */
    INSTALLED("installed", "插件已安装"),
    
    /**
     * 插件已卸载
     */
    UNINSTALLED("uninstalled", "插件已卸载"),
    
    /**
     * 插件已启用
     */
    ENABLED("enabled", "插件已启用"),
    
    /**
     * 插件已禁用
     */
    DISABLED("disabled", "插件已禁用"),
    
    /**
     * 插件已初始化
     */
    INITIALIZED("initialized", "插件已初始化"),
    
    /**
     * 插件已销毁
     */
    DESTROYED("destroyed", "插件已销毁"),
    
    /**
     * 插件配置已更新
     */
    CONFIG_UPDATED("config_updated", "插件配置已更新"),
    
    /**
     * 插件发生错误
     */
    ERROR("error", "插件发生错误");
    
    private final String code;
    private final String description;
    
    PluginLifecycleType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为成功事件
     * 
     * @return 是否为成功事件
     */
    public boolean isSuccessEvent() {
        return this != ERROR;
    }
    
    /**
     * 检查是否为状态变更事件
     * 
     * @return 是否为状态变更事件
     */
    public boolean isStatusChangeEvent() {
        return this == INSTALLED || 
               this == UNINSTALLED || 
               this == ENABLED || 
               this == DISABLED;
    }
    
    /**
     * 根据代码获取生命周期类型
     * 
     * @param code 类型代码
     * @return 生命周期类型
     * @throws IllegalArgumentException 如果代码无效
     */
    public static PluginLifecycleType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin lifecycle type code cannot be null or empty");
        }
        
        for (PluginLifecycleType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown plugin lifecycle type code: " + code);
    }
}

