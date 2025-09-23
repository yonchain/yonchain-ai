package com.yonchain.ai.plugin;

import com.yonchain.ai.model.registry.ModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件上下文默认实现
 * 为插件提供运行时需要的各种服务和组件
 * 
 * @author yonchain
 */
public class DefaultPluginContext implements PluginContext {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultPluginContext.class);
    
    private final ApplicationContext applicationContext;
   // private final ModelRegistry modelRegistry;
    private final Object modelPluginAdapter;
    private final String pluginId;
    private final String pluginWorkDirectory;
    
    // 插件配置存储
    private final Map<String, String> pluginConfig = new ConcurrentHashMap<>();
    
    /**
     * 构造函数
     * 
     * @param applicationContext Spring应用上下文
   //  * @param modelRegistry 模型注册中心
     * @param modelPluginAdapter 模型插件适配器
     * @param pluginId 插件ID
     * @param pluginWorkDirectory 插件工作目录
     */
    public DefaultPluginContext(ApplicationContext applicationContext,
                             // ModelRegistry modelRegistry,
                              Object modelPluginAdapter,
                              String pluginId,
                              String pluginWorkDirectory) {
        this.applicationContext = applicationContext;
       // this.modelRegistry = modelRegistry;
        this.modelPluginAdapter = modelPluginAdapter;
        this.pluginId = pluginId;
        this.pluginWorkDirectory = pluginWorkDirectory;
        
        // 确保插件工作目录存在
        ensureWorkDirectoryExists();
    }
    
    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    @Override
    public ModelRegistry getModelRegistry() {
        return null;//modelRegistry;
    }
    
    @Override
    public Object getModelPluginAdapter() {
        return modelPluginAdapter;
    }
    
    @Override
    public String getPluginConfig(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        
        // 先从插件特定配置中获取
        String value = pluginConfig.get(key);
        if (value != null) {
            return value;
        }
        
        // 如果没有找到，尝试从系统属性中获取（以插件ID为前缀）
        String systemPropertyKey = "plugin." + pluginId + "." + key;
        value = System.getProperty(systemPropertyKey);
        if (value != null) {
            return value;
        }
        
        // 最后尝试从环境变量中获取（转换为大写，下划线分隔）
        String envKey = ("PLUGIN_" + pluginId + "_" + key).toUpperCase().replace(".", "_").replace("-", "_");
        return System.getenv(envKey);
    }
    
    @Override
    public void setPluginConfig(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Plugin config key cannot be null or empty for plugin: {}", pluginId);
            return;
        }
        
        if (value == null) {
            pluginConfig.remove(key);
            log.debug("Removed plugin config: {} for plugin: {}", key, pluginId);
        } else {
            pluginConfig.put(key, value);
            log.debug("Set plugin config: {}={} for plugin: {}", key, value, pluginId);
        }
    }
    
    @Override
    public String getPluginWorkDirectory() {
        return pluginWorkDirectory;
    }
    
    @Override
    public void log(String level, String message, Object... args) {
        if (level == null) {
            level = "INFO";
        }
        
        // 为插件日志添加插件ID前缀
        String formattedMessage = "[Plugin:" + pluginId + "] " + message;
        
        switch (level.toUpperCase()) {
            case "TRACE":
                log.trace(formattedMessage, args);
                break;
            case "DEBUG":
                log.debug(formattedMessage, args);
                break;
            case "INFO":
                log.info(formattedMessage, args);
                break;
            case "WARN":
            case "WARNING":
                log.warn(formattedMessage, args);
                break;
            case "ERROR":
                log.error(formattedMessage, args);
                break;
            default:
                log.info(formattedMessage, args);
                break;
        }
    }
    
    /**
     * 获取插件ID
     * 
     * @return 插件ID
     */
    public String getPluginId() {
        return pluginId;
    }
    
    /**
     * 获取所有插件配置
     * 
     * @return 配置映射的副本
     */
    public Map<String, String> getAllPluginConfig() {
        return new ConcurrentHashMap<>(pluginConfig);
    }
    
    /**
     * 清理插件上下文
     * 在插件卸载时调用
     */
    public void cleanup() {
        pluginConfig.clear();
        log.debug("Plugin context cleaned up for plugin: {}", pluginId);
    }
    
    /**
     * 确保插件工作目录存在
     */
    private void ensureWorkDirectoryExists() {
        if (pluginWorkDirectory != null && !pluginWorkDirectory.trim().isEmpty()) {
            File workDir = new File(pluginWorkDirectory);
            if (!workDir.exists()) {
                if (workDir.mkdirs()) {
                    log.debug("Created plugin work directory: {} for plugin: {}", pluginWorkDirectory, pluginId);
                } else {
                    log.warn("Failed to create plugin work directory: {} for plugin: {}", pluginWorkDirectory, pluginId);
                }
            }
        }
    }
}
