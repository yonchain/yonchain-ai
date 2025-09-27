package com.yonchain.ai.model;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.model.options.ModelOptionsHandler;
import com.yonchain.ai.model.options.ModelOptionsHandlerRegistry;
import org.springframework.ai.model.ModelOptions;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * 模型配置管理中心
 * <p>
 * 负责管理整个模型系统的配置信息，包括：
 * - 全局配置属性
 * - 模型注册中心
 * - 命名空间工厂注册中心
 * - 类型处理器注册中心
 */
public class ModelConfiguration {

    private ModelRegistry modelRegistry;
    private ModelFactoryRegistry modelFactoryRegistry;
    private ModelOptionsHandlerRegistry optionsHandlerRegistry;
    private ModelEnvironment environment;

    public ModelConfiguration() {
        this.environment = new ModelEnvironment("model");
        this.modelRegistry = new ModelRegistry();
        this.modelFactoryRegistry = new ModelFactoryRegistry();
        this.optionsHandlerRegistry = new ModelOptionsHandlerRegistry();
    }

    public ModelConfiguration(ModelEnvironment environment) {
        this.environment = environment;
        this.modelRegistry = new ModelRegistry();
        this.modelFactoryRegistry = new ModelFactoryRegistry();
        this.optionsHandlerRegistry = new ModelOptionsHandlerRegistry();
    }

    // ================== 环境配置管理 ==================

    public ModelEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(ModelEnvironment environment) {
        this.environment = environment;
    }

    // ================== 配置属性管理 ==================

    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        environment.setProperty(key, value);
    }

    public Properties getProperties() {
        return environment.getProperties();
    }

    public void setProperties(Properties properties) {
        environment.setProperties(properties);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return environment.getBooleanProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        return environment.getIntProperty(key, defaultValue);
    }

    public long getLongProperty(String key, long defaultValue) {
        return environment.getLongProperty(key, defaultValue);
    }

    // ================== Model Registry Methods ==================

    /**
     * 注册模型定义
     */
    public void registerModel(ModelDefinition definition) {
        modelRegistry.registerModel(definition);
    }

    /**
     * 注销模型定义
     *
     * @param modelId
     */
    public void unregisterModel(String modelId) {
        String[] model = modelId.split(":");
        modelRegistry.removeModel(model[0],model[1]);
    }

    /**
     * 获取模型定义
     */
    public Optional<ModelDefinition> getModelDefinition(String namespace, String modelId) {
        return modelRegistry.getModelDefinition(namespace, modelId);
    }

    /**
     * 获取指定命名空间下的所有模型
     */
    public List<ModelDefinition> getModelsByNamespace(String namespace) {
        return modelRegistry.getModelsByNamespace(namespace);
    }

    /**
     * 获取指定类型的所有模型
     */
    public List<ModelDefinition> getModelsByType(String modelType) {
        return modelRegistry.getModelsByType(modelType);
    }

    /**
     * 获取所有模型定义
     */
    public List<ModelDefinition> getAllModels() {
        return modelRegistry.getAllModels();
    }

    // ================== Model Factory Registry Methods ==================

    /**
     * 注册模型工厂
     */
    public void registerFactory(String namespace, ModelFactory factory) {
        modelFactoryRegistry.registerFactory(namespace, factory);
    }

    /**
     * 获取模型工厂
     */
    public Optional<ModelFactory> getFactory(String namespace) {
        return modelFactoryRegistry.getFactory(namespace);
    }

    /**
     * 检查模型工厂是否存在
     */
    public boolean containsFactory(String namespace) {
        return modelFactoryRegistry.containsFactory(namespace);
    }

    /**
     * 移除模型工厂
     */
    public Optional<ModelFactory> removeFactory(String namespace) {
        return modelFactoryRegistry.removeFactory(namespace);
    }

    /**
     * 获取所有注册的命名空间
     */
    public Set<String> getRegisteredNamespaces() {
        return modelFactoryRegistry.getRegisteredNamespaces();
    }

    /**
     * 检查指定命名空间是否支持指定类型的模型
     */
    public boolean supportsModelType(String namespace, ModelType modelType) {
        return modelFactoryRegistry.supportsModelType(namespace, modelType);
    }

    /**
     * 检查指定命名空间是否支持指定类型的模型（字符串版本）
     */
    public boolean supportsModelType(String namespace, String modelType) {
        return modelFactoryRegistry.supportsModelType(namespace, modelType);
    }

    /**
     * 获取工厂数量
     */
    public int getFactoryCount() {
        return modelFactoryRegistry.size();
    }

    // ================== Options Handler Registry Methods ==================

    /**
     * 注册命名空间级Handler
     */
    public void registerNamespaceHandler(String namespace, String type, ModelOptionsHandler<?> handler) {
        optionsHandlerRegistry.registerNamespaceHandler(namespace, type, handler);
    }

    /**
     * 注册模型级Handler
     */
    public void registerModelHandler(String namespace, String modelId, ModelOptionsHandler<?> handler) {
        optionsHandlerRegistry.registerModelHandler(namespace, modelId, handler);
    }

    /**
     * 通过类名注册命名空间级Handler
     */
    public void registerNamespaceHandlerByClass(String namespace, String type, String handlerClass) {
        optionsHandlerRegistry.registerNamespaceHandlerByClass(namespace, type, handlerClass);
    }

    /**
     * 通过类名注册模型级Handler
     */
    public void registerModelHandlerByClass(String namespace, String modelId, String handlerClass) {
        optionsHandlerRegistry.registerModelHandlerByClass(namespace, modelId, handlerClass);
    }

    /**
     * 解析选项处理器
     */
    public <T extends ModelOptions> ModelOptionsHandler<T> resolveHandler(String namespace, String modelId, String type, String explicitHandlerClass) {
        return optionsHandlerRegistry.resolveHandler(namespace, modelId, type, explicitHandlerClass);
    }

    /**
     * 根据键获取选项处理器
     */
    public <T extends ModelOptions> Optional<ModelOptionsHandler<T>> getHandler(String key) {
        return optionsHandlerRegistry.getHandler(key);
    }

    // ================== 工具方法 ==================

    // Convenience methods
    public boolean isCacheEnabled() {
        return Boolean.parseBoolean(getProperty("cache.enabled", "true"));
    }

    public int getDefaultTimeout() {
        return Integer.parseInt(getProperty("default.timeout", "30"));
    }

    public boolean isOpenAiCompatibilityEnabled() {
        return Boolean.parseBoolean(getProperty("openai.compatibility.enabled", "true"));
    }

    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }
}
