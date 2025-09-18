package com.yonchain.ai.plugin;

import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.provider.ModelProvider;
import com.yonchain.ai.model.registry.ModelRegistry;

import java.util.List;

/**
 * 模型插件接口
 * 模型插件需要实现此接口
 * 
 * @author yonchain
 */
public interface ModelPlugin extends Plugin {
    
    /**
     * 获取模型提供商
     * 一个插件对应一个模型提供商
     * 
     * @return 模型提供商实例
     */
    ModelProvider getProvider();
    
    /**
     * 获取此插件提供的所有模型元数据
     * 
     * @return 模型列表
     */
    List<ModelMetadata> getModels();
    
    /**
     * 插件启用时的回调
     * 在此方法中注册模型到系统
     */
    void onEnable();
    
    /**
     * 插件禁用时的回调
     * 在此方法中从系统注销模型
     */
    void onDisable();
    
    /**
     * 将模型注册到系统注册中心
     * 
     * @param registry 模型注册中心
     */
    void registerModels(ModelRegistry registry);
    
    /**
     * 从系统注册中心注销模型
     * 
     * @param registry 模型注册中心
     */
    void unregisterModels(ModelRegistry registry);
}

