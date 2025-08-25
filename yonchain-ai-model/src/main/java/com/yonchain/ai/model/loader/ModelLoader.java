package com.yonchain.ai.model.loader;

import com.yonchain.ai.api.model.DefaultModel;
import com.yonchain.ai.api.model.DefaultModelProvider;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;

import java.util.Collection;
import java.util.List;

/**
 * 模型加载器接口
 * <p>
 * 负责从不同来源（如YAML文件、数据库等）加载模型和提供商的配置信息
 * 遵循单一职责原则，只负责加载，不负责存储
 * 
 * @author Cgy
 */
public interface ModelLoader {

    /**
     * 加载所有提供商配置
     * 
     * @return 所有提供商配置
     */
    Collection<? extends ModelProvider> loadProviders();

    /**
     * 加载所有模型配置
     * 
     * @return 所有模型配置
     */
    Collection<? extends ModelInfo> loadModels();
}