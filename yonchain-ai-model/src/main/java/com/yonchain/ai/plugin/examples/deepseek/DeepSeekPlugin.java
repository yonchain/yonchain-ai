package com.yonchain.ai.plugin.examples.deepseek;

import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.ModelType;
import com.yonchain.ai.model.provider.ModelProvider;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.plugin.ModelPlugin;
import com.yonchain.ai.plugin.PluginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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
            List<ModelMetadata> models = getModels();
            for (ModelMetadata model : models) {
                registry.registerModel(model);
                log.debug("Registered model: {} from DeepSeek plugin", model.getModelId());
            }
            
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
                registry.unregisterModel(model.getModelId());
                log.debug("Unregistered model: {} from DeepSeek plugin", model.getModelId());
            }
            
            log.info("All DeepSeek models unregistered successfully");
            
        } catch (Exception e) {
            log.error("Failed to unregister DeepSeek models", e);
            throw new RuntimeException("Failed to unregister DeepSeek models", e);
        }
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
      //  metadata.see(true);
        metadata.setVersion("1.0");
        
      /*  // API配置（实际应用中应该从配置文件或环境变量读取）
        metadata.setApiUrl("https://api.deepseek.com/v1");
        metadata.setApiKey("${DEEPSEEK_API_KEY}"); // 占位符，实际使用时替换*/
        
        return metadata;
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

