package com.yonchain.ai.plugin.deepseek;

import com.yonchain.ai.tmpl.ModelConfig;
import com.yonchain.ai.tmpl.ModelMetadata;
import com.yonchain.ai.tmpl.ModelType;
import com.yonchain.ai.plugin.spi.ModelProvider;
import com.yonchain.ai.plugin.spi.ProviderMetadata;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.plugin.model.ModelPlugin;
import com.yonchain.ai.plugin.PluginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * DeepSeek模型插件实现
 * 
 * @author yonchain
 */
public class DeepSeekPlugin implements ModelPlugin {
    
    private static final Logger log = LoggerFactory.getLogger(DeepSeekPlugin.class);
    
    private static final String PLUGIN_ID = "com.yonchain.ai.plugin.deepseek";
    private static final String PLUGIN_NAME = "DeepSeek";
    private static final String PLUGIN_VERSION = "0.0.2";
    private static final String PROVIDER_NAME = "deepseek";
    
    private DeepSeekModelProvider modelProvider;
    private boolean initialized = false;
    
    @Override
    public String getId() {
        return PLUGIN_ID;
    }
    
    @Override
    public String getName() {
        return PLUGIN_NAME;
    }
    
    @Override
    public String getVersion() {
        return PLUGIN_VERSION;
    }
    
    @Override
    public String getDescription() {
        return "DeepSeek AI模型插件，提供deepseek-chat、deepseek-reasoner、deepseek-coder等模型";
    }
    
    @Override
    public void initialize(PluginContext context) {
        if (initialized) {
            log.warn("Plugin already initialized: {}", PLUGIN_ID);
            return;
        }
        
        try {
            log.info("Initializing DeepSeek plugin: {}", PLUGIN_ID);
            
            // 创建模型提供商
            this.modelProvider = new DeepSeekModelProvider();
            
            // 标记为已初始化
            this.initialized = true;
            
            log.info("DeepSeek plugin initialized successfully: {}", PLUGIN_ID);
            
        } catch (Exception e) {
            log.error("Failed to initialize DeepSeek plugin: {}", PLUGIN_ID, e);
            throw new RuntimeException("Failed to initialize DeepSeek plugin", e);
        }
    }
    
    @Override
    public void dispose() {
        if (!initialized) {
            return;
        }
        
        try {
            log.info("Disposing DeepSeek plugin: {}", PLUGIN_ID);
            
            // 清理资源
            if (modelProvider != null) {
                // TODO: 如果ModelProvider有清理方法，在这里调用
                modelProvider = null;
            }
            
            this.initialized = false;
            
            log.info("DeepSeek plugin disposed successfully: {}", PLUGIN_ID);
            
        } catch (Exception e) {
            log.error("Failed to dispose DeepSeek plugin: {}", PLUGIN_ID, e);
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
        return Arrays.asList(
            createModelMetadata("deepseek-chat", "DeepSeek Chat", "强大的对话模型"),
            createModelMetadata("deepseek-reasoner", "DeepSeek Reasoner", "深度推理模型"), 
            createModelMetadata("deepseek-coder", "DeepSeek Coder", "代码生成模型")
        );
    }
    
    @Override
    public void onEnable() {
        log.info("DeepSeek plugin enabled: {}", PLUGIN_ID);
        
        // 插件启用时的特殊逻辑
        try {
            // 可以在这里执行一些启用时的初始化工作
            log.debug("DeepSeek plugin enable completed");
        } catch (Exception e) {
            log.error("Error during plugin enable: {}", PLUGIN_ID, e);
            throw new RuntimeException("Failed to enable DeepSeek plugin", e);
        }
    }
    
    @Override
    public void onDisable() {
        log.info("DeepSeek plugin disabled: {}", PLUGIN_ID);
        
        // 插件禁用时的清理逻辑
        try {
            // 可以在这里执行一些禁用时的清理工作
            log.debug("DeepSeek plugin disable completed");
        } catch (Exception e) {
            log.error("Error during plugin disable: {}", PLUGIN_ID, e);
        }
    }
    
    @Override
    public void registerModels(ModelRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Model registry cannot be null");
        }
        
        try {
          /*  List<ModelMetadata> models = getModels();
            for (ModelMetadata model : models) {
                registry.registerModel(model);
                log.debug("Registered model: {} from DeepSeek plugin", model.getModelId());
            }*/
            
            log.info("All DeepSeek models registered successfully");
            
        } catch (Exception e) {
            log.error("Failed to register DeepSeek models", e);
            throw new RuntimeException("Failed to register DeepSeek models", e);
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
              //  registry.unregisterModel(model.getModelId());
                log.debug("Unregistered model: {} from DeepSeek plugin", model.getModelId());
            }
            
            log.info("All DeepSeek models unregistered successfully");
            
        } catch (Exception e) {
            log.error("Failed to unregister DeepSeek models", e);
            throw new RuntimeException("Failed to unregister DeepSeek models", e);
        }
    }
    
    @Override
    public ProviderMetadata getProviderMetadata() {
        try {
            // 从YAML文件加载元数据
            Map<String, Object> yamlData = loadYamlConfig();
            if (yamlData == null) {
                log.warn("Failed to load YAML config, using fallback metadata");
                return createFallbackMetadata();
            }
            
            ProviderMetadata metadata = new ProviderMetadata();
            
            // 从YAML设置基本信息
            metadata.setProvider((String) yamlData.get("provider"));
            metadata.setBackground((String) yamlData.get("background"));
            
            // 设置多语言标签
            @SuppressWarnings("unchecked")
            Map<String, String> labels = (Map<String, String>) yamlData.get("label");
            if (labels != null) {
                metadata.setLabel(labels);
            }
            
            // 设置多语言描述
            @SuppressWarnings("unchecked")
            Map<String, String> descriptions = (Map<String, String>) yamlData.get("description");
            if (descriptions != null) {
                metadata.setDescription(descriptions);
            }
            
            // 设置小图标
            @SuppressWarnings("unchecked")
            Map<String, String> iconSmall = (Map<String, String>) yamlData.get("icon_small");
            if (iconSmall != null) {
                metadata.setIconSmall(iconSmall);
            }
            
            // 设置大图标
            @SuppressWarnings("unchecked")
            Map<String, String> iconLarge = (Map<String, String>) yamlData.get("icon_large");
            if (iconLarge != null) {
                metadata.setIconLarge(iconLarge);
            }
            
            // 设置支持的模型类型
            @SuppressWarnings("unchecked")
            List<String> supportedModelTypes = (List<String>) yamlData.get("supported_model_types");
            if (supportedModelTypes != null) {
                metadata.setSupportedModelTypes(supportedModelTypes);
            }
            
            // 设置配置方法
            @SuppressWarnings("unchecked")
            List<String> configurateMethods = (List<String>) yamlData.get("configurate_methods");
            if (configurateMethods != null) {
                metadata.setConfigurateMethods(configurateMethods);
            }
            
            // 设置帮助信息
            @SuppressWarnings("unchecked")
            Map<String, Object> help = (Map<String, Object>) yamlData.get("help");
            if (help != null) {
                metadata.setHelp(help);
            }
            
            // 设置提供商凭证配置Schema
            @SuppressWarnings("unchecked")
            Map<String, Object> credentialSchema = (Map<String, Object>) yamlData.get("provider_credential_schema");
            if (credentialSchema != null) {
                metadata.setProviderCredentialSchema(credentialSchema);
            }
            
            return metadata;
            
        } catch (Exception e) {
            log.error("Failed to load provider metadata from YAML for DeepSeek plugin", e);
            return createFallbackMetadata();
        }
    }
    
    /**
     * 从YAML文件加载配置
     * 
     * @return YAML配置数据
     */
    private Map<String, Object> loadYamlConfig() {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("deepseek.yaml");
            if (inputStream == null) {
                log.error("deepseek.yaml not found in classpath");
                return null;
            }
            
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(inputStream);
            
            log.debug("Successfully loaded deepseek.yaml configuration");
            return data;
            
        } catch (Exception e) {
            log.error("Failed to load deepseek.yaml", e);
            return null;
        }
    }
    
    /**
     * 创建fallback元数据（当YAML加载失败时使用）
     * 
     * @return fallback元数据
     */
    private ProviderMetadata createFallbackMetadata() {
        ProviderMetadata metadata = new ProviderMetadata();
        
        // 设置基本信息
        metadata.setProvider("deepseek");
        metadata.setBackground("#c0cdff");
        
        // 设置多语言标签
        Map<String, String> labels = new HashMap<>();
        labels.put("en_US", "deepseek");
        labels.put("zh_Hans", "深度求索");
        metadata.setLabel(labels);
        
        // 设置多语言描述
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("en_US", "Models provided by deepseek, such as deepseek-reasoner、deepseek-chat、deepseek-coder.");
        descriptions.put("zh_Hans", "深度求索提供的模型，例如 deepseek-reasoner、deepseek-chat、deepseek-coder 。");
        metadata.setDescription(descriptions);
        
        // 设置支持的模型类型
        List<String> modelTypes = Arrays.asList("llm");
        metadata.setSupportedModelTypes(modelTypes);
        
        // 设置配置方法
        List<String> configurateMethods = Arrays.asList("predefined-model");
        metadata.setConfigurateMethods(configurateMethods);
        
        return metadata;
    }
    
    
    /**
     * 创建模型元数据
     * 
     * @param modelName 模型名称
     * @param displayName 显示名称
     * @param description 描述
     * @return 模型元数据
     */
    private ModelMetadata createModelMetadata(String modelName, String displayName, String description) {
        ModelMetadata metadata = new ModelMetadata();
        metadata.setName(modelName);
        metadata.setProvider(PROVIDER_NAME);
        metadata.setType(ModelType.TEXT);
        metadata.setDescription(description);
        metadata.setDisplayName(displayName);
        metadata.setVersion("1.0");
        
        // 创建基础ModelConfig
        ModelConfig config = createBaseModelConfig(modelName);
        metadata.setConfig(config);
        
        return metadata;
    }
    
    /**
     * 创建基础模型配置
     * 
     * @param modelName 模型名称
     * @return 基础模型配置
     */
    private ModelConfig createBaseModelConfig(String modelName) {
        ModelConfig config = new ModelConfig();
        config.setApiKey("sk-3ef709a6aa404b00af299c288264a48f");
        config.setName(modelName);
        config.setProvider(PROVIDER_NAME);
        config.setType(ModelType.TEXT);
        config.setEnabled(true);
        
        // 设置基础参数
        config.setTimeout(30000); // 30秒超时
        config.setRetryCount(3);
        config.setTemperature(0.7); // 默认温度
        
        // 根据模型设置最大Token数
        switch (modelName) {
            case "deepseek-chat":
                config.setMaxTokens(32768);
                break;
            case "deepseek-coder":
                config.setMaxTokens(16384);
                break;
            case "deepseek-reasoner":
                config.setMaxTokens(8192);
                break;
            default:
                config.setMaxTokens(4096);
                break;
        }
        
        // 设置模型特定属性
        config.setProperty("streaming", true);
        config.setProperty("function_calling", true);
        
        return config;
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
     * 获取提供商名称
     * 
     * @return 提供商名称
     */
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public String toString() {
        return "DeepSeekPlugin{" +
                "id='" + PLUGIN_ID + '\'' +
                ", name='" + PLUGIN_NAME + '\'' +
                ", version='" + PLUGIN_VERSION + '\'' +
                ", initialized=" + initialized +
                '}';
    }
}

