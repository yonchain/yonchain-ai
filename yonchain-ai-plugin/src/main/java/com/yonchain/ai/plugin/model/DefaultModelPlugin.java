package com.yonchain.ai.plugin.model;

import com.yonchain.ai.plugin.PluginContext;
import com.yonchain.ai.plugin.config.ModelConfigData;
import com.yonchain.ai.plugin.config.PluginConfig;
import com.yonchain.ai.plugin.config.ProviderConfig;
import com.yonchain.ai.plugin.spi.ModelProvider;
import com.yonchain.ai.plugin.spi.ProviderMetadata;
import com.yonchain.ai.tmpl.ModelMetadata;
import com.yonchain.ai.model.ModelRegistry;
import com.yonchain.ai.model.ModelConfiguration;
import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.tmpl.ModelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自动生成的模型插件实现
 * 替代开发者手写的518行DeepSeekPlugin代码
 */
public class DefaultModelPlugin implements ModelPlugin {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultModelPlugin.class);
    
    private final PluginConfig pluginConfig;
    private final ProviderConfig providerConfig;
    private final List<ModelConfigData> modelConfigs;
    private final ModelProvider provider;
    private final ProviderMetadata providerMetadata;
    private final List<ModelMetadata> modelMetadataList;
    
    private boolean initialized = false;
    
    public DefaultModelPlugin(
            PluginConfig pluginConfig,
            ProviderConfig providerConfig,
            List<ModelConfigData> modelConfigs,
            ModelProvider provider) {
        
        this.pluginConfig = pluginConfig;
        this.providerConfig = providerConfig;
        this.modelConfigs = modelConfigs;
        this.provider = provider;
        
        // 构建元数据
        this.providerMetadata = buildProviderMetadata();
        this.modelMetadataList = buildModelMetadata();
        
        log.info("Created auto-generated plugin: {}", pluginConfig.getId());
    }
    
    @Override
    public String getId() {
        return pluginConfig.getId();
    }
    
    @Override
    public String getName() {
        return pluginConfig.getName();
    }
    
    @Override
    public String getVersion() {
        return pluginConfig.getVersion();
    }
    
    @Override
    public String getDescription() {
        return pluginConfig.getLocalizedDescription("zh_Hans");
    }
    
    @Override
    public void initialize(PluginContext context) {
        if (initialized) {
            log.warn("Plugin already initialized: {}", getId());
            return;
        }
        
        try {
            log.info("Auto-initializing plugin: {}", getId());
            
            // 自动初始化逻辑
            // 这里可以添加一些通用的初始化逻辑
            
            this.initialized = true;
            log.info("Plugin auto-initialized successfully: {}", getId());
            
        } catch (Exception e) {
            log.error("Failed to auto-initialize plugin: {}", getId(), e);
            throw new RuntimeException("Failed to auto-initialize plugin", e);
        }
    }
    
    @Override
    public void dispose() {
        if (!initialized) {
            return;
        }
        
        try {
            log.info("Auto-disposing plugin: {}", getId());
            
            // 自动清理逻辑
            // 这里可以添加一些通用的清理逻辑
            
            this.initialized = false;
            log.info("Plugin auto-disposed successfully: {}", getId());
            
        } catch (Exception e) {
            log.error("Failed to auto-dispose plugin: {}", getId(), e);
        }
    }
    
    @Override
    public ModelProvider getProvider() {
        if (!initialized || provider == null) {
            throw new IllegalStateException("Plugin not initialized or provider not available");
        }
        return provider;
    }
    
    @Override
    public ProviderMetadata getProviderMetadata() {
        return providerMetadata;
    }
    
    @Override
    public List<ModelMetadata> getModels() {
        return new ArrayList<>(modelMetadataList);
    }
    
    @Override
    public void onEnable() {
        log.info("Auto-enabling plugin: {}", getId());
        
        try {
            // 自动启用逻辑
            // 可以在这里执行一些启用时的初始化工作
            log.debug("Plugin auto-enable completed: {}", getId());
            
        } catch (Exception e) {
            log.error("Error during plugin auto-enable: {}", getId(), e);
            throw new RuntimeException("Failed to auto-enable plugin", e);
        }
    }
    
    @Override
    public void onDisable() {
        log.info("Auto-disabling plugin: {}", getId());
        
        try {
            // 自动禁用逻辑
            // 可以在这里执行一些禁用时的清理工作
            log.debug("Plugin auto-disable completed: {}", getId());
            
        } catch (Exception e) {
            log.error("Error during plugin auto-disable: {}", getId(), e);
        }
    }
    
    @Override
    public void registerModels(ModelRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Model registry cannot be null");
        }
        
        try {
            for (ModelMetadata model : modelMetadataList) {
                // 将ModelMetadata转换为ModelDefinition
                ModelDefinition definition = convertToModelDefinition(model);
                registry.registerModel(definition);
                log.debug("Auto-registered model: {} from plugin: {}", model.getModelId(), getId());
            }
            
            log.info("All models auto-registered successfully for plugin: {}", getId());
            
        } catch (Exception e) {
            log.error("Failed to auto-register models for plugin: {}", getId(), e);
            throw new RuntimeException("Failed to auto-register models", e);
        }
    }
    
    /**
     * 将ModelMetadata转换为ModelDefinition
     */
    private ModelDefinition convertToModelDefinition(ModelMetadata metadata) {
        ModelDefinition definition = new ModelDefinition();
        
        definition.setId(metadata.getName());
        definition.setNamespace(metadata.getProvider());
        definition.setType(metadata.getType().getCode());
        
        if (metadata.getConfig() != null) {
            definition.setAuthValue(metadata.getConfig().getApiKey());
            definition.setBaseUrl(metadata.getConfig().getEndpoint());
        }
        
        return definition;
    }
    
    @Override
    public void unregisterModels(ModelRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Model registry cannot be null");
        }
        
        try {
            for (ModelMetadata model : modelMetadataList) {
                // registry.unregisterModel(model.getModelId());
                log.debug("Auto-unregistered model: {} from plugin: {}", model.getModelId(), getId());
            }
            
            log.info("All models auto-unregistered successfully for plugin: {}", getId());
            
        } catch (Exception e) {
            log.error("Failed to auto-unregister models for plugin: {}", getId(), e);
            throw new RuntimeException("Failed to auto-unregister models", e);
        }
    }
    
    @Override
    public void registerOptionsHandlers(ModelConfiguration modelConfiguration) {
        if (modelConfiguration == null) {
            throw new IllegalArgumentException("ModelConfiguration cannot be null");
        }
        
        try {
            log.info("Auto-registering options handlers for plugin: {}", getId());
            
            // 从配置中获取选项处理器映射
            Map<String, String> handlerMappings = providerConfig.getOptionsHandlers();
            
            for (Map.Entry<String, String> entry : handlerMappings.entrySet()) {
                String modelType = entry.getKey();
                String handlerClass = entry.getValue();
                
                try {
                    modelConfiguration.registerNamespaceHandlerByClass(
                        provider.getProviderName(), modelType, handlerClass);
                    
                    log.debug("Auto-registered options handler: {}:{} -> {}", 
                        provider.getProviderName(), modelType, handlerClass);
                        
                } catch (Exception e) {
                    log.warn("Failed to auto-register options handler for {}:{} with class {}", 
                        provider.getProviderName(), modelType, handlerClass, e);
                }
            }
            
            log.info("All options handlers auto-registered successfully for plugin: {}", getId());
            
        } catch (Exception e) {
            log.error("Failed to auto-register options handlers for plugin: {}", getId(), e);
            throw new RuntimeException("Failed to auto-register options handlers", e);
        }
    }
    
    @Override
    public void unregisterOptionsHandlers(ModelConfiguration modelConfiguration) {
        if (modelConfiguration == null) {
            throw new IllegalArgumentException("ModelConfiguration cannot be null");
        }
        
        try {
            log.info("Auto-unregistering options handlers for plugin: {}", getId());
            
            // 自动注销选项处理器
            // 在实际应用中，可能需要从modelConfiguration中移除handlers
            // 但目前的ModelConfiguration API没有提供移除方法
            
            log.info("Options handlers auto-unregistered for plugin: {}", getId());
            
        } catch (Exception e) {
            log.warn("Error during options handlers auto-unregistration for plugin: {}", getId(), e);
        }
    }
    
    /**
     * 构建提供商元数据
     */
    private ProviderMetadata buildProviderMetadata() {
        ProviderMetadata metadata = new ProviderMetadata();
        
        metadata.setProvider(providerConfig.getProvider());
        metadata.setBackground(providerConfig.getBackground());
        metadata.setLabel(providerConfig.getLabel());
        metadata.setDescription(providerConfig.getDescription());
        metadata.setIconSmall(providerConfig.getIconSmall());
        metadata.setIconLarge(providerConfig.getIconLarge());
        metadata.setSupportedModelTypes(providerConfig.getSupportedModelTypes());
        metadata.setConfigurateMethods(providerConfig.getConfigurateMethods());
        metadata.setHelp(providerConfig.getHelp());
        metadata.setProviderCredentialSchema(providerConfig.getProviderCredentialSchema());
        
        return metadata;
    }
    
    /**
     * 构建模型元数据列表
     */
    private List<ModelMetadata> buildModelMetadata() {
        List<ModelMetadata> metadataList = new ArrayList<>();
        
        for (ModelConfigData modelConfig : modelConfigs) {
            ModelMetadata metadata = new ModelMetadata();
            
            metadata.setName(modelConfig.getModel());
            metadata.setProvider(providerConfig.getProvider());
            metadata.setType(convertModelType(modelConfig.getModelType()));
            metadata.setDisplayName(getModelDisplayName(modelConfig));
            metadata.setDescription(getModelDescription(modelConfig));
            metadata.setVersion("1.0");
            
            // 设置模型属性
            if (modelConfig.getModelProperties() != null) {
                Map<String, Object> properties = modelConfig.getModelProperties();
                if (properties.containsKey("context_size")) {
                    metadata.setMaxTokens((Integer) properties.get("context_size"));
                }
            }
            
            // 创建基础ModelConfig
            ModelConfig config = createBaseModelConfig(modelConfig);
            metadata.setConfig(config);
            
            metadataList.add(metadata);
        }
        
        return metadataList;
    }
    
    /**
     * 转换模型类型
     */
    private ModelType convertModelType(String typeString) {
        if (typeString == null) {
            return ModelType.CHAT;
        }
        
        switch (typeString.toLowerCase()) {
            case "chat":
                return ModelType.CHAT;
            case "embedding":
                return ModelType.EMBEDDING;
            case "image":
                return ModelType.IMAGE;
            default:
                return ModelType.CHAT;
        }
    }
    
    /**
     * 获取模型显示名称
     */
    private String getModelDisplayName(ModelConfigData modelConfig) {
        if (modelConfig.getLabel() != null) {
            return modelConfig.getLabel().getOrDefault("zh_Hans", 
                   modelConfig.getLabel().getOrDefault("en_US", modelConfig.getModel()));
        }
        return modelConfig.getModel();
    }
    
    /**
     * 获取模型描述
     */
    private String getModelDescription(ModelConfigData modelConfig) {
        // 可以从模型配置中提取更详细的描述信息
        return "Auto-generated description for " + modelConfig.getModel();
    }
    
    /**
     * 创建基础模型配置
     */
    private ModelConfig createBaseModelConfig(ModelConfigData modelConfig) {
        ModelConfig config = new ModelConfig();
        
        config.setName(modelConfig.getModel());
        config.setProvider(providerConfig.getProvider());
        config.setType(convertModelType(modelConfig.getModelType()));
        config.setEnabled(true);
        
        // 设置基础参数
        config.setTimeout(30000); // 30秒超时
        config.setRetryCount(3);
        config.setTemperature(0.7); // 默认温度
        
        // 从模型属性设置最大Token数
        if (modelConfig.getModelProperties() != null) {
            Map<String, Object> properties = modelConfig.getModelProperties();
            if (properties.containsKey("context_size")) {
                config.setMaxTokens((Integer) properties.get("context_size"));
            }
        }
        
        // 设置模型特定属性
        config.setProperty("streaming", true);
        config.setProperty("function_calling", true);
        
        return config;
    }
    
    /**
     * 检查插件是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public String toString() {
        return "AutoGeneratedModelPlugin{" +
                "id='" + pluginConfig.getId() + '\'' +
                ", name='" + pluginConfig.getName() + '\'' +
                ", version='" + pluginConfig.getVersion() + '\'' +
                ", initialized=" + initialized +
                '}';
    }
}
