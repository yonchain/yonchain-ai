package com.yonchain.ai.model.registry;

import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;

import java.util.Collection;
import java.util.List;

/**
 * 模型注册表接口
 * <p>
 * 负责注册和管理模型静态信息
 * 核心功能：
 * 1. 能获取所有注册的提供商列表
 * 2. 根据提供商获取所有的对应的模型列表
 * 3. 支持注册提供商、模型
 *
 * @author Cgy
 */
public interface ModelRegistry {

    /**
     * 获取所有注册的提供商列表
     *
     * @return 所有已注册的提供商列表
     */
    List<ModelProvider> getProviders();

    /**
     * 根据模型id获取模型信息
     *
     * @param modelId 模型ID
     * @return 模型信息
     */
    ModelInfo getModels(String modelId);

    /**
     * 根据提供商获取所有的对应的模型列表
     *
     * @param providerCode 提供商代码
     * @return 该提供商下的所有模型列表
     */
    List<ModelInfo> getModelsByProvider(String providerCode);

    /**
     * 注册提供商
     *
     * @param provider 提供商信息
     */
    void registerProvider(ModelProvider provider);

    /**
     * 注册模型
     *
     * @param model 模型信息
     */
    void registerModel(ModelInfo model);

    /**
     * 批量注册提供商
     *
     * @param providers 提供商列表
     */
    void registerProviders(Collection<? extends ModelProvider> providers);

    /**
     * 批量注册模型
     *
     * @param models 模型列表
     */
    void registerModels(Collection<? extends ModelInfo> models);
}