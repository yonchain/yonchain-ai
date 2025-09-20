package com.yonchain.ai.plugin.model;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.provider.ModelProvider;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.plugin.PluginAdapter;
import com.yonchain.ai.plugin.PluginContext;
import com.yonchain.ai.plugin.DefaultPluginContext;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.loader.PluginClassLoader;
import com.yonchain.ai.plugin.registry.PluginRegistry;
import com.yonchain.ai.plugin.exception.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型插件适配器
 * 处理模型插件的特定逻辑
 * 
 * @author yonchain
 */
@Component
public class ModelPluginAdapter implements PluginAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(ModelPluginAdapter.class);
    
    private final PluginRegistry pluginRegistry;
    private final ModelRegistry modelRegistry;
    private final ModelFactory modelFactory;
    private final PluginClassLoader pluginClassLoader;
    private final ApplicationContext applicationContext;
    
    // 缓存插件实例和提供商
    private final Map<String, ModelPlugin> pluginInstances = new ConcurrentHashMap<>();
    private final Map<String, ModelProvider> modelProviders = new ConcurrentHashMap<>();
    
    // 缓存插件上下文
    private final Map<String, DefaultPluginContext> pluginContexts = new ConcurrentHashMap<>();
    
    public ModelPluginAdapter(PluginRegistry pluginRegistry, 
                             ModelRegistry modelRegistry,
                             ModelFactory modelFactory,
                              PluginClassLoader pluginClassLoader,
                             ApplicationContext applicationContext) {
        this.pluginRegistry = pluginRegistry;
        this.modelRegistry = modelRegistry;
        this.modelFactory = modelFactory;
        this.pluginClassLoader = pluginClassLoader;
        this.applicationContext = applicationContext;
    }
    
    @Override
    public PluginType getSupportedType() {
        return PluginType.MODEL;
    }
    
    @Override
    public void onPluginInstall(PluginDescriptor descriptor) {
        log.info("Installing model plugin: {}", descriptor.getId());
        
        try {
         /*   // 验证插件是否有必要的SPI配置
            if (descriptor.getSpi() == null || !descriptor.getSpi().hasProviderSource()) {
                throw new IllegalArgumentException("Model plugin must have provider source configuration");
            }*/
            
            // 暂时不加载插件实例，等到启用时再加载
            log.info("Model plugin installed successfully: {}", descriptor.getId());
            
        } catch (Exception e) {
            log.error("Failed to install model plugin: {}", descriptor.getId(), e);
            throw new RuntimeException("Failed to install model plugin: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onPluginUninstall(String pluginId) {
        log.info("Uninstalling model plugin: {}", pluginId);
        
        try {
            // 先确保插件已禁用
            if (pluginInstances.containsKey(pluginId)) {
                log.warn("Plugin {} is still enabled, disabling before uninstall", pluginId);
                onPluginDisable(pluginId);
            }
            
            // 清理缓存
            pluginInstances.remove(pluginId);
            modelProviders.remove(pluginId);
            
            log.info("Model plugin uninstalled successfully: {}", pluginId);
            
        } catch (Exception e) {
            log.error("Failed to uninstall model plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to uninstall model plugin: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onPluginEnable(String pluginId) throws PluginException {
        log.info("Enabling model plugin: {}", pluginId);
        
        try {
            // 1. 获取插件信息
            Optional<PluginInfo> pluginInfoOpt = pluginRegistry.findByPluginId(pluginId);
            if (!pluginInfoOpt.isPresent()) {
                throw new PluginException("Plugin not found: " + pluginId);
            }
            
            PluginInfo pluginInfo = pluginInfoOpt.get();
            
            // 2. 加载插件实例（直接使用PluginInfo，不需要依赖PluginDescriptor）
            ModelPlugin pluginInstance = loadPluginInstance(pluginInfo);
            if (pluginInstance == null) {
                throw new PluginException("Failed to load plugin instance: " + pluginId);
            }
            
            // 3. 获取模型提供商
            ModelProvider modelProvider = pluginInstance.getProvider();
            if (modelProvider == null) {
                throw new PluginException("Plugin does not provide a model provider: " + pluginId);
            }
            
            // 4. 注册模型提供商到Spring容器
            registerModelProvider(pluginId, modelProvider);
            
            // 5. 注册模型提供商到模型工厂
            modelFactory.registerProvider(modelProvider.getProviderName(), modelProvider);
            
            // 6. 注册模型到模型注册中心
            List<ModelMetadata> models = pluginInstance.getModels();
            if (models != null && !models.isEmpty()) {
                for (ModelMetadata model : models) {
                    // 检查并补充ModelConfig信息
                    ensureModelConfigComplete(model, pluginId, pluginInstance);
                    modelRegistry.registerModel(model);
                    log.debug("Registered model: {} from plugin: {}", model.getModelId(), pluginId);
                }
            }
            
            // 7. 调用插件的启用回调
            pluginInstance.onEnable();
            
            // 8. 缓存插件实例和提供商
            pluginInstances.put(pluginId, pluginInstance);
            modelProviders.put(pluginId, modelProvider);
            
            log.info("Model plugin enabled successfully: {}", pluginId);
            
        } catch (Exception e) {
            log.error("Failed to enable model plugin: {}", pluginId, e);
            
            // 清理可能的残留状态
            cleanup(pluginId);
            
            throw new PluginException("Failed to enable model plugin: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onPluginDisable(String pluginId) throws PluginException {
        log.info("Disabling model plugin: {}", pluginId);
        
        try {
            // 1. 获取插件实例
            ModelPlugin pluginInstance = pluginInstances.get(pluginId);
            if (pluginInstance == null) {
                log.warn("Plugin instance not found, may already be disabled: {}", pluginId);
                return;
            }
            
            // 2. 获取模型提供商
            ModelProvider modelProvider = pluginInstance.getProvider();
            
            // 3. 注销模型
            List<ModelMetadata> models = pluginInstance.getModels();
            if (models != null && !models.isEmpty()) {
                for (ModelMetadata model : models) {
                    modelRegistry.unregisterModel(model.getModelId());
                    log.debug("Unregistered model: {} from plugin: {}", model.getModelId(), pluginId);
                }
            }
            
            // 4. 调用插件的禁用回调
            pluginInstance.onDisable();
            
            // 5. 调用插件的销毁方法
            pluginInstance.dispose();
            
            // 6. 从模型工厂注销模型提供商
            if (modelProvider != null) {
                modelFactory.unregisterProvider(modelProvider.getProviderName());
            }
            
            // 7. 从Spring容器注销模型提供商
            unregisterModelProvider(pluginId);
            
            // 8. 清理插件上下文
            DefaultPluginContext pluginContext = pluginContexts.remove(pluginId);
            if (pluginContext != null) {
                pluginContext.cleanup();
                log.debug("Plugin context cleaned up for plugin: {}", pluginId);
            }
            
            // 9. 清理缓存
            pluginInstances.remove(pluginId);
            modelProviders.remove(pluginId);
            
            log.info("Model plugin disabled successfully: {}", pluginId);
            
        } catch (Exception e) {
            log.error("Failed to disable model plugin: {}", pluginId, e);
            
            // 即使失败也要清理缓存，避免状态不一致
            cleanup(pluginId);
            
            throw new PluginException("Failed to disable model plugin: " + e.getMessage(), e);
        }
    }
    
    /**
     * 加载插件实例
     * 
     * @param pluginInfo 插件信息
     * @return 插件实例
     */
    private ModelPlugin loadPluginInstance(PluginInfo pluginInfo) {
        try {
            String providerSource = pluginInfo.getMainClass();
            String pluginPath = pluginInfo.getPluginPath();
            
            if (providerSource == null || providerSource.trim().isEmpty()) {
                throw new IllegalArgumentException("Plugin main class is not specified for plugin: " + pluginInfo.getPluginId());
            }
            
            if (pluginPath == null || pluginPath.trim().isEmpty()) {
                throw new IllegalArgumentException("Plugin path is not specified for plugin: " + pluginInfo.getPluginId());
            }
            
            Class<?> pluginClass = pluginClassLoader.loadClass(pluginPath, providerSource);
            
            if (!ModelPlugin.class.isAssignableFrom(pluginClass)) {
                throw new IllegalArgumentException("Plugin class must implement ModelPlugin interface: " + providerSource);
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends ModelPlugin> modelPluginClass = (Class<? extends ModelPlugin>) pluginClass;
            
            // 创建插件实例
            ModelPlugin instance = modelPluginClass.getDeclaredConstructor().newInstance();
            
            // 创建插件上下文
            PluginContext pluginContext = createPluginContext(pluginInfo);
            
            // 初始化插件
            instance.initialize(pluginContext);
            
            return instance;
            
        } catch (Exception e) {
            log.error("Failed to load plugin instance: {}", pluginInfo.getPluginId(), e);
            return null;
        }
    }
    
    /**
     * 创建插件上下文
     * 
     * @param pluginInfo 插件信息
     * @return 插件上下文
     */
    private PluginContext createPluginContext(PluginInfo pluginInfo) {
        String pluginId = pluginInfo.getPluginId();
        
        // 检查是否已存在上下文
        DefaultPluginContext existingContext = pluginContexts.get(pluginId);
        if (existingContext != null) {
            log.debug("Reusing existing plugin context for plugin: {}", pluginId);
            return existingContext;
        }
        
        // 创建插件工作目录路径
        String baseWorkDir = System.getProperty("yonchain.plugin.work-dir", 
                                               System.getProperty("java.io.tmpdir") + "/yonchain-plugins");
        String pluginWorkDirectory = baseWorkDir + "/" + pluginId + "/work";
        
        // 创建并缓存插件上下文
        DefaultPluginContext pluginContext = new DefaultPluginContext(
            applicationContext,
            modelRegistry,
            this, // 传递自身作为模型插件适配器
            pluginId,
            pluginWorkDirectory
        );
        
        pluginContexts.put(pluginId, pluginContext);
        log.debug("Created and cached plugin context for plugin: {}", pluginId);
        
        return pluginContext;
    }
    
    /**
     * 注册模型提供商到Spring容器
     * 
     * @param pluginId 插件ID
     * @param modelProvider 模型提供商
     */
    private void registerModelProvider(String pluginId, ModelProvider modelProvider) {
        try {
            if (applicationContext instanceof ConfigurableApplicationContext) {
                ConfigurableListableBeanFactory beanFactory = 
                    ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                
                String beanName = "modelProvider_" + pluginId;
                beanFactory.registerSingleton(beanName, modelProvider);
                
                log.debug("Registered model provider as Spring bean: {}", beanName);
            } else {
                log.warn("Application context is not configurable, cannot register model provider as bean");
            }
        } catch (Exception e) {
            log.error("Failed to register model provider for plugin: {}", pluginId, e);
        }
    }
    
    /**
     * 从Spring容器注销模型提供商
     * 
     * @param pluginId 插件ID
     */
    private void unregisterModelProvider(String pluginId) {
        try {
            if (applicationContext instanceof ConfigurableApplicationContext) {
                ConfigurableListableBeanFactory beanFactory = 
                    ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                
                String beanName = "modelProvider_" + pluginId;
                
                // 检查是否存在该singleton bean
                if (beanFactory.containsSingleton(beanName)) {
                    // 如果bean factory是DefaultListableBeanFactory，可以移除singleton
                    if (beanFactory instanceof DefaultListableBeanFactory) {
                        DefaultListableBeanFactory defaultBeanFactory = 
                            (DefaultListableBeanFactory) beanFactory;
                        
                        // 销毁singleton bean实例
                        defaultBeanFactory.destroySingleton(beanName);
                        log.info("Successfully unregistered model provider bean: {}", beanName);
                    } else {
                        log.warn("BeanFactory is not DefaultListableBeanFactory, cannot destroy singleton: {}", beanName);
                    }
                }
                
                // 如果存在bean定义，也要移除
                if (beanFactory instanceof BeanDefinitionRegistry) {
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                    
                    if (registry.containsBeanDefinition(beanName)) {
                        registry.removeBeanDefinition(beanName);
                        log.debug("Removed bean definition: {}", beanName);
                    }
                }
                
            } else {
                log.warn("Application context is not configurable, cannot unregister model provider");
            }
        } catch (Exception e) {
            log.error("Failed to unregister model provider for plugin: {}", pluginId, e);
        }
    }
    
    /**
     * 清理插件相关资源
     * 
     * @param pluginId 插件ID
     */
    private void cleanup(String pluginId) {
        try {
            // 获取模型提供商（在移除前）
            ModelProvider modelProvider = modelProviders.get(pluginId);
            
            // 清理插件实例和提供商
            pluginInstances.remove(pluginId);
            modelProviders.remove(pluginId);
            
            // 清理插件上下文
            DefaultPluginContext pluginContext = pluginContexts.remove(pluginId);
            if (pluginContext != null) {
                pluginContext.cleanup();
                log.debug("Plugin context cleaned up for plugin: {}", pluginId);
            }
            
            // 从模型工厂注销模型提供商
            if (modelProvider != null) {
                modelFactory.unregisterProvider(modelProvider.getProviderName());
            }
            
            // 从Spring容器注销模型提供商
            unregisterModelProvider(pluginId);
            
            log.debug("Cleaned up all resources for plugin: {}", pluginId);
        } catch (Exception e) {
            log.error("Failed to cleanup resources for plugin: {}", pluginId, e);
        }
    }
    
    /**
     * 获取插件实例
     * 
     * @param pluginId 插件ID
     * @return 插件实例
     */
    public Optional<ModelPlugin> getPluginInstance(String pluginId) {
        return Optional.ofNullable(pluginInstances.get(pluginId));
    }
    
    /**
     * 获取模型提供商
     * 
     * @param pluginId 插件ID
     * @return 模型提供商
     */
    public Optional<ModelProvider> getModelProvider(String pluginId) {
        return Optional.ofNullable(modelProviders.get(pluginId));
    }
    
    /**
     * 获取所有已启用的模型插件
     * 
     * @return 插件实例映射
     */
    public Map<String, ModelPlugin> getEnabledPlugins() {
        return new ConcurrentHashMap<>(pluginInstances);
    }
    
    /**
     * 确保模型元数据包含完整的ModelConfig
     * 
     * @param metadata 模型元数据
     * @param pluginId 插件ID
     * @param pluginInstance 插件实例
     */
    private void ensureModelConfigComplete(ModelMetadata metadata, String pluginId, ModelPlugin pluginInstance) {
        if (metadata.getConfig() == null) {
            log.debug("Creating default ModelConfig for model: {} from plugin: {}", metadata.getModelId(), pluginId);
            ModelConfig config = createDefaultModelConfig(metadata, pluginId);
            metadata.setConfig(config);
        } else {
            log.debug("Supplementing existing ModelConfig for model: {} from plugin: {}", metadata.getModelId(), pluginId);
            supplementModelConfig(metadata.getConfig(), pluginId);
        }
    }
    
    /**
     * 为插件模型创建默认配置
     * 
     * @param metadata 模型元数据
     * @param pluginId 插件ID
     * @return 模型配置
     */
    private ModelConfig createDefaultModelConfig(ModelMetadata metadata, String pluginId) {
        ModelConfig config = new ModelConfig();
        config.setName(metadata.getName());
        config.setProvider(metadata.getProvider());
        config.setType(metadata.getType());
        config.setEnabled(true);
        
        // 设置默认超时和重试
        config.setTimeout(30000); // 30秒
        config.setRetryCount(3);
        
        // 从模型元数据设置最大Token
        if (metadata.getMaxTokens() != null) {
            config.setMaxTokens(metadata.getMaxTokens());
        }
        
        // 补充运行时配置
        supplementModelConfig(config, pluginId);
        
        return config;
    }
    
    /**
     * 补充模型配置的运行时信息
     * 
     * @param config 模型配置
     * @param pluginId 插件ID
     */
    private void supplementModelConfig(ModelConfig config, String pluginId) {
        try {
            DefaultPluginContext pluginContext = pluginContexts.get(pluginId);
            if (pluginContext == null) {
                log.warn("Plugin context not found for plugin: {}, using environment variables", pluginId);
                supplementFromEnvironment(config);
                return;
            }
            
            // 从插件上下文获取用户配置
            String apiKey = pluginContext.getPluginConfig("api_key");
            String endpoint = pluginContext.getPluginConfig("endpoint_url");
            
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                config.setApiKey(apiKey);
                log.debug("Set API key from plugin context for model: {}", config.getName());
            } else {
                // 从环境变量获取API Key
                supplementApiKeyFromEnvironment(config);
            }
            
            if (endpoint != null && !endpoint.trim().isEmpty()) {
                config.setEndpoint(endpoint);
                log.debug("Set endpoint from plugin context for model: {} -> {}", config.getName(), endpoint);
            } else {
                // 设置默认endpoint
                setDefaultEndpoint(config);
            }
            
            // 获取其他可选配置
            supplementOptionalConfig(config, pluginContext);
            
        } catch (Exception e) {
            log.error("Failed to supplement model config for plugin: {}, falling back to environment", pluginId, e);
            supplementFromEnvironment(config);
        }
    }
    
    /**
     * 从环境变量补充配置
     * 
     * @param config 模型配置
     */
    private void supplementFromEnvironment(ModelConfig config) {
        supplementApiKeyFromEnvironment(config);
        setDefaultEndpoint(config);
    }
    
    /**
     * 从环境变量获取API Key
     * 
     * @param config 模型配置
     */
    private void supplementApiKeyFromEnvironment(ModelConfig config) {
        String provider = config.getProvider();
        if (provider == null) {
            return;
        }
        
        // 根据提供商构建环境变量名
        String envVarName = provider.toUpperCase() + "_API_KEY";
        String apiKey = System.getenv(envVarName);
        
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            config.setApiKey(apiKey);
            log.debug("Set API key from environment variable {} for model: {}", envVarName, config.getName());
        } else {
            log.warn("API key not found in environment variable {} for model: {}", envVarName, config.getName());
        }
    }
    
    /**
     * 设置默认endpoint
     * 
     * @param config 模型配置
     */
    private void setDefaultEndpoint(ModelConfig config) {
        String provider = config.getProvider();
        if (provider == null) {
            return;
        }
        
        // 根据提供商设置默认endpoint
        String defaultEndpoint = getDefaultEndpointForProvider(provider);
        if (defaultEndpoint != null) {
            config.setEndpoint(defaultEndpoint);
            log.debug("Set default endpoint for provider {} -> {}", provider, defaultEndpoint);
        }
    }
    
    /**
     * 获取提供商的默认endpoint
     * 
     * @param provider 提供商名称
     * @return 默认endpoint
     */
    private String getDefaultEndpointForProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "deepseek":
                return "https://api.deepseek.com/v1";
            case "openai":
                return "https://api.openai.com/v1";
            case "anthropic":
                return "https://api.anthropic.com/v1";
            case "grok":
                return "https://api.x.ai/v1";
            default:
                return null;
        }
    }
    
    /**
     * 补充可选配置
     * 
     * @param config 模型配置
     * @param pluginContext 插件上下文
     */
    private void supplementOptionalConfig(ModelConfig config, DefaultPluginContext pluginContext) {
        try {
            // 温度参数
            String temperatureStr = pluginContext.getPluginConfig("temperature");
            if (temperatureStr != null) {
                try {
                    double temperature = Double.parseDouble(temperatureStr);
                    config.setTemperature(temperature);
                } catch (NumberFormatException e) {
                    log.warn("Invalid temperature value: {} for model: {}", temperatureStr, config.getName());
                }
            }
            
            // 最大Token数
            String maxTokensStr = pluginContext.getPluginConfig("max_tokens");
            if (maxTokensStr != null) {
                try {
                    int maxTokens = Integer.parseInt(maxTokensStr);
                    config.setMaxTokens(maxTokens);
                } catch (NumberFormatException e) {
                    log.warn("Invalid max_tokens value: {} for model: {}", maxTokensStr, config.getName());
                }
            }
            
            // 超时配置
            String timeoutStr = pluginContext.getPluginConfig("timeout");
            if (timeoutStr != null) {
                try {
                    int timeout = Integer.parseInt(timeoutStr);
                    config.setTimeout(timeout);
                } catch (NumberFormatException e) {
                    log.warn("Invalid timeout value: {} for model: {}", timeoutStr, config.getName());
                }
            }
            
        } catch (Exception e) {
            log.debug("No additional optional config found or error reading config for model: {}", config.getName());
        }
    }
    
}
