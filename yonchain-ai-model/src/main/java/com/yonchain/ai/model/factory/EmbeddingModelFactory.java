package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.registry.OptionsHandlerRegistry;
import org.springframework.ai.embedding.EmbeddingModel;

/**
 * 嵌入模型工厂接口
 */
public interface EmbeddingModelFactory {
    
    /**
     * 创建嵌入模型
     * 
     * @param definition 模型定义（包含所有必要的配置信息）
     * @return Spring AI EmbeddingModel实例
     */
    EmbeddingModel createEmbeddingModel(ModelDefinition definition);
}
