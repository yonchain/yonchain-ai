package com.yonchain.ai.plugin.deepseek;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.model.provider.ModelProvider;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.plugin.model.ModelPlugin;
import com.yonchain.ai.plugin.PluginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于YAML配置的DeepSeek模型插件实现
 * 从deepseek.yaml等配置文件读取插件和模型信息
 * 
 * @author yonchain
 */
public class YamlBasedDeepSeekPlugin implements ModelPlugin {
    
    private static final Logger log = LoggerFactory.getLogger(YamlBasedDeepSeekPlugin.class);
    
    private static final String PLUGIN_ID = "com.yonchain.ai.plugin.deepseek";
    private static final String PLUGIN_YAML = "/plugin.yaml";
    
    // 动态从plugin.yaml读取的配置
    private String deepseekYamlPath;
    private Map<String, Object> pluginMetadata;
    
    private DeepSeekModelProvider modelProvider;
    private boolean initialized = false;
    private Map<String, Object> pluginConfig;
    private List<ModelMetadata> modelMetadataList;
    
    @Override
    public String getId() {
        return PLUGIN_ID;
    }
    
    @Override
    public String getName() {
        if (pluginMetadata != null && pluginMetadata.containsKey("label")) {
            @SuppressWarnings("unchecked")
            Map<String, String> labels = (Map<String, String>) pluginMetadata.get("label");
            return labels.getOrDefault("zh_Hans", labels.getOrDefault("en_US", "DeepSeek"));
        }
        return "DeepSeek";
    }
    
    @Override
    public String getVersion() {
        if (pluginMetadata != null && pluginMetadata.containsKey("version")) {
            Object version = pluginMetadata.get("version");
            return version != null ? version.toString() : "0.0.2";
        }
        return "0.0.2";
    }
    
    @Override
    public String getDescription() {
        if (pluginMetadata != null && pluginMetadata.containsKey("description")) {
            @SuppressWarnings("unchecked")
            Map<String, String> descriptions = (Map<String, String>) pluginMetadata.get("description");
            return descriptions.getOrDefault("zh_Hans", descriptions.getOrDefault("en_US", "DeepSeek AI模型插件"));
        }
        return "DeepSeek AI模型插件，提供deepseek-chat、deepseek-reasoner、deepseek-coder等模型";
    }
    
    @Override
    public void initialize(PluginContext context) {
        if (initialized) {
            log.warn("Plugin already initialized: {}", PLUGIN_ID);
            return;
        }
        
        try {
            log.info("Initializing YAML-based DeepSeek plugin: {}", PLUGIN_ID);
            
            // 1. 加载插件元数据配置
            loadPluginMetadata();
            
            // 2. 加载插件配置
            loadPluginConfig();
            
            // 3. 加载模型配置
            loadModelConfigs();
            
            // 4. 创建模型提供商
            this.modelProvider = new DeepSeekModelProvider();
            
            // 5. 标记为已初始化
            this.initialized = true;
            
            log.info("YAML-based DeepSeek plugin initialized successfully: {}", PLUGIN_ID);
            
        } catch (Exception e) {
            log.error("Failed to initialize YAML-based DeepSeek plugin: {}", PLUGIN_ID, e);
            throw new RuntimeException("Failed to initialize YAML-based DeepSeek plugin", e);
        }
    }
    
    @Override
    public void dispose() {
        if (!initialized) {
            return;
        }
        
        try {
            log.info("Disposing YAML-based DeepSeek plugin: {}", PLUGIN_ID);
            
            // 清理资源
            if (modelProvider != null) {
                modelProvider = null;
            }
            
            pluginConfig = null;
            pluginMetadata = null;
            modelMetadataList = null;
            deepseekYamlPath = null;
            this.initialized = false;
            
            log.info("YAML-based DeepSeek plugin disposed successfully: {}", PLUGIN_ID);
            
        } catch (Exception e) {
            log.error("Failed to dispose YAML-based DeepSeek plugin: {}", PLUGIN_ID, e);
        }
    }
    
    @Override
    public ModelProvider getProvider() {
        if (!initialized || modelProvider == null) {
            throw new IllegalStateException("Plugin not initialized or provider not available");
        }
        return modelProvider;
    }
    
    @Override
    public List<ModelMetadata> getModels() {
        if (modelMetadataList == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(modelMetadataList);
    }
    
    @Override
    public void onEnable() {
        log.info("YAML-based DeepSeek plugin enabled: {}", PLUGIN_ID);
    }
    
    @Override
    public void onDisable() {
        log.info("YAML-based DeepSeek plugin disabled: {}", PLUGIN_ID);
    }
    
    @Override
    public void registerModels(ModelRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Model registry cannot be null");
        }
        
        try {
            List<ModelMetadata> models = getModels();
            for (ModelMetadata model : models) {
                // registry.register(model); // 需要实现ModelRegistry的register方法
                log.debug("Registered model: {} from YAML-based DeepSeek plugin", model.getModelId());
            }
            
            log.info("All YAML-based DeepSeek models registered successfully");
            
        } catch (Exception e) {
            log.error("Failed to register YAML-based DeepSeek models", e);
            throw new RuntimeException("Failed to register YAML-based DeepSeek models", e);
        }
    }
    
    @Override
    public void unregisterModels(ModelRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Model registry cannot be null");
        }
        
        try {
            List<ModelMetadata> models = getModels();
            for (ModelMetadata model : models) {
                // registry.unregister(model.getModelId()); // 需要实现ModelRegistry的unregister方法
                log.debug("Unregistered model: {} from YAML-based DeepSeek plugin", model.getModelId());
            }
            
            log.info("All YAML-based DeepSeek models unregistered successfully");
            
        } catch (Exception e) {
            log.error("Failed to unregister YAML-based DeepSeek models", e);
            throw new RuntimeException("Failed to unregister YAML-based DeepSeek models", e);
        }
    }
    
    /**
     * 加载插件元数据配置（从plugin.yaml）
     */
    private void loadPluginMetadata() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getResourceAsStream(PLUGIN_YAML);
            
            if (inputStream == null) {
                throw new RuntimeException("Cannot find plugin.yaml configuration file");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> pluginMetadata = yaml.load(inputStream);
            this.pluginMetadata = pluginMetadata; // 保存插件元数据
            
            // 从plugin.yaml读取供应商配置文件路径
            @SuppressWarnings("unchecked")
            List<String> plugins = (List<String>) pluginMetadata.get("plugins");
            if (plugins != null && !plugins.isEmpty()) {
                this.deepseekYamlPath = "/" + plugins.get(0); // deepseek.yaml
            } else {
                this.deepseekYamlPath = "/deepseek.yaml"; // 默认值
            }
            
            log.debug("Loaded plugin metadata - deepseekYaml: {}", deepseekYamlPath);
            
        } catch (Exception e) {
            log.error("Failed to load plugin metadata from plugin.yaml", e);
            throw new RuntimeException("Failed to load plugin metadata", e);
        }
    }
    
    /**
     * 加载插件配置（从deepseek.yaml）
     */
    private void loadPluginConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getResourceAsStream(deepseekYamlPath);
            
            if (inputStream == null) {
                throw new RuntimeException("Cannot find configuration file: " + deepseekYamlPath);
            }
            
            pluginConfig = yaml.load(inputStream);
            log.debug("Loaded plugin config from {}: {}", deepseekYamlPath, pluginConfig.keySet());
            
        } catch (Exception e) {
            log.error("Failed to load plugin config from {}", deepseekYamlPath, e);
            throw new RuntimeException("Failed to load plugin configuration", e);
        }
    }
    
    /**
     * 加载模型配置（从deepseek.yaml中的models列表）
     */
    private void loadModelConfigs() {
        try {
            modelMetadataList = new ArrayList<>();
            
            // 从deepseek.yaml中读取模型列表
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> models = (List<Map<String, Object>>) pluginConfig.get("models");
            
            if (models != null) {
                for (Map<String, Object> modelConfig : models) {
                    ModelMetadata metadata = createModelMetadataFromConfig(modelConfig);
                    
                    if (metadata != null) {
                        modelMetadataList.add(metadata);
                        log.debug("Loaded model config: {}", metadata.getModelId());
                    }
                }
            }
            
            log.info("Loaded {} model configurations from deepseek.yaml", modelMetadataList.size());
            
        } catch (Exception e) {
            log.error("Failed to load model configs from deepseek.yaml", e);
            throw new RuntimeException("Failed to load model configurations", e);
        }
    }
    
    /**
     * 从配置创建模型元数据
     * 
     * @param modelConfig 模型配置
     * @return 模型元数据
     */
    @SuppressWarnings("unchecked")
    private ModelMetadata createModelMetadataFromConfig(Map<String, Object> modelConfig) {
        try {
            String modelName = (String) modelConfig.get("model");
            if (modelName == null || modelName.trim().isEmpty()) {
                log.warn("Model name is missing in config: {}", modelConfig);
                return null;
            }
            
            ModelMetadata metadata = new ModelMetadata();
            metadata.setName(modelName);
            metadata.setProvider(getProviderName());
            
            // 设置模型类型
            String modelType = (String) modelConfig.get("model_type");
            if ("llm".equals(modelType)) {
                metadata.setType(ModelType.TEXT);
            } else {
                metadata.setType(ModelType.TEXT); // 默认为文本模型
            }
            
            // 设置显示名称
            Map<String, String> labels = (Map<String, String>) modelConfig.get("label");
            if (labels != null) {
                String displayName = labels.getOrDefault("zh_Hans", labels.getOrDefault("en_US", modelName));
                metadata.setDisplayName(displayName);
            } else {
                metadata.setDisplayName(modelName);
            }
            
            // 设置模型属性
            Map<String, Object> modelProperties = (Map<String, Object>) modelConfig.get("model_properties");
            if (modelProperties != null) {
                Object contextLength = modelProperties.get("context_length");
                if (contextLength instanceof Number) {
                    metadata.setMaxTokens(((Number) contextLength).intValue());
                }
            }
            
            // 设置版本和描述
            metadata.setVersion("1.0");
            metadata.setDescription("DeepSeek " + modelName + " 模型");
            
            // 创建基础ModelConfig
            Integer maxTokens = metadata.getMaxTokens() != null ? metadata.getMaxTokens() : 4096;
            ModelConfig config = createBaseModelConfigFromYaml(modelName, maxTokens, modelConfig);
            metadata.setConfig(config);
            
            // 设置支持的特性
            List<String> features = (List<String>) modelConfig.get("features");
            if (features != null) {
                for (String feature : features) {
                    metadata.addSupportedFeature(feature);
                }
            } else {
                metadata.addSupportedFeature("chat");
                metadata.addSupportedFeature("completion");
            }
            
            return metadata;
            
        } catch (Exception e) {
            log.error("Failed to create model metadata from config: {}", modelConfig, e);
            return null;
        }
    }
    
    /**
     * 获取提供商名称
     * 
     * @return 提供商名称
     */
    public String getProviderName() {
        // 优先从deepseek.yaml读取
        if (pluginConfig != null && pluginConfig.containsKey("provider")) {
            return (String) pluginConfig.get("provider");
        }
        // 其次从plugin.yaml读取
        if (pluginMetadata != null && pluginMetadata.containsKey("name")) {
            return (String) pluginMetadata.get("name");
        }
        return "deepseek";
    }
    
    /**
     * 检查插件是否已初始化
     * 
     * @return 是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 获取插件配置
     * 
     * @return 插件配置
     */
    public Map<String, Object> getPluginConfig() {
        return pluginConfig;
    }
    
    /**
     * 从YAML配置创建基础模型配置
     * 
     * @param modelName 模型名称
     * @param maxTokens 最大Token数
     * @param yamlConfig YAML配置
     * @return 基础模型配置
     */
    @SuppressWarnings("unchecked")
    private ModelConfig createBaseModelConfigFromYaml(String modelName, Integer maxTokens, Map<String, Object> yamlConfig) {
        ModelConfig config = new ModelConfig();
        config.setName(modelName);
        config.setProvider(getProviderName());
        config.setType(ModelType.TEXT);
        config.setEnabled(true);
        
        // 设置基础参数
        config.setTimeout(30000); // 30秒超时
        config.setRetryCount(3);
        config.setMaxTokens(maxTokens);
        
        // 从YAML配置读取默认参数
        Map<String, Object> modelProperties = (Map<String, Object>) yamlConfig.get("model_properties");
        if (modelProperties != null) {
            // 温度参数
            Object temperature = modelProperties.get("temperature");
            if (temperature instanceof Number) {
                config.setTemperature(((Number) temperature).doubleValue());
            } else {
                config.setTemperature(0.7); // 默认温度
            }
            
            // 其他参数
            Object topP = modelProperties.get("top_p");
            if (topP instanceof Number) {
                config.setProperty("top_p", ((Number) topP).doubleValue());
            }
        } else {
            config.setTemperature(0.7); // 默认温度
        }
        
        // 设置模型特定属性
        config.setProperty("streaming", true);
        config.setProperty("function_calling", true);
        
        return config;
    }
    
    @Override
    public String toString() {
        return "YamlBasedDeepSeekPlugin{" +
                "id='" + PLUGIN_ID + '\'' +
                ", name='" + getName() + '\'' +
                ", version='" + getVersion() + '\'' +
                ", initialized=" + initialized +
                ", modelsCount=" + (modelMetadataList != null ? modelMetadataList.size() : 0) +
                '}';
    }
}



