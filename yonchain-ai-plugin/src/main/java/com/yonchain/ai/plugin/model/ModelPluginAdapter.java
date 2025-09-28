package com.yonchain.ai.plugin.model;

import com.yonchain.ai.api.model.ModelService;
import com.yonchain.ai.api.model.ModelProviderInfo;
import com.yonchain.ai.api.model.DefaultModelProvider;
import com.yonchain.ai.api.model.ModelConfigItem;
import com.yonchain.ai.model.ModelConfiguration;
import com.yonchain.ai.model.ModelRegistry;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.plugin.*;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.loader.EnhancedPluginLoader;
import com.yonchain.ai.plugin.registry.PluginRegistry;
import com.yonchain.ai.plugin.service.PluginIconService;
import com.yonchain.ai.plugin.exception.PluginException;
import com.yonchain.ai.plugin.spi.ProviderMetadata;
import com.yonchain.ai.plugin.config.ProviderConfig;
import com.yonchain.ai.plugin.loader.PluginClassLoader;
import com.yonchain.ai.tmpl.ModelMetadata;

import java.io.InputStream;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final EnhancedPluginLoader enhancedPluginLoader;
    private final PluginClassLoader pluginClassLoader;
    private final ApplicationContext applicationContext;
    private final ModelService modelService;
    private final PluginIconService pluginIconService;
    private final ModelConfiguration modelConfiguration;
    
    // 缓存插件实例和提供商
    private final Map<String, ModelPlugin> pluginInstances = new ConcurrentHashMap<>();
    private final Map<String, ModelProvider> modelProviders = new ConcurrentHashMap<>();
    
    // 缓存插件上下文
    private final Map<String, DefaultPluginContext> pluginContexts = new ConcurrentHashMap<>();
    
    public ModelPluginAdapter(PluginRegistry pluginRegistry, 
                             ModelRegistry modelRegistry,
                             EnhancedPluginLoader enhancedPluginLoader,
                             PluginClassLoader pluginClassLoader,
                             ApplicationContext applicationContext,
                             ModelService modelService,
                             PluginIconService pluginIconService,
                             ModelConfiguration modelConfiguration) {
        this.pluginRegistry = pluginRegistry;
        this.modelRegistry = modelRegistry;
        this.enhancedPluginLoader = enhancedPluginLoader;
        this.pluginClassLoader = pluginClassLoader;
        this.applicationContext = applicationContext;
        this.modelService = modelService;
        this.pluginIconService = pluginIconService;
        this.modelConfiguration = modelConfiguration;
    }
    
    @Override
    public PluginType getSupportedType() {
        return PluginType.MODEL;
    }
    
    @Override
    public void onPluginInstall(PluginDescriptor descriptor) {
        log.info("Installing model plugin: {}", descriptor.getId());
        
        try {
            // 验证模型插件的配置完整性
            validateModelPluginConfiguration(descriptor);
            
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
            
            // 5. 创建适配器，将ModelProvider适配为ModelFactory
            PluginModelFactory factoryAdapter = new PluginModelFactory(modelProvider);
            
            // 6. 注册适配器到ModelConfiguration（符合ModelFactory标准）
            modelConfiguration.registerFactory(modelProvider.getProviderName(), factoryAdapter);

            // 7. 保存提供商信息到数据库（用于可视化界面展示和配置）
            ModelProviderInfo providerInfo = convertToProviderInfo(pluginInstance, modelProvider, pluginId);
            modelService.saveProvider(pluginId, providerInfo);
            
            // 8. 保存模型信息到数据库（用于可视化界面展示和配置）
            List<ModelMetadata> models = pluginInstance.getModels();
            if (models != null && !models.isEmpty()) {
                List<Object> modelObjects = new ArrayList<>(models);
                modelService.saveModels(pluginId, modelObjects, modelProvider.getProviderName());
                log.debug("Saved {} models to database from plugin: {}", models.size(), pluginId);
            }
            
            // 9. 注册OptionsHandlers到ModelConfiguration
            registerPluginOptionsHandlers(pluginInstance, modelConfiguration);
            
            // 10. 调用插件的启用回调
            pluginInstance.onEnable();
            
            // 11. 缓存插件实例和提供商
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
            
            // 3. 从数据库删除插件相关数据
            modelService.removePluginData(pluginId);
            log.debug("Removed plugin data from database: {}", pluginId);
            
            // 4. 注销OptionsHandlers
            unregisterPluginOptionsHandlers(pluginInstance, modelConfiguration);
            
            // 5. 调用插件的禁用回调
            pluginInstance.onDisable();
            
            // 6. 调用插件的销毁方法
            pluginInstance.dispose();
            
            // 7. 从ModelConfiguration注销模型工厂
            if (modelProvider != null) {
                modelConfiguration.removeFactory(modelProvider.getProviderName());
                log.debug("Removed factory from ModelConfiguration: {}", modelProvider.getProviderName());
            }
            
            // 8. 从Spring容器注销模型提供商
            unregisterModelProvider(pluginId);
            
            // 9. 清理插件上下文
            DefaultPluginContext pluginContext = pluginContexts.remove(pluginId);
            if (pluginContext != null) {
                pluginContext.cleanup();
                log.debug("Plugin context cleaned up for plugin: {}", pluginId);
            }
            
            // 10. 清理缓存
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
            // 创建插件描述符
            PluginDescriptor descriptor = createPluginDescriptor(pluginInfo);
            
            // 使用增强的插件加载器
            ModelPlugin instance = (ModelPlugin) enhancedPluginLoader.loadPlugin(descriptor);
            
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
     * 从插件信息创建插件描述符
     */
    private PluginDescriptor createPluginDescriptor(PluginInfo pluginInfo) {
        PluginDescriptor descriptor = new PluginDescriptor();
        
        descriptor.setId(pluginInfo.getPluginId());
        descriptor.setName(pluginInfo.getName());
        descriptor.setVersion(pluginInfo.getVersion());
        descriptor.setAuthor(pluginInfo.getAuthor());
        descriptor.setType(pluginInfo.getType());
        descriptor.setPluginClass(pluginInfo.getMainClass());
        descriptor.setPluginPath(java.nio.file.Paths.get(pluginInfo.getPluginPath()));
        
        return descriptor;
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
            
            // 从ModelConfiguration注销模型工厂
            if (modelProvider != null) {
                try {
                    modelConfiguration.removeFactory(modelProvider.getProviderName());
                    log.debug("Cleaned up factory from ModelConfiguration: {}", modelProvider.getProviderName());
                } catch (Exception e) {
                    log.error("Failed to cleanup factory from ModelConfiguration: {}", modelProvider.getProviderName(), e);
                }
            }
            
            // 从Spring容器注销模型提供商
            unregisterModelProvider(pluginId);
            
            // 从数据库清理插件数据
            try {
                modelService.removePluginData(pluginId);
                log.debug("Cleaned up plugin data from database: {}", pluginId);
            } catch (Exception e) {
                log.error("Failed to cleanup plugin data from database: {}", pluginId, e);
            }
            
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
     * 将运行时ModelProvider转换为界面用的ModelProviderInfo
     * 
     * @param modelProvider 运行时模型提供商
     * @param pluginId 插件ID
     * @return 界面用的提供商信息
     */
    private ModelProviderInfo convertToProviderInfo(ModelPlugin pluginInstance, ModelProvider modelProvider, String pluginId) {
        ModelProviderInfo providerInfo = new DefaultModelProvider();
        
        try {
            // 从插件获取元数据
           ProviderMetadata metadata = pluginInstance.getProviderMetadata();
            
            if (metadata != null) {
                // 使用插件元数据
                providerInfo.setCode(metadata.getProvider());
                providerInfo.setName(metadata.getLocalizedLabel("zh_Hans"));
                providerInfo.setDescription(metadata.getLocalizedDescription("zh_Hans"));
                
                // 生成图标URL
                Map<String, String> iconSmall = metadata.getIconSmall();
                String iconFileName = null;
                if (iconSmall != null) {
                    iconFileName = iconSmall.getOrDefault("en_US", iconSmall.values().stream().findFirst().orElse(null));
                }
                if (iconFileName != null) {
                    String iconUrl = generateProviderIconUrl(pluginId, iconFileName);
                    providerInfo.setIcon(iconUrl);
                }
                
                // 从元数据设置支持的模型类型
                if (metadata.getSupportedModelTypes() != null) {
                    providerInfo.setSupportedModelTypes(metadata.getSupportedModelTypes());
                } else {
                    providerInfo.setSupportedModelTypes(getSupportedModelTypes(modelProvider));
                }
                
                // 设置配置Schema
                if (metadata.getProviderCredentialSchema() != null) {
                    List<ModelConfigItem> configSchemas = convertCredentialSchemaToConfigItems(metadata.getProviderCredentialSchema());
                    providerInfo.setConfigSchemas(configSchemas);
                    log.debug("Converted {} credential schema items for provider: {}", configSchemas.size(), metadata.getProvider());
                }
                
            } else {
                // 降级使用硬编码信息
                log.warn("No metadata available for provider: {}, using fallback values", modelProvider.getProviderName());
                providerInfo.setCode(modelProvider.getProviderName());
                providerInfo.setName(getProviderDisplayName(modelProvider.getProviderName()));
                providerInfo.setDescription(getProviderDescription(modelProvider.getProviderName()));
                
                // 生成默认图标URL
                String defaultIconUrl = generateDefaultProviderIconUrl(pluginId, modelProvider.getProviderName());
                providerInfo.setIcon(defaultIconUrl);
                
                providerInfo.setSupportedModelTypes(getSupportedModelTypes(modelProvider));
            }
            
            // 通用设置
            providerInfo.setSortOrder(getProviderSortOrder(providerInfo.getCode()));
            providerInfo.setEnabled(false); // 默认未启用，等待用户配置
            
        } catch (Exception e) {
            log.error("Failed to get provider metadata for: {}, using fallback", modelProvider.getProviderName(), e);
            // 使用降级方案
            providerInfo.setCode(modelProvider.getProviderName());
            providerInfo.setName(getProviderDisplayName(modelProvider.getProviderName()));
            providerInfo.setDescription(getProviderDescription(modelProvider.getProviderName()));
            providerInfo.setIcon(getProviderIcon(modelProvider.getProviderName()));
            providerInfo.setSortOrder(getProviderSortOrder(modelProvider.getProviderName()));
            providerInfo.setSupportedModelTypes(getSupportedModelTypes(modelProvider));
            providerInfo.setEnabled(false);
        }
        
        return providerInfo;
    }
    
    /**
     * 获取提供商显示名称
     */
    private String getProviderDisplayName(String providerCode) {
        switch (providerCode.toLowerCase()) {
            case "deepseek":
                return "DeepSeek";
            case "openai":
                return "OpenAI";
            case "anthropic":
                return "Anthropic";
            case "grok":
                return "Grok";
            default:
                return providerCode;
        }
    }
    
    /**
     * 获取提供商描述
     */
    private String getProviderDescription(String providerCode) {
        switch (providerCode.toLowerCase()) {
            case "deepseek":
                return "DeepSeek AI模型提供商";
            case "openai":
                return "OpenAI GPT系列模型提供商";
            case "anthropic":
                return "Anthropic Claude系列模型提供商";
            case "grok":
                return "Grok AI模型提供商";
            default:
                return providerCode + " 模型提供商";
        }
    }
    
    /**
     * 获取提供商图标
     */
    private String getProviderIcon(String providerCode) {
        // 返回默认图标路径或null，后续可以从插件中获取
        return null;
    }
    
    /**
     * 生成提供商图标URL
     */
    private String generateProviderIconUrl(String pluginId, String iconFileName) {
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 检查插件图标是否存在
            if (pluginIconService.iconExists(pluginId, iconFileName)) {
                return "/plugins/" + pluginId + "/icon";
            }
        } catch (Exception e) {
            log.warn("Failed to check provider icon existence for plugin {}: {}", pluginId, iconFileName, e);
        }
        
        return null;
    }
    
    /**
     * 生成默认提供商图标URL
     */
    private String generateDefaultProviderIconUrl(String pluginId, String providerCode) {
        try {
            // 获取插件信息
            Optional<PluginInfo> pluginInfoOpt = pluginRegistry.findByPluginId(pluginId);
            if (pluginInfoOpt.isPresent()) {
                PluginInfo pluginInfo = pluginInfoOpt.get();
                String iconPath = pluginInfo.getIconPath();
                
                if (iconPath != null && pluginIconService.iconExists(pluginId, iconPath)) {
                    return "/plugins/" + pluginId + "/icon";
                }
            }
        } catch (Exception e) {
            log.warn("Failed to generate default provider icon URL for plugin {}: {}", pluginId, providerCode, e);
        }
        
        return null;
    }
    
    /**
     * 获取提供商排序权重
     */
    private Integer getProviderSortOrder(String providerCode) {
        switch (providerCode.toLowerCase()) {
            case "openai":
                return 1;
            case "anthropic":
                return 2;
            case "deepseek":
                return 3;
            case "grok":
                return 4;
            default:
                return 100;
        }
    }
    
    /**
     * 获取支持的模型类型
     */
    private List<String> getSupportedModelTypes(ModelProvider modelProvider) {
        List<String> supportedTypes = new ArrayList<>();
        
        // 检查提供商支持的模型类型
        try {
            if (modelProvider.supports(ModelType.CHAT)) {
                supportedTypes.add("TEXT");
            }
            if (modelProvider.supports(ModelType.IMAGE)) {
                supportedTypes.add("IMAGE");
            }
            if (modelProvider.supports(ModelType.EMBEDDING)) {
                supportedTypes.add("EMBEDDING");
            }
        } catch (Exception e) {
            log.debug("Failed to check model type support for provider: {}", modelProvider.getProviderName(), e);
        }
        
        return supportedTypes;
    }
    
    
    
    /**
     * 注册插件的OptionsHandlers到ModelConfiguration
     * 
     * @param pluginInstance 插件实例
     * @param modelConfiguration 模型配置
     */
    private void registerPluginOptionsHandlers(ModelPlugin pluginInstance, ModelConfiguration modelConfiguration) {
        try {
            log.info("Registering OptionsHandlers for plugin: {}", pluginInstance.getId());
            
            // 使用插件感知的方式注册选项处理器
            registerPluginOptionsHandlersWithClassLoader(pluginInstance, modelConfiguration);
            
            log.info("Successfully registered OptionsHandlers for plugin: {}", pluginInstance.getId());
            
        } catch (Exception e) {
            log.error("Failed to register OptionsHandlers for plugin: {}", pluginInstance.getId(), e);
            // 不抛出异常，允许插件继续启动，只是没有OptionsHandler支持
        }
    }
    
    /**
     * 从ModelConfiguration注销插件的OptionsHandlers
     * 
     * @param pluginInstance 插件实例
     * @param modelConfiguration 模型配置
     */
    private void unregisterPluginOptionsHandlers(ModelPlugin pluginInstance, ModelConfiguration modelConfiguration) {
        try {
            log.info("Unregistering OptionsHandlers for plugin: {}", pluginInstance.getId());
            
            // 调用插件的unregisterOptionsHandlers方法
            pluginInstance.unregisterOptionsHandlers(modelConfiguration);
            
            log.info("Successfully unregistered OptionsHandlers for plugin: {}", pluginInstance.getId());
            
        } catch (Exception e) {
            log.error("Failed to unregister OptionsHandlers for plugin: {}", pluginInstance.getId(), e);
            // 不抛出异常，继续禁用流程
        }
    }
    
    /**
     * 将插件的凭证配置Schema转换为ModelConfigItem列表
     * 
     * @param credentialSchema 插件的凭证配置Schema
     * @return ModelConfigItem列表
     */
    private List<ModelConfigItem> convertCredentialSchemaToConfigItems(Map<String, Object> credentialSchema) {
        List<ModelConfigItem> configItems = new ArrayList<>();
        
        try {
            // 获取credential_form_schemas
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> formSchemas = (List<Map<String, Object>>) credentialSchema.get("credential_form_schemas");
            
            if (formSchemas != null) {
                int order = 1; // 配置项显示顺序
                
                for (Map<String, Object> schema : formSchemas) {
                    ModelConfigItem configItem = new ModelConfigItem();
                    
                    // 设置变量名
                    String variable = (String) schema.get("variable");
                    configItem.setName(variable);
                    
                    // 设置标题（优先中文，然后英文）
                    @SuppressWarnings("unchecked")
                    Map<String, String> labelMap = (Map<String, String>) schema.get("label");
                    if (labelMap != null) {
                        String title = labelMap.getOrDefault("zh_Hans", labelMap.get("en_US"));
                        configItem.setTitle(title);
                    }
                    
                    // 设置描述（使用placeholder作为描述，优先中文）
                    @SuppressWarnings("unchecked")
                    Map<String, String> placeholderMap = (Map<String, String>) schema.get("placeholder");
                    if (placeholderMap != null) {
                        String description = placeholderMap.getOrDefault("zh_Hans", placeholderMap.get("en_US"));
                        configItem.setDescription(description);
                    }
                    
                    // 设置类型（转换插件类型到标准类型）
                    String pluginType = (String) schema.get("type");
                    String standardType = convertInputType(pluginType);
                    configItem.setType(standardType);
                    
                    // 设置是否必需
                    Boolean required = (Boolean) schema.get("required");
                    configItem.setRequired(required != null ? required : false);
                    
                    // 设置显示顺序
                    configItem.setOrder(order++);
                    
                    // 设置分组（默认为基础配置）
                    configItem.setGroup("basic");
                    
                    // 根据类型设置默认值
                    if ("password".equals(standardType)) {
                        configItem.setDefaultValue("");
                    } else if ("text".equals(standardType)) {
                        configItem.setDefaultValue("");
                    }
                    
                    configItems.add(configItem);
                    
                    log.debug("Converted credential schema item: {} -> {}", variable, standardType);
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to convert credential schema to config items", e);
        }
        
        return configItems;
    }
    
    /**
     * 转换插件输入类型到标准类型
     * 
     * @param pluginType 插件类型
     * @return 标准类型
     */
    private String convertInputType(String pluginType) {
        if (pluginType == null) {
            return "text";
        }
        
        switch (pluginType.toLowerCase()) {
            case "secret-input":
                return "password";
            case "text-input":
                return "text";
            case "number-input":
                return "number";
            case "boolean-input":
            case "checkbox":
                return "boolean";
            case "select":
            case "radio":
                return "select";
            default:
                return "text";
        }
    }
    
    /**
     * 验证模型插件配置的完整性
     * 
     * @param descriptor 插件描述符
     * @throws IllegalArgumentException 配置验证失败时抛出
     */
    private void validateModelPluginConfiguration(PluginDescriptor descriptor) {
        String pluginId = descriptor.getId();
        
        // 1. 检查是否有提供商配置文件
        if (descriptor.getPlugins() == null || descriptor.getPlugins().isEmpty()) {
            throw new IllegalArgumentException(
                String.format("Model plugin [%s] must specify provider configuration files (e.g., deepseek.yaml)", pluginId)
            );
        }
        
        // 2. 验证每个提供商配置文件
        for (String configFile : descriptor.getPlugins()) {
            validateProviderConfiguration(descriptor, configFile);
        }
        
        log.info("Model plugin configuration validation passed for: {}", pluginId);
    }
    
    /**
     * 验证提供商配置文件
     * 
     * @param descriptor 插件描述符
     * @param configFile 配置文件名
     * @throws IllegalArgumentException 配置验证失败时抛出
     */
    private void validateProviderConfiguration(PluginDescriptor descriptor, String configFile) {
        String pluginId = descriptor.getId();
        
        try {
            // 从JAR中读取提供商配置文件
            InputStream configStream = descriptor.getConfigInputStream(configFile);
            if (configStream == null) {
                throw new IllegalArgumentException(
                    String.format("Provider configuration file [%s] not found in plugin [%s]", configFile, pluginId)
                );
            }
            
            // 解析提供商配置
            ProviderConfig providerConfig = parseProviderConfig(configStream);
            if (providerConfig == null) {
                throw new IllegalArgumentException(
                    String.format("Failed to parse provider configuration file [%s] in plugin [%s]", configFile, pluginId)
                );
            }
            
            // 验证提供商配置的必要字段
            validateProviderConfigFields(providerConfig, configFile, pluginId);
            
            log.debug("Provider configuration validation passed for: {} in plugin: {}", configFile, pluginId);
            
        } catch (Exception e) {
            log.error("Failed to validate provider configuration [{}] in plugin [{}]", configFile, pluginId, e);
            throw new IllegalArgumentException(
                String.format("Provider configuration validation failed for [%s] in plugin [%s]: %s", 
                    configFile, pluginId, e.getMessage()), e
            );
        }
    }
    
    /**
     * 验证提供商配置字段的完整性
     * 
     * @param providerConfig 提供商配置
     * @param configFile 配置文件名
     * @param pluginId 插件ID
     * @throws IllegalArgumentException 字段验证失败时抛出
     */
    private void validateProviderConfigFields(ProviderConfig providerConfig, String configFile, String pluginId) {
        List<String> errors = new ArrayList<>();
        
        // 1. 验证provider字段
        if (providerConfig.getProvider() == null || providerConfig.getProvider().trim().isEmpty()) {
            errors.add("'provider' field is required");
        }
        
        // 2. 验证provider_source字段
        if (providerConfig.getProviderSource() == null || providerConfig.getProviderSource().trim().isEmpty()) {
            errors.add("'provider_source' field is required (e.g., com.yonchain.ai.plugin.deepseek.DeepSeekModelProvider)");
        }
        
        // 3. 验证models配置
        if (providerConfig.getModels() == null || providerConfig.getModels().isEmpty()) {
            errors.add("'models' configuration is required");
        } else {
            validateModelsConfiguration(providerConfig.getModels(), errors);
        }
        
        // 4. 验证supported_model_types
        if (providerConfig.getSupportedModelTypes() == null || providerConfig.getSupportedModelTypes().isEmpty()) {
            errors.add("'supported_model_types' field is required (e.g., ['chat', 'embedding'])");
        }
        
        // 如果有错误，抛出异常
        if (!errors.isEmpty()) {
            String errorMessage = String.format(
                "Provider configuration validation failed for [%s] in plugin [%s]:\n- %s", 
                configFile, pluginId, String.join("\n- ", errors)
            );
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    /**
     * 验证models配置的完整性
     * 
     * @param modelsConfig models配置
     * @param errors 错误列表
     */
    @SuppressWarnings("unchecked")
    private void validateModelsConfiguration(Map<String, Object> modelsConfig, List<String> errors) {
        for (Map.Entry<String, Object> entry : modelsConfig.entrySet()) {
            String modelType = entry.getKey();
            Object modelConfig = entry.getValue();
            
            if (!(modelConfig instanceof Map)) {
                errors.add(String.format("Model type [%s] configuration must be a map", modelType));
                continue;
            }
            
            Map<String, Object> typeConfig = (Map<String, Object>) modelConfig;
            
            // 验证source字段（Spring AI模型实现类）
            String source = (String) typeConfig.get("source");
            if (source == null || source.trim().isEmpty()) {
                errors.add(String.format("Model type [%s] must specify 'source' field (e.g., org.springframework.ai.deepseek.DeepSeekChatModel)", modelType));
            }
            
            // 验证options_handler字段（选项处理器）
            String optionsHandler = (String) typeConfig.get("options_handler");
            if (optionsHandler == null || optionsHandler.trim().isEmpty()) {
                errors.add(String.format("Model type [%s] must specify 'options_handler' field (e.g., com.yonchain.ai.plugin.deepseek.DeepSeekChatOptionsHandler)", modelType));
            }
            
            // 验证predefined字段
            Object predefined = typeConfig.get("predefined");
            if (predefined == null) {
                errors.add(String.format("Model type [%s] must specify 'predefined' field with model configuration files", modelType));
            }
        }
    }
    
    /**
     * 解析提供商配置文件
     * 
     * @param inputStream 配置文件输入流
     * @return 提供商配置对象
     */
    private ProviderConfig parseProviderConfig(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> configData = yaml.load(inputStream);
            
            if (configData == null) {
                return null;
            }
            
            // 手动构建ProviderConfig对象
            ProviderConfig config = new ProviderConfig();
            config.setProvider((String) configData.get("provider"));
            config.setProviderSource((String) configData.get("provider_source"));
            config.setBackground((String) configData.get("background"));
            
            @SuppressWarnings("unchecked")
            Map<String, String> label = (Map<String, String>) configData.get("label");
            config.setLabel(label);
            
            @SuppressWarnings("unchecked")
            Map<String, String> description = (Map<String, String>) configData.get("description");
            config.setDescription(description);
            
            @SuppressWarnings("unchecked")
            Map<String, String> iconSmall = (Map<String, String>) configData.get("icon_small");
            config.setIconSmall(iconSmall);
            
            @SuppressWarnings("unchecked")
            Map<String, String> iconLarge = (Map<String, String>) configData.get("icon_large");
            config.setIconLarge(iconLarge);
            
            @SuppressWarnings("unchecked")
            List<String> supportedModelTypes = (List<String>) configData.get("supported_model_types");
            config.setSupportedModelTypes(supportedModelTypes);
            
            @SuppressWarnings("unchecked")
            List<String> configurateMethods = (List<String>) configData.get("configurate_methods");
            config.setConfigurateMethods(configurateMethods);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> models = (Map<String, Object>) configData.get("models");
            config.setModels(models);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> help = (Map<String, Object>) configData.get("help");
            config.setHelp(help);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> credentialSchema = (Map<String, Object>) configData.get("provider_credential_schema");
            config.setProviderCredentialSchema(credentialSchema);
            
            return config;
            
        } catch (Exception e) {
            log.error("Failed to parse provider configuration", e);
            return null;
        }
    }
    
    /**
     * 使用插件类加载器注册选项处理器
     * 
     * @param pluginInstance 插件实例
     * @param modelConfiguration 模型配置
     */
    private void registerPluginOptionsHandlersWithClassLoader(ModelPlugin pluginInstance, ModelConfiguration modelConfiguration) {
        String pluginId = pluginInstance.getId();
        
        try {
            // 获取插件的提供商配置
            ProviderConfig providerConfig = pluginInstance.getProviderConfig();
            if (providerConfig == null) {
                log.warn("No provider config found for plugin: {}", pluginId);
                return;
            }
            
            // 获取选项处理器映射
            Map<String, String> optionsHandlers = providerConfig.getOptionsHandlers();
            if (optionsHandlers == null || optionsHandlers.isEmpty()) {
                log.debug("No options handlers configured for plugin: {}", pluginId);
                return;
            }
            
            // 获取插件路径（用于类加载器）
            Optional<PluginInfo> pluginInfoOpt = pluginRegistry.findByPluginId(pluginId);
            if (!pluginInfoOpt.isPresent()) {
                log.error("Plugin info not found for: {}", pluginId);
                return;
            }
            
            String pluginPath = pluginInfoOpt.get().getPluginPath();
            String providerName = providerConfig.getProvider();
            
            // 使用插件类加载器创建并注册选项处理器
            for (Map.Entry<String, String> entry : optionsHandlers.entrySet()) {
                String modelType = entry.getKey();
                String handlerClassName = entry.getValue();
                
                try {
                    // 使用插件类加载器加载处理器类
                    Class<?> handlerClass = pluginClassLoader.loadClass(pluginPath, handlerClassName);
                    Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();
                    
                    // 注册到ModelConfiguration
                    modelConfiguration.registerNamespaceHandler(providerName, modelType, (com.yonchain.ai.model.options.ModelOptionsHandler<?>) handlerInstance);
                    
                    log.debug("Successfully registered options handler: {}:{} -> {}", providerName, modelType, handlerClassName);
                    
                } catch (Exception e) {
                    log.error("Failed to register options handler for {}:{} with class {}", 
                        providerName, modelType, handlerClassName, e);
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to register options handlers for plugin: {}", pluginId, e);
            throw e;
        }
    }
    
}
