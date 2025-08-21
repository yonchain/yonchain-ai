package com.yonchain.ai.api.model.enums;

/**
 * 模型类型枚举
 */
public enum ModelType {

    /**
     * 文本模型
     */
    TEXT("text", "文本模型"),
    
    /**
     * 图像模型
     */
    IMAGE("image", "图像模型"),
    
    /**
     * 音频模型
     */
    AUDIO("audio", "音频模型"),
    
    /**
     * 视频模型
     */
    VIDEO("video", "视频模型"),
    
    /**
     * 多模态模型
     */
    MULTIMODAL("multimodal", "多模态模型"),
    
    /**
     * 嵌入模型
     */
    EMBEDDING("embedding", "嵌入模型");
    
    private final String code;
    private final String name;
    
    ModelType(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public static ModelType fromCode(String code) {
        for (ModelType type : ModelType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}