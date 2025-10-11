package com.yonchain.ai.plugin.model;

import com.yonchain.ai.business.ModelMetadata;
import com.yonchain.ai.plugin.ModelProvider;
import com.yonchain.ai.plugin.spi.ProviderMetadata;
import com.yonchain.ai.plugin.config.ProviderConfig;
import com.yonchain.ai.model.ModelRegistry;
import com.yonchain.ai.model.ModelConfiguration;
import com.yonchain.ai.plugin.Plugin;

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
     * 获取提供商元数据信息
     * 包含从插件配置文件中读取的信息
     *
     * @return 提供商元数据
     */
    ProviderMetadata getProviderMetadata();

    /**
     * 获取提供商配置信息
     * 包含从插件配置文件中读取的原始配置数据
     *
     * @return 提供商配置
     */
    ProviderConfig getProviderConfig();

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
    
    /**
     * 注册模型选项处理器到ModelConfiguration
     * 插件启动时调用此方法注册自己的OptionsHandler
     * 
     * @param modelConfiguration 模型配置实例
     */
    default void registerOptionsHandlers(ModelConfiguration modelConfiguration) {
        // 默认实现为空，插件可以选择性实现
    }
    
    /**
     * 从ModelConfiguration注销模型选项处理器
     * 插件禁用时调用此方法清理自己的OptionsHandler
     * 
     * @param modelConfiguration 模型配置实例
     */
    default void unregisterOptionsHandlers(ModelConfiguration modelConfiguration) {
        // 默认实现为空，插件可以选择性实现
    }

}

