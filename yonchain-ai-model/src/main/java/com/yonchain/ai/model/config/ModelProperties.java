package com.yonchain.ai.model.config;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型配置属性类
 * 用于绑定配置文件中的模型配置
 */
//@Component
@ConfigurationProperties(prefix = "yonchain.ai.models")
public class ModelProperties {
    
    /**
     * 聊天模型配置列表
     */
    private List<ChatModelConfig> chat = new ArrayList<>();
    
    /**
     * 图像模型配置列表
     */
    private List<ImageModelConfig> image = new ArrayList<>();
    
    /**
     * 音频模型配置列表
     */
    private List<AudioModelConfig> audio = new ArrayList<>();
    
    /**
     * 嵌入模型配置列表
     */
    private List<EmbeddingModelConfig> embedding = new ArrayList<>();
    
    // Getter和Setter方法
    public List<ChatModelConfig> getChat() {
        return chat;
    }
    
    public void setChat(List<ChatModelConfig> chat) {
        this.chat = chat;
    }
    
    public List<ImageModelConfig> getImage() {
        return image;
    }
    
    public void setImage(List<ImageModelConfig> image) {
        this.image = image;
    }
    
    public List<AudioModelConfig> getAudio() {
        return audio;
    }
    
    public void setAudio(List<AudioModelConfig> audio) {
        this.audio = audio;
    }
    
    public List<EmbeddingModelConfig> getEmbedding() {
        return embedding;
    }
    
    public void setEmbedding(List<EmbeddingModelConfig> embedding) {
        this.embedding = embedding;
    }
    
    /**
     * 获取所有模型配置
     * 
     * @return 所有模型配置列表
     */
    public List<ModelConfig> getAllModelConfigs() {
        List<ModelConfig> allConfigs = new ArrayList<>();
        
        // 添加聊天模型配置
        for (ChatModelConfig chatConfig : chat) {
            ModelConfig config = new ModelConfig();
            config.setName(chatConfig.getName());
            config.setProvider(chatConfig.getProvider());
            config.setType(ModelType.TEXT);
            config.setEndpoint(chatConfig.getEndpoint());
            config.setApiKey(chatConfig.getApiKey());
            config.setMaxTokens(chatConfig.getMaxTokens());
            config.setTemperature(chatConfig.getTemperature());
            config.setEnabled(chatConfig.getEnabled());
            config.setTimeout(chatConfig.getTimeout());
            config.setRetryCount(chatConfig.getRetryCount());
            config.setProperties(chatConfig.getProperties());
            allConfigs.add(config);
        }
        
        // 添加图像模型配置
        for (ImageModelConfig imageConfig : image) {
            ModelConfig config = new ModelConfig();
            config.setName(imageConfig.getName());
            config.setProvider(imageConfig.getProvider());
            config.setType(ModelType.IMAGE);
            config.setEndpoint(imageConfig.getEndpoint());
            config.setApiKey(imageConfig.getApiKey());
            config.setEnabled(imageConfig.getEnabled());
            config.setTimeout(imageConfig.getTimeout());
            config.setRetryCount(imageConfig.getRetryCount());
            config.setProperties(imageConfig.getProperties());
            allConfigs.add(config);
        }
        
        // 添加音频模型配置
        for (AudioModelConfig audioConfig : audio) {
            ModelConfig config = new ModelConfig();
            config.setName(audioConfig.getName());
            config.setProvider(audioConfig.getProvider());
            config.setType(ModelType.AUDIO);
            config.setEndpoint(audioConfig.getEndpoint());
            config.setApiKey(audioConfig.getApiKey());
            config.setEnabled(audioConfig.getEnabled());
            config.setTimeout(audioConfig.getTimeout());
            config.setRetryCount(audioConfig.getRetryCount());
            config.setProperties(audioConfig.getProperties());
            allConfigs.add(config);
        }
        
        // 添加嵌入模型配置
        for (EmbeddingModelConfig embeddingConfig : embedding) {
            ModelConfig config = new ModelConfig();
            config.setName(embeddingConfig.getName());
            config.setProvider(embeddingConfig.getProvider());
            config.setType(ModelType.EMBEDDING);
            config.setEndpoint(embeddingConfig.getEndpoint());
            config.setApiKey(embeddingConfig.getApiKey());
            config.setEnabled(embeddingConfig.getEnabled());
            config.setTimeout(embeddingConfig.getTimeout());
            config.setRetryCount(embeddingConfig.getRetryCount());
            config.setProperties(embeddingConfig.getProperties());
            allConfigs.add(config);
        }
        
        return allConfigs;
    }
}
