/*
package com.yonchain.ai.tmpl;

*/
/**
 * 模型类型枚举
 * 定义系统支持的所有模型类型
 *//*

public enum ModelType {

    */
/**
     * 文本模型 - 用于对话、文本生成等
     *//*

    TEXT("text", "文本模型"),
    
    */
/**
     * 图像模型 - 用于图像生成、编辑等
     *//*

    IMAGE("image", "图像模型"),
    
    */
/**
     * 音频模型 - 用于语音转文字、文字转语音等
     *//*

    AUDIO("audio", "音频模型"),
    
    */
/**
     * 视频模型 - 用于视频生成、编辑等
     *//*

    VIDEO("video", "视频模型"),
    
    */
/**
     * 多模态模型 - 支持多种输入输出类型
     *//*

    MULTIMODAL("multimodal", "多模态模型"),
    
    */
/**
     * 嵌入模型 - 用于文本向量化、语义搜索等
     *//*

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
    
    */
/**
     * 根据代码获取模型类型
     * 
     * @param code 类型代码
     * @return 模型类型，如果不存在返回null
     *//*

    public static ModelType fromCode(String code) {
        for (ModelType type : ModelType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return name + "(" + code + ")";
    }
}


*/
