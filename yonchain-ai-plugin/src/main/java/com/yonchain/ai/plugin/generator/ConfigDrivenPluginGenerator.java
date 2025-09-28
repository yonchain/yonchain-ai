package com.yonchain.ai.plugin.generator;

import com.yonchain.ai.plugin.model.DefaultModelPlugin;
import com.yonchain.ai.plugin.config.*;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.loader.PluginClassLoader;
import com.yonchain.ai.plugin.model.ModelPlugin;
import com.yonchain.ai.plugin.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
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
     * 生成自动插件
     */
    public ModelPlugin generateModelPlugin(PluginDescriptor descriptor) {
        try {
            log.info("Generating auto plugin for: {}", descriptor.getId());
            
            // 1. 解析插件配置
            PluginConfig pluginConfig = parsePluginConfig(descriptor);
            
            // 2. 解析提供商配置
            ProviderConfig providerConfig = parseProviderConfig(descriptor, pluginConfig);
            
            // 3. 解析模型配置
            List<ModelConfigData> modelConfigs = parseModelConfigs(descriptor);
            
            // 4. 加载ModelProvider类
            ModelProvider provider = loadModelProvider(providerConfig, descriptor);
            
            // 5. 创建自动生成的插件
            DefaultModelPlugin plugin = new DefaultModelPlugin(
                pluginConfig, providerConfig, modelConfigs, provider);
            
            log.info("Successfully generated auto plugin: {}", descriptor.getId());
            return plugin;
            
        } catch (Exception e) {
            log.error("Failed to generate auto plugin: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to generate auto plugin", e);
        }
    }
    
    /**
     * 解析插件配置
     */
    private PluginConfig parsePluginConfig(PluginDescriptor descriptor) {
        try (InputStream inputStream = descriptor.getConfigInputStream("plugin.yaml")) {
            return configParser.parsePluginConfig(inputStream);
        } catch (Exception e) {
            log.error("Failed to parse plugin config for: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to parse plugin config", e);
        }
    }
    
    /**
     * 解析提供商配置
     */
    private ProviderConfig parseProviderConfig(PluginDescriptor descriptor, PluginConfig pluginConfig) {
        try {
            // 从plugin.yaml中获取提供商配置文件名
            String providerConfigFile = "deepseek.yaml"; // 默认值
            
            if (pluginConfig.getPlugins() != null && !pluginConfig.getPlugins().isEmpty()) {
                providerConfigFile = pluginConfig.getPlugins().get(0);
            }
            
            try (InputStream inputStream = descriptor.getConfigInputStream(providerConfigFile)) {
                return configParser.parseProviderConfig(inputStream);
            }
            
        } catch (Exception e) {
            log.error("Failed to parse provider config for: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to parse provider config", e);
        }
    }
    
    /**
     * 解析模型配置
     */
    private List<ModelConfigData> parseModelConfigs(PluginDescriptor descriptor) {
        List<ModelConfigData> modelConfigs = new ArrayList<>();
        
        try {
            // 获取模型配置文件列表
            List<String> modelConfigFiles = descriptor.getModelConfigFiles();
            
            for (String configFile : modelConfigFiles) {
                try (InputStream inputStream = descriptor.getConfigInputStream(configFile)) {
                    ModelConfigData modelConfig = configParser.parseModelConfig(inputStream);
                    modelConfigs.add(modelConfig);
                } catch (Exception e) {
                    log.warn("Failed to parse model config file: {}, skipping", configFile, e);
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to parse model configs for: {}", descriptor.getId(), e);
        }
        
        return modelConfigs;
    }
    
    /**
     * 加载ModelProvider类
     */
    private ModelProvider loadModelProvider(ProviderConfig providerConfig, PluginDescriptor descriptor) {
        try {
            String providerClassName = providerConfig.getProviderSource();
            if (providerClassName == null || providerClassName.trim().isEmpty()) {
                throw new IllegalArgumentException("Provider source class not specified");
            }
            
            // 使用插件类加载器加载类
            Class<?> providerClass = pluginClassLoader.loadClass(
                descriptor.getPluginPath(), providerClassName);
            
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
            log.error("Failed to load ModelProvider for: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to load ModelProvider", e);
        }
    }
}
