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
        
        // 定义多个扫描路径，支持从不同模块加载配置，同时支持yml和yaml扩展名
        String[] scanPaths = {
            "classpath*:models/*.yml",                    // 新的模型配置路径（yml）
            "classpath*:models/*.yaml"                   // 新的模型配置路径（yaml）
        };
        
        Set<String> loadedProviders = new HashSet<>(); // 防止重复加载同一提供商
        
        for (String scanPath : scanPaths) {
            try {
                Resource[] resources = resourceResolver.getResources(scanPath);
                log.info("Found {} config files in path: {}", resources.length, scanPath);
                
                for (Resource resource : resources) {
                    String filename = resource.getFilename();
                    if (filename == null) {
                        continue;
                    }
                    
                    try {
                        log.debug("Loading config from: {}", resource.getURI());
                        
                        // 检查是否是提供商配置文件（包含provider配置）
                        Map<String, Object> configMap = yamlMapper.readValue(resource.getInputStream(), Map.class);
                        if (configMap.containsKey("provider")) {
                            ProviderConfig config = yamlMapper.convertValue(configMap, ProviderConfig.class);
                            String providerCode = config.getProvider().getCode();
                            
                            if (!loadedProviders.contains(providerCode)) {
                                ModelProvider provider = convertToModelProvider(config);
                                providers.add(provider);
                                loadedProviders.add(providerCode);
                                log.info("Successfully loaded provider: {} from {}", providerCode, filename);
                            } else {
                                log.debug("Provider {} already loaded, skipping {}", providerCode, filename);
                            }
                        } else if (configMap.containsKey("model")) {
                            // 这是单个模型配置文件，暂时跳过（可以在后续版本中支持）
                            log.debug("Skipping individual model config file: {}", filename);
                        }
                    } catch (IOException e) {
                        log.error("Failed to load config from {}: {}", filename, e.getMessage());
                    }
                }
            } catch (IOException e) {
                log.warn("Failed to scan config files in path {}: {}", scanPath, e.getMessage());
            }
        }
        
        log.info("Total loaded {} model providers", providers.size());
        return providers;
    }
    
    /**
     * 根据提供商代码加载特定提供商配置
     * @param providerCode 提供商代码
     * @return 模型提供商，如果未找到返回null
     */
    public ModelProvider loadProviderByCode(String providerCode) {
        String[] scanPaths = {
            "classpath*:model-providers/" + providerCode + ".yml",
            "classpath*:model-providers/" + providerCode + ".yaml",
            "classpath*:model-providers/" + providerCode + "/*.yml",
            "classpath*:model-providers/" + providerCode + "/*.yaml",
            "classpath*:META-INF/model-providers/" + providerCode + ".yml",
            "classpath*:META-INF/model-providers/" + providerCode + ".yaml",
            "classpath*:META-INF/model-providers/" + providerCode + "/*.yml",
            "classpath*:META-INF/model-providers/" + providerCode + "/*.yaml"
        };
        
        for (String scanPath : scanPaths) {
            try {
                Resource[] resources = resourceResolver.getResources(scanPath);
                for (Resource resource : resources) {
                    try {
                        ProviderConfig config = yamlMapper.readValue(resource.getInputStream(), ProviderConfig.class);
                        ModelProvider provider = convertToModelProvider(config);
                        if (providerCode.equals(provider.getCode())) {
                            log.info("Successfully loaded provider: {} from {}", providerCode, resource.getFilename());
                            return provider;
                        }
                    } catch (IOException e) {
                        log.error("Failed to load provider config from {}: {}", resource.getFilename(), e.getMessage());
                    }
                }
            } catch (IOException e) {
                log.debug("No provider config found in path: {}", scanPath);
            }
        }
        
        log.warn("Provider config not found for code: {}", providerCode);
        return null;
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
        provider.setIcon(config.getProvider().getIcon());
        provider.setWebsiteUrl(config.getProvider().getWebsiteUrl());
        provider.setSupportedModelTypes(config.getProvider().getSupportedModelTypes());
        
        // 将JsonNode转换为Map<String, Object>
        if (config.getProvider().getConfigSchema() != null) {
            provider.setConfigSchema(yamlMapper.convertValue(
                yamlMapper.valueToTree(config.getProvider().getConfigSchema()), 
                yamlMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
            ));
        }
        
        // 设置模型列表
        List<AIModel> models = new ArrayList<>();
        if (config.getModels() != null) {
            for (ModelConfig modelConfig : config.getModels()) {
                AIModel model = new AIModel();
                model.setCode(modelConfig.getCode());
                model.setName(modelConfig.getName());
                model.setDescription(modelConfig.getDescription());
                model.setIcon(modelConfig.getIcon());
                model.setModelType(modelConfig.getModelType());
                model.setVersion(modelConfig.getVersion());
                model.setCapabilities(modelConfig.getCapabilities().toArray(new String[modelConfig.getCapabilities().size()]));
                
                // 将JsonNode转换为Map<String, Object>
                if (modelConfig.getConfigSchema() != null) {
                    model.setConfigSchema(yamlMapper.convertValue(
                        yamlMapper.valueToTree(modelConfig.getConfigSchema()), 
                        yamlMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
                    ));
                }
                
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
                
                // 将JsonNode转换为Map<String, Object>
                if (capConfig.getParamSchema() != null) {
                    capability.setParamSchema(yamlMapper.convertValue(
                        yamlMapper.valueToTree(capConfig.getParamSchema()), 
                        yamlMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
                    ));
                }
                
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
        private String icon;
        private String websiteUrl;
        private List<String> supportedModelTypes;
        private Map<String, Object> configSchema;
    }
    
    @Data
    public static class ModelConfig {
        private String code;
        private String name;
        private String description;
        private String icon;
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