package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.registry.OptionsHandlerRegistry;
import org.springframework.ai.image.ImageModel;

/**
 * 图像模型工厂接口
 */
public interface ImageModelFactory {
    
    /**
     * 创建图像模型
     * 
     * @param definition 模型定义
     * @param typeHandlerRegistry 类型处理器注册中心
     * @return Spring AI ImageModel实例
     */
    ImageModel createImageModel(ModelDefinition definition, OptionsHandlerRegistry typeHandlerRegistry);
}
