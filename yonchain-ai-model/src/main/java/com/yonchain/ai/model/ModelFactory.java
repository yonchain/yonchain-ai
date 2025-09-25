package com.yonchain.ai.model;

import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.model.definition.ModelDefinition;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.model.Model;

/**
 * 模型工厂接口
 *
 * @author Cgy
 */
public interface ModelFactory {

    /**
     * 是否支持指定类型的模型
     *
     * @param modelType 模型类型：CHAT, IMAGE, EMBEDDING, AUDIO
     * @return 是否支持
     */
    boolean supports(ModelType modelType);

    /**
     * 根据模型类型Class和定义创建对应的模型实例
     *
     * @param <T>        模型类型，必须继承自Model接口
     * @param definition 模型定义
     * @param modelClass 模型类型的Class对象
     * @return 对应类型的模型实例
     * @throws IllegalArgumentException 如果模型类型不支持或无效
     */
    @SuppressWarnings("unchecked")
    default <T extends Model<?, ?>> T createModel(ModelDefinition definition, Class<T> modelClass) {
        if (ChatModel.class.isAssignableFrom(modelClass)) {
            return (T) createChatModel(definition);
        } else if (ImageModel.class.isAssignableFrom(modelClass)) {
            return (T) createImageModel(definition);
        } else if (EmbeddingModel.class.isAssignableFrom(modelClass)) {
            return (T) createEmbeddingModel(definition);
        } else {
            throw new IllegalArgumentException("Unsupported model class: " + modelClass.getName());
        }
    }

    /**
     * 根据模型定义创建聊天模型
     *
     * @param definition 模型定义
     * @return ChatModel实例
     * @throws UnsupportedOperationException 如果不支持该类型
     */
    default ChatModel createChatModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Chat model not supported by " + this.getClass().getSimpleName());
    }

    /**
     * 根据模型定义创建图像模型
     *
     * @param definition 模型定义
     * @return ImageModel实例
     * @throws UnsupportedOperationException 如果不支持该类型
     */
    default ImageModel createImageModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Image model not supported by " + this.getClass().getSimpleName());
    }

    /**
     * 根据模型定义创建嵌入模型
     *
     * @param definition 模型定义
     * @return EmbeddingModel实例
     * @throws UnsupportedOperationException 如果不支持该类型
     */
    default EmbeddingModel createEmbeddingModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Embedding model not supported by " + this.getClass().getSimpleName());
    }

}
