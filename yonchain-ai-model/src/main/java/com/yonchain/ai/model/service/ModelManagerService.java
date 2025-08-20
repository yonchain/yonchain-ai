package com.yonchain.ai.model.service;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.vo.ModelCapability;

import java.util.List;
import java.util.Map;

/**
 * 模型管理服务接口
 */
public interface ModelManagerService {

    /**
     * 获取所有模型提供商列表
     * @return 提供商列表
     */
    List<ModelProvider> listProviders();

    /**
     * 获取指定提供商信息
     * @param providerCode 提供商代码
     * @return 提供商信息
     */
    ModelProvider getProvider(String providerCode);

    /**
     * 获取指定提供商的所有模型列表
     * @param providerCode 提供商代码
     * @return 模型列表
     */
    List<AIModel> listModelsByProvider(String providerCode);

    /**
     * 获取所有模型列表
     * @return 模型列表
     */
    List<AIModel> listAllModels();

    /**
     * 获取指定模型信息
     * @param modelCode 模型代码
     * @return 模型信息
     */
    AIModel getModel(String modelCode);

    /**
     * 添加自定义模型提供商
     * @param provider 提供商信息
     * @param config 配置参数
     * @return 添加后的提供商信息
     */
    ModelProvider addProvider(ModelProvider provider, Map<String, Object> config);

    /**
     * 更新模型提供商
     * @param provider 提供商信息
     * @param config 配置参数
     * @return 更新后的提供商信息
     */
    ModelProvider updateProvider(ModelProvider provider, Map<String, Object> config);

    /**
     * 删除模型提供商
     * @param providerCode 提供商代码
     * @return 是否删除成功
     */
    boolean deleteProvider(String providerCode);

    /**
     * 添加自定义模型
     * @param model 模型信息
     * @param config 配置参数
     * @return 添加后的模型信息
     */
    AIModel addModel(AIModel model, Map<String, Object> config);

    /**
     * 更新模型
     * @param model 模型信息
     * @param config 配置参数
     * @return 更新后的模型信息
     */
    AIModel updateModel(AIModel model, Map<String, Object> config);

    /**
     * 删除模型
     * @param modelCode 模型代码
     * @return 是否删除成功
     */
    boolean deleteModel(String modelCode);

    /**
     * 获取模型能力列表
     * @param modelCode 模型代码
     * @return 模型能力列表
     */
    List<ModelCapability> getModelCapabilities(String modelCode);

    /**
     * 获取指定类型的所有模型
     * @param modelType 模型类型
     * @return 模型列表
     */
    List<AIModel> listModelsByType(String modelType);

    /**
     * 启用或禁用模型提供商
     * @param providerCode 提供商代码
     * @param enabled 是否启用
     * @return 更新后的提供商信息
     */
    ModelProvider enableProvider(String providerCode, boolean enabled);

    /**
     * 启用或禁用模型
     * @param modelCode 模型代码
     * @param enabled 是否启用
     * @return 更新后的模型信息
     */
    AIModel enableModel(String modelCode, boolean enabled);
}