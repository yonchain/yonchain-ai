package com.yonchain.ai.plugin.generator;

import com.yonchain.ai.plugin.model.DefaultModelPlugin;
import com.yonchain.ai.plugin.config.*;
import com.yonchain.ai.plugin.loader.PluginClassLoader;
import com.yonchain.ai.plugin.model.ModelPlugin;
import com.yonchain.ai.plugin.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置驱动的插件生成器
 */
@Component
public class ConfigDrivenPluginGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigDrivenPluginGenerator.class);
    
    private final ConfigDrivenParser configParser;
    private final PluginClassLoader pluginClassLoader;
    
    public ConfigDrivenPluginGenerator(ConfigDrivenParser configParser, 
                                     PluginClassLoader pluginClassLoader) {
        this.configParser = configParser;
        this.pluginClassLoader = pluginClassLoader;
    }
    
    /**
     * 生成自动插件（新版本，直接接受必要参数）
     */
    public ModelPlugin generateModelPlugin(String pluginId, String pluginPath, 
                                         PluginConfig pluginConfig, 
                                         ProviderConfig providerConfig, 
                                         List<ModelConfigData> modelConfigs) {
        try {
            log.info("Generating auto plugin for: {}", pluginId);
            
            // 加载ModelProvider类
            ModelProvider provider = loadModelProvider(providerConfig, pluginPath);
            
            // 创建自动生成的插件
            DefaultModelPlugin plugin = new DefaultModelPlugin(
                pluginConfig, providerConfig, modelConfigs, provider);
            
            log.info("Successfully generated auto plugin: {}", pluginId);
            return plugin;
            
        } catch (Exception e) {
            log.error("Failed to generate auto plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to generate auto plugin", e);
        }
    }
    
    
    
    /**
     * 加载ModelProvider类（新版本）
     */
    private ModelProvider loadModelProvider(ProviderConfig providerConfig, String pluginPath) {
        try {
            String providerClassName = providerConfig.getProviderSource();
            if (providerClassName == null || providerClassName.trim().isEmpty()) {
                throw new IllegalArgumentException("Provider source class not specified");
            }
            
            // 使用插件类加载器加载类
            Class<?> providerClass = pluginClassLoader.loadClass(pluginPath, providerClassName);
            
            if (!ModelProvider.class.isAssignableFrom(providerClass)) {
                throw new IllegalArgumentException("Provider class must implement ModelProvider interface: " + providerClassName);
            }
            
            // 创建实例
            @SuppressWarnings("unchecked")
            Class<? extends ModelProvider> modelProviderClass = (Class<? extends ModelProvider>) providerClass;
            ModelProvider provider = modelProviderClass.getDeclaredConstructor().newInstance();
            
            log.debug("Successfully loaded ModelProvider: {}", providerClassName);
            return provider;
            
        } catch (Exception e) {
            log.error("Failed to load ModelProvider from path: {}", pluginPath, e);
            throw new RuntimeException("Failed to load ModelProvider", e);
        }
    }
    
}





