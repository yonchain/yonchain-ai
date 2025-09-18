package com.yonchain.ai.plugin.enums;

/**
 * 插件状态枚举
 * 
 * @author yonchain
 */
public enum PluginStatus {
    
    /**
     * 未安装
     */
    NOT_INSTALLED("not_installed", "未安装"),
    
    /**
     * 安装中
     */
    INSTALLING("installing", "安装中"),
    
    /**
     * 已安装但禁用
     */
    INSTALLED_DISABLED("installed_disabled", "已安装(禁用)"),
    
    /**
     * 启用中
     */
    ENABLING("enabling", "启用中"),
    
    /**
     * 已安装且启用
     */
    INSTALLED_ENABLED("installed_enabled", "已安装(启用)"),
    
    /**
     * 禁用中
     */
    DISABLING("disabling", "禁用中"),
    
    /**
     * 卸载中
     */
    UNINSTALLING("uninstalling", "卸载中"),
    
    /**
     * 安装失败
     */
    INSTALL_FAILED("install_failed", "安装失败"),
    
    /**
     * 启用失败
     */
    ENABLE_FAILED("enable_failed", "启用失败"),
    
    /**
     * 禁用失败
     */
    DISABLE_FAILED("disable_failed", "禁用失败"),
    
    /**
     * 卸载失败
     */
    UNINSTALL_FAILED("uninstall_failed", "卸载失败");
    
    private final String code;
    private final String description;
    
    PluginStatus(String code, String description) {
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
     * 检查状态是否为最终状态（不会再变化）
     * 
     * @return 是否为最终状态
     */
    public boolean isFinalState() {
        return this == INSTALLED_DISABLED || 
               this == INSTALLED_ENABLED || 
               this == NOT_INSTALLED ||
               this == INSTALL_FAILED ||
               this == ENABLE_FAILED ||
               this == DISABLE_FAILED ||
               this == UNINSTALL_FAILED;
    }
    
    /**
     * 检查状态是否为过渡状态（正在进行中）
     * 
     * @return 是否为过渡状态
     */
    public boolean isTransitionState() {
        return this == INSTALLING || 
               this == ENABLING || 
               this == DISABLING || 
               this == UNINSTALLING;
    }
    
    /**
     * 检查状态是否为失败状态
     * 
     * @return 是否为失败状态
     */
    public boolean isFailedState() {
        return this == INSTALL_FAILED ||
               this == ENABLE_FAILED ||
               this == DISABLE_FAILED ||
               this == UNINSTALL_FAILED;
    }
    
    /**
     * 检查插件是否可用
     * 
     * @return 插件是否可用
     */
    public boolean isAvailable() {
        return this == INSTALLED_ENABLED;
    }
    
    /**
     * 根据代码获取插件状态
     * 
     * @param code 状态代码
     * @return 插件状态
     * @throws IllegalArgumentException 如果代码无效
     */
    public static PluginStatus fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin status code cannot be null or empty");
        }
        
        for (PluginStatus status : values()) {
            if (status.code.equalsIgnoreCase(code.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown plugin status code: " + code);
    }
}

