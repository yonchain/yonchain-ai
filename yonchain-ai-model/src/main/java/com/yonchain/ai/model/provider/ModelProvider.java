package com.yonchain.ai.model.provider;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
// import org.springframework.ai.audio.transcription.AudioTranscriptionModel;
import org.springframework.ai.embedding.EmbeddingModel;

/**
 * 模型提供商接口
 * 定义各种类型模型的创建方法
 * 每个提供商创建自己的模型实现类，实现Spring AI标准接口
 */
public interface ModelProvider {
    
    /**
     * 获取提供商名称
     * 
     * @return 提供商名称
     */
    String getProviderName();
    
    /**
     * 检查是否支持指定的模型类型
     * 
     * @param modelType 模型类型
     * @return 是否支持
     */
    boolean supports(ModelType modelType);
    
    /**
     * 创建聊天模型
     * 例如：DeepSeek提供商创建DeepSeekChatModel实现ChatModel接口
     * 
     * @param config 模型配置
     * @return 聊天模型实例
     */
    ChatModel createChatModel(ModelConfig config);
    
    /**
     * 创建图像模型
     * 例如：OpenAI提供商创建OpenAIImageModel实现ImageModel接口
     * 
     * @param config 模型配置
     * @return 图像模型实例
     */
    ImageModel createImageModel(ModelConfig config);
    
    /**
     * 创建音频模型
     * 
     * @param config 模型配置
     * @return 音频模型实例
     */
    default Object createAudioModel(ModelConfig config) {
        throw new UnsupportedOperationException("Audio model not supported yet");
    }
    
    /**
     * 创建嵌入模型
     * 
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    EmbeddingModel createEmbeddingModel(ModelConfig config);
    
    /**
     * 验证配置是否有效
     * 
     * @param config 模型配置
     * @return 是否有效
     */
    boolean validateConfig(ModelConfig config);
    
    /**
     * 测试模型连接
     * 
     * @param config 模型配置
     * @return 连接是否成功
     */
    boolean testConnection(ModelConfig config);
    
}
