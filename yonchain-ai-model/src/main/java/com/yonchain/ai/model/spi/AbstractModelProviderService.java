package com.yonchain.ai.model.spi;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.vo.ModelCapability;

import java.util.List;
import java.util.Map;

/**
 * 模型提供商服务抽象类
 * 提供基本实现，子类可以根据需要覆盖特定方法
 */
public abstract class AbstractModelProviderService implements ModelProviderService {

    /**
     * 提供商配置
     */
    protected Map<String, Object> providerConfig;

    @Override
    public void initialize(Map<String, Object> config) {
        this.providerConfig = config;
        doInitialize(config);
    }

    /**
     * 子类实现的初始化方法
     * @param config 配置参数
     */
    protected abstract void doInitialize(Map<String, Object> config);

    @Override
    public boolean validateProviderConfig(Map<String, Object> config) {
        // 默认实现，子类可以覆盖
        return true;
    }

    @Override
    public boolean validateModelConfig(String modelCode, Map<String, Object> config) {
        // 默认实现，子类可以覆盖
        return true;
    }

    @Override
    public Map<String, Object> getProviderConfigSchema() {
        // 默认返回空的Schema，子类应该覆盖此方法
        return Map.of(
            "type", "object",
            "properties", Map.of(),
            "required", List.of()
        );
    }

    @Override
    public Map<String, Object> getModelConfigSchema(String modelCode) {
        // 默认返回空的Schema，子类应该覆盖此方法
        return Map.of(
            "type", "object",
            "properties", Map.of(),
            "required", List.of()
        );
    }

    @Override
    public List<ModelCapability> getModelCapabilities(String modelCode) {
        // 默认返回空列表，子类应该覆盖此方法
        return List.of();
    }

    @Override
    public List<AIModel> getModels() {
        // 默认返回通过listModels()获取的模型列表
        return listModels();
    }

    @Override
    public Map<String, ModelCapability> getCapabilities() {
        // 默认返回空的能力映射，子类应该覆盖此方法
        return Map.of();
    }
}
