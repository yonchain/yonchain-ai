package com.yonchain.ai.model.impl;

import com.yonchain.ai.model.ModelConfig;
import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.registry.ModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

/**
 * 模型初始化器
 * 在应用启动时将配置的模型注册到注册中心
 */
public class ModelInitializer implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelInitializer.class);
    
    private final ModelRegistry modelRegistry;
    private final ModelProperties modelProperties;
    
    public ModelInitializer(ModelRegistry modelRegistry, ModelProperties modelProperties) {
        this.modelRegistry = modelRegistry;
        this.modelProperties = modelProperties;
    }
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Initializing models from configuration...");
        
        List<ModelConfig> allConfigs = modelProperties.getAllModelConfigs();
        int registeredCount = 0;
        
        for (ModelConfig config : allConfigs) {
            try {
                if (Boolean.TRUE.equals(config.getEnabled())) {
                    registerModel(config);
                    registeredCount++;
                } else {
                    logger.debug("Skipping disabled model: {}", config.getName());
                }
            } catch (Exception e) {
                logger.error("Failed to register model: " + config.getName(), e);
            }
        }
        
        logger.info("Model initialization completed. Registered {} models out of {} configured", 
            registeredCount, allConfigs.size());
    }
    
    /**
     * 注册单个模型
     * 
     * @param config 模型配置
     */
    private void registerModel(ModelConfig config) {
        // 创建模型元数据
        ModelMetadata metadata = new ModelMetadata();
        metadata.setName(config.getName());
        metadata.setProvider(config.getProvider());
        metadata.setType(config.getType());
        metadata.setConfig(config);
        metadata.setAvailable(true);
        
        // 设置显示名称为模型ID
        metadata.setDisplayName(config.getFullModelName());
        
        // 设置描述和其他属性
        metadata.setDescription(generateDescription(config));
        metadata.setMaxTokens(config.getMaxTokens());
        
        // 添加支持的特性
        addSupportedFeatures(metadata, config);
        
        // 注册到注册中心（内部会使用完整名称作为key）
        //TODO
        //modelRegistry.registerModel(metadata);
        
        logger.info("Registered model: {} -> {} (type: {}, provider: {})", 
            config.getName(), metadata.getModelId(), config.getType(), config.getProvider());
    }
    
    /**
     * 生成模型描述
     * 
     * @param config 模型配置
     * @return 模型描述
     */
    private String generateDescription(ModelConfig config) {
        if (config.getProvider() != null) {
            return String.format("%s model provided by %s", 
                config.getType().name().toLowerCase(), config.getProvider());
        } else {
            return String.format("%s model", config.getType().name().toLowerCase());
        }
    }
    
    /**
     * 添加支持的特性
     * 
     * @param metadata 模型元数据
     * @param config 模型配置
     */
    private void addSupportedFeatures(ModelMetadata metadata, ModelConfig config) {
        switch (config.getType()) {
            case TEXT:
                metadata.addSupportedFeature("chat");
                metadata.addSupportedFeature("completion");
                // 检查是否支持流式输出
                if (config.getProperty("streaming", false)) {
                    metadata.addSupportedFeature("streaming");
                }
                // 检查是否支持函数调用
                if (config.getProperty("function_calling", false)) {
                    metadata.addSupportedFeature("function_calling");
                }
                break;
            case IMAGE:
                metadata.addSupportedFeature("image_generation");
                if (config.getProperty("image_editing", false)) {
                    metadata.addSupportedFeature("image_editing");
                }
                if (config.getProperty("image_variation", false)) {
                    metadata.addSupportedFeature("image_variation");
                }
                break;
            case AUDIO:
                metadata.addSupportedFeature("transcription");
                if (config.getProperty("speech_synthesis", false)) {
                    metadata.addSupportedFeature("speech_synthesis");
                }
                break;
            case EMBEDDING:
                metadata.addSupportedFeature("text_embedding");
                metadata.addSupportedFeature("similarity_search");
                break;
            default:
                // 其他类型的默认特性
                break;
        }
    }
}
