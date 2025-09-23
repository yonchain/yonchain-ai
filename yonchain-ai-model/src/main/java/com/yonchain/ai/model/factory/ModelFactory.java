package com.yonchain.ai.model.factory;

/**
 * 模型工厂接口
 * 
 * 每个命名空间（如openai、deepseek、anthropic等）都需要实现此接口
 * 提供该命名空间下所有类型模型的创建能力
 */
public interface ModelFactory extends ChatModelFactory, ImageModelFactory, EmbeddingModelFactory {
    
    /**
     * 获取命名空间名称
     * 
     * @return 命名空间名称，如 "openai"、"deepseek"、"anthropic"
     */
    String namespace();
    
    /**
     * 是否支持指定类型的模型
     * 
     * @param modelType 模型类型：CHAT, IMAGE, EMBEDDING, AUDIO
     * @return 是否支持
     */
    boolean supports(String modelType);
}
