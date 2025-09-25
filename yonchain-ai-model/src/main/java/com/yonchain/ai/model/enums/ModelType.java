package com.yonchain.ai.model.enums;

/**
 * 模型工厂支持的模型类型枚举
 */
public enum ModelType {
    
    /**
     * 聊天模型 - 用于对话、文本生成等
     */
    CHAT("chat"),
    
    /**
     * 图像模型 - 用于图像生成、编辑等
     */
    IMAGE("image"),
    
    /**
     * 嵌入模型 - 用于文本向量化、语义搜索等
     */
    EMBEDDING("embedding"),
    
    /**
     * 音频模型 - 用于语音转文字、文字转语音等
     */
    AUDIO("audio");
    
    private final String code;
    
    ModelType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    /**
     * 根据代码获取模型类型
     * 
     * @param code 类型代码
     * @return 模型类型，如果不存在返回null
     */
    public static ModelType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (ModelType type : ModelType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return code;
    }
}
