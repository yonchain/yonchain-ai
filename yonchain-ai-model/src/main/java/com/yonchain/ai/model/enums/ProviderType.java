package com.yonchain.ai.model.enums;

/**
 * 模型提供商类型枚举
 */
public enum ProviderType {
    /**
     * 系统内置提供商
     */
    SYSTEM("system", "系统内置"),
    
    /**
     * 自定义提供商
     */
    CUSTOM("custom", "自定义");
    
    private final String code;
    private final String name;
    
    ProviderType(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public static ProviderType fromCode(String code) {
        for (ProviderType type : ProviderType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}