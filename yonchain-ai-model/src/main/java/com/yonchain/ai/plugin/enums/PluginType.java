package com.yonchain.ai.plugin.enums;

/**
 * 插件类型枚举
 * 
 * @author yonchain
 */
public enum PluginType {
    
    /**
     * 模型插件
     */
    MODEL("model", "模型插件"),
    
    /**
     * 工具插件
     */
    TOOL("tool", "工具插件"),
    
    /**
     * UI插件
     */
    UI("ui", "UI插件"),
    
    /**
     * 工作流插件
     */
    WORKFLOW("workflow", "工作流插件"),
    
    /**
     * 集成插件
     */
    INTEGRATION("integration", "集成插件"),
    
    /**
     * 其他插件
     */
    OTHER("other", "其他插件");
    
    private final String code;
    private final String description;
    
    PluginType(String code, String description) {
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
     * 根据代码获取插件类型
     * 
     * @param code 类型代码
     * @return 插件类型
     * @throws IllegalArgumentException 如果代码无效
     */
    public static PluginType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin type code cannot be null or empty");
        }
        
        for (PluginType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown plugin type code: " + code);
    }
    
    /**
     * 检查代码是否有效
     * 
     * @param code 类型代码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        try {
            fromCode(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

