package com.yonchain.ai.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.vo.ModelCapability;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 模型提供商配置加载器
 * 从YAML文件中加载模型提供商和模型配置
 */
@Slf4j
@Component
public class ModelProviderConfigLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    
    /**
     * 加载所有模型提供商配置
     * @return 模型提供商列表
     */
    public List<ModelProvider> loadAllProviders() {
        List<ModelProvider> providers = new ArrayList<>();
        try {
            Resource[] resources = resourceResolver.getResources("classpath:model-providers/*.yml");
            for (Resource resource : resources) {
                try {
                    ProviderConfig config = yamlMapper.readValue(resource.getInputStream(), ProviderConfig.class);
                    ModelProvider provider = convertToModelProvider(config);
                    providers.add(provider);
                } catch (IOException e) {
                    log.error("Failed to load provider config from {}: {}", resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan provider config files: {}", e.getMessage());
        }
        return providers;
    }
    
    /**
     * 将配置对象转换为ModelProvider实体
     * @param config YAML配置对象
     * @return ModelProvider实体
     */
    private ModelProvider convertToModelProvider(ProviderConfig config) {
        ModelProvider provider = new ModelProvider();
        provider.setCode(config.getProvider().getCode());
        provider.setName(config.getProvider().getName());
        provider.setDescription(config.getProvider().getDescription());
        provider.setIconUrl(config.getProvider().getIconUrl());
        provider.setWebsiteUrl(config.getProvider().getWebsiteUrl());
        provider.setSupportedModelTypes(String.join(",", config.getProvider().getSupportedModelTypes()));
        provider.setConfigSchema(yamlMapper.valueToTree(config.getProvider().getConfigSchema()));
        
        // 设置模型列表
        List<AIModel> models = new ArrayList<>();
        if (config.getModels() != null) {
            for (ModelConfig modelConfig : config.getModels()) {
                AIModel model = new AIModel();
                model.setCode(modelConfig.getCode());
                model.setName(modelConfig.getName());
                model.setDescription(modelConfig.getDescription());
                model.setIconUrl(modelConfig.getIconUrl());
                model.setModelType(modelConfig.getModelType());
                model.setVersion(modelConfig.getVersion());
                model.setCapabilities(String.join(",", modelConfig.getCapabilities()));
                model.setConfigSchema(yamlMapper.valueToTree(modelConfig.getConfigSchema()));
                model.setProviderCode(provider.getCode());
                models.add(model);
            }
        }
        provider.setModels(models);
        
        // 设置能力列表
        Map<String, ModelCapability> capabilities = new HashMap<>();
        if (config.getCapabilities() != null) {
            config.getCapabilities().forEach((key, capConfig) -> {
                ModelCapability capability = new ModelCapability();
                capability.setCode(capConfig.getCode());
                capability.setName(capConfig.getName());
                capability.setDescription(capConfig.getDescription());
                capability.setType(capConfig.getType());
                capability.setIcon(capConfig.getIcon());
                capability.setSupportsStreaming(capConfig.isSupportsStreaming());
                capability.setParamSchema(yamlMapper.valueToTree(capConfig.getParamSchema()));
                capabilities.put(key, capability);
            });
        }
        provider.setCapabilities(capabilities);
        
        return provider;
    }
    
    /**
     * YAML配置类
     */
    @Data
    public static class ProviderConfig {
        private ProviderDetailConfig provider;
        private List<ModelConfig> models;
        private Map<String, CapabilityConfig> capabilities;
    }
    
    @Data
    public static class ProviderDetailConfig {
        private String code;
        private String name;
        private String description;
        private String iconUrl;
        private String websiteUrl;
        private List<String> supportedModelTypes;
        private Map<String, Object> configSchema;
    }
    
    @Data
    public static class ModelConfig {
        private String code;
        private String name;
        private String description;
        private String iconUrl;
        private String modelType;
        private String version;
        private List<String> capabilities;
        private Map<String, Object> configSchema;
    }
    
    @Data
    public static class CapabilityConfig {
        private String code;
        private String name;
        private String description;
        private String type;
        private String icon;
        private boolean supportsStreaming;
        private Map<String, Object> paramSchema;
    }
}