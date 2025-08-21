package com.yonchain.ai.model.spi;

import com.yonchain.ai.model.entity.AiModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.vo.ModelCapability;

import java.util.List;
import java.util.Map;

/**
 * 模型提供商服务接口
 * 所有模型提供商都需要实现此接口，以便系统能够统一管理和调用不同的模型提供商
 */
public interface ModelProviderService {

    /**
     * 获取提供商信息
     * @return 提供商信息
     */
    ModelProvider getProviderInfo();

    /**
     * 获取提供商支持的所有模型列表
     * @return 模型列表
     */
    List<AiModel> listModels();

    /**
     * 获取指定模型信息
     * @param modelCode 模型代码
     * @return 模型信息
     */
    AiModel getModel(String modelCode);

    /**
     * 获取模型配置参数Schema
     * @param modelCode 模型代码
     * @return 配置参数Schema，JSON Schema格式
     */
    Map<String, Object> getModelConfigSchema(String modelCode);

    /**
     * 获取提供商配置参数Schema
     * @return 配置参数Schema，JSON Schema格式
     */
    Map<String, Object> getProviderConfigSchema();

    /**
     * 验证提供商配置是否有效
     * @param config 配置参数
     * @return 是否有效
     */
    boolean validateProviderConfig(Map<String, Object> config);

    /**
     * 验证模型配置是否有效
     * @param modelCode 模型代码
     * @param config 配置参数
     * @return 是否有效
     */
    boolean validateModelConfig(String modelCode, Map<String, Object> config);

    /**
     * 获取模型能力列表
     * @param modelCode 模型代码
     * @return 模型能力列表
     */
    List<ModelCapability> getModelCapabilities(String modelCode);

    /**
     * 初始化提供商
     * @param config 配置参数
     */
    void initialize(Map<String, Object> config);

    /**
     * 获取提供商支持的所有模型列表
     * 通过API获取实时模型列表（如果支持）
     * @return 模型列表
     */
    List<AiModel> getModels();

    /**
     * 获取提供商支持的能力映射
     * @return 能力映射，key为能力代码，value为能力详情
     */
    Map<String, ModelCapability> getCapabilities();
}
