package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.model.provider.ModelProvider;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
// import org.springframework.ai.audio.transcription.AudioTranscriptionModel;
import org.springframework.ai.embedding.EmbeddingModel;

import java.util.Set;

/**
 * 模型工厂接口
 * 负责根据配置创建不同类型的模型实例
 */
public interface ModelFactory {
    
    /**
     * 创建聊天模型实例
     * 
     * @param config 模型配置
     * @return 聊天模型实例
     */
    ChatModel createChatModel(ModelConfig config);
    
    /**
     * 创建图像模型实例
     * 
     * @param config 模型配置
     * @return 图像模型实例
     */
    ImageModel createImageModel(ModelConfig config);
    
    /**
     * 创建音频模型实例
     * 
     * @param config 模型配置
     * @return 音频模型实例
     */
    default Object createAudioModel(ModelConfig config) {
        throw new UnsupportedOperationException("Audio model not supported yet");
    }
    
    /**
     * 创建嵌入模型实例
     * 
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    EmbeddingModel createEmbeddingModel(ModelConfig config);
    
    /**
     * 检查是否支持指定的模型类型
     * 
     * @param modelType 模型类型
     * @return 是否支持
     */
    boolean supports(ModelType modelType);
    
    /**
     * 检查是否支持指定的提供商
     * 
     * @param provider 提供商名称
     * @return 是否支持
     */
    boolean supportsProvider(String provider);
    
    /**
     * 注册模型提供商
     * 
     * @param provider 提供商名称
     * @param modelProvider 模型提供商实例
     */
    void registerProvider(String provider, ModelProvider modelProvider);
    
    /**
     * 注销模型提供商
     * 
     * @param provider 提供商名称
     */
    void unregisterProvider(String provider);
    
    /**
     * 获取所有支持的提供商
     * 
     * @return 提供商列表
     */
    Set<String> getSupportedProviders();
    
    /**
     * 获取所有支持的模型类型
     * 
     * @return 模型类型列表
     */
    Set<ModelType> getSupportedModelTypes();
}
