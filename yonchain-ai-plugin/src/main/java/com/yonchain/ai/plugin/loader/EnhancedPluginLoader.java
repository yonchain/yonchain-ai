package com.yonchain.ai.plugin.loader;

import com.yonchain.ai.plugin.Plugin;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.generator.ConfigDrivenPluginGenerator;
import com.yonchain.ai.plugin.model.ModelPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 增强的插件加载器，支持自动生成
 */
@Component
public class EnhancedPluginLoader {
    
    private static final Logger log = LoggerFactory.getLogger(EnhancedPluginLoader.class);
    
    private final ConfigDrivenPluginGenerator pluginGenerator;
    private final PluginClassLoader pluginClassLoader;
    
    public EnhancedPluginLoader(ConfigDrivenPluginGenerator pluginGenerator,
                               PluginClassLoader pluginClassLoader) {
        this.pluginGenerator = pluginGenerator;
        this.pluginClassLoader = pluginClassLoader;
    }
    
    /**
     * 加载插件
     */
    public Plugin loadPlugin(PluginDescriptor descriptor) {
        try {
            log.info("Loading plugin: {} (type: {})", descriptor.getId(), descriptor.getType());
            
            // 检查插件类型
            if (PluginType.MODEL.getCode().equals(descriptor.getType())) {
                return loadModelPlugin(descriptor);
            }
            
            // 其他类型插件使用传统方式加载
            return loadTraditionalPlugin(descriptor);
            
        } catch (Exception e) {
            log.error("Failed to load plugin: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to load plugin: " + e.getMessage(), e);
        }
    }
    
    /**
     * 加载模型插件
     */
    private ModelPlugin loadModelPlugin(PluginDescriptor descriptor) {
        // 检查是否指定了plugin_class
        String pluginClass = descriptor.getPluginClass();
        
        if (pluginClass == null || pluginClass.trim().isEmpty()) {
            // 没有指定plugin_class，使用自动生成模式
            log.info("Using auto-generated plugin for: {}", descriptor.getId());
            return pluginGenerator.generateModelPlugin(descriptor);
        } else {
            // 指定了plugin_class，使用传统模式
            log.info("Using traditional plugin loading for: {}", descriptor.getId());
            return loadTraditionalModelPlugin(descriptor);
        }
    }
    
    /**
     * 加载传统模型插件
     */
    private ModelPlugin loadTraditionalModelPlugin(PluginDescriptor descriptor) {
        try {
            String pluginClassName = descriptor.getPluginClass();
            
            // 使用插件类加载器加载类
            Class<?> pluginClass = pluginClassLoader.loadClass(
                descriptor.getPluginPath(), pluginClassName);
            
            if (!ModelPlugin.class.isAssignableFrom(pluginClass)) {
                throw new IllegalArgumentException("Plugin class must implement ModelPlugin interface: " + pluginClassName);
            }
            
            // 创建实例
            @SuppressWarnings("unchecked")
            Class<? extends ModelPlugin> modelPluginClass = (Class<? extends ModelPlugin>) pluginClass;
            ModelPlugin plugin = modelPluginClass.getDeclaredConstructor().newInstance();
            
            log.debug("Successfully loaded traditional ModelPlugin: {}", pluginClassName);
            return plugin;
            
        } catch (Exception e) {
            log.error("Failed to load traditional model plugin: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to load traditional model plugin", e);
        }
    }
    
    /**
     * 加载传统插件
     */
    private Plugin loadTraditionalPlugin(PluginDescriptor descriptor) {
        try {
            String pluginClassName = descriptor.getPluginClass();
            if (pluginClassName == null || pluginClassName.trim().isEmpty()) {
                throw new IllegalArgumentException("Plugin class not specified for traditional plugin");
            }
            
            // 使用插件类加载器加载类
            Class<?> pluginClass = pluginClassLoader.loadClass(
                descriptor.getPluginPath(), pluginClassName);
            
            if (!Plugin.class.isAssignableFrom(pluginClass)) {
                throw new IllegalArgumentException("Plugin class must implement Plugin interface: " + pluginClassName);
            }
            
            // 创建实例
            @SuppressWarnings("unchecked")
            Class<? extends Plugin> pluginImplClass = (Class<? extends Plugin>) pluginClass;
            Plugin plugin = pluginImplClass.getDeclaredConstructor().newInstance();
            
            log.debug("Successfully loaded traditional plugin: {}", pluginClassName);
            return plugin;
            
        } catch (Exception e) {
            log.error("Failed to load traditional plugin: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to load traditional plugin", e);
        }
    }
}
