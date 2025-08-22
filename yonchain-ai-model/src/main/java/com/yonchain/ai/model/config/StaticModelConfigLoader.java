package com.yonchain.ai.model.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yonchain.ai.api.exception.YonchainException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 静态模型配置加载器
 * 从YAML文件加载提供商和模型的静态配置信息
 */
@Slf4j
@Component
public class StaticModelConfigLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    
    private final Map<String, ProviderConfig> providerConfigs = new HashMap<>();
    private final Map<String, ModelConfig> modelConfigs = new HashMap<>();

    @PostConstruct
    public void loadConfigurations() {
        loadProviderConfigurations();
        loadModelConfigurations();
        log.info("Loaded {} providers and {} models", providerConfigs.size(), modelConfigs.size());
    }

    /**
     * 加载提供商配置
     */
    private void loadProviderConfigurations() {
        try {
            Resource[] resources = resourceResolver.getResources("classpath*:models/*.yaml");
            for (Resource resource : resources) {
                try {
                    String filename = resource.getFilename();
                    if (filename != null && !filename.contains("-")) {
                        // 提供商配置文件（如 openai.yaml, anthropic.yaml）
                        Map<String, Object> yamlData = yamlMapper.readValue(resource.getInputStream(), Map.class);
                        ProviderConfig config = loadProviderConfig(yamlData, filename);
                        if (config != null) {
                            providerConfigs.put(config.getCode(), config);
                            log.debug("Loaded provider config: {}", config.getCode());
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to load provider config from {}: {}", resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan provider configuration files", e);
        }
    }

    /**
     * 加载模型配置
     */
    private void loadModelConfigurations() {
        try {
            Resource[] resources = resourceResolver.getResources("classpath*:models/*.yaml");
            for (Resource resource : resources) {
                try {
                    String filename = resource.getFilename();
                    if (filename != null && filename.contains("-")) {
                        // 模型配置文件（如 gpt-4.yaml, claude-3-sonnet.yaml）
                        Map<String, Object> yamlData = yamlMapper.readValue(resource.getInputStream(), Map.class);
                        ModelConfig config = loadModelConfig(yamlData, filename);
                        if (config != null) {
                            modelConfigs.put(config.getCode(), config);
                            log.debug("Loaded model config: {}", config.getCode());
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to load model config from {}: {}", resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan model configuration files", e);
        }
    }

    /**
     * 从YAML数据加载提供商配置
     */
    private ProviderConfig loadProviderConfig(Map<String, Object> yamlData, String filename) {
        try {
            ProviderConfig config = yamlMapper.convertValue(yamlData, ProviderConfig.class);
            if (config.getCode() == null) {
                config.setCode(filename.replace(".yaml", "").replace(".yml", ""));
            }
            return config;
        } catch (Exception e) {
            log.error("Failed to parse provider config from {}: {}", filename, e.getMessage());
            throw new YonchainException("加载模型提供商失败",e);
        }
    }

    /**
     * 从YAML数据加载模型配置
     */
    private ModelConfig loadModelConfig(Map<String, Object> yamlData, String filename) {
        try {
            ModelConfig config = yamlMapper.convertValue(yamlData.get("model"), ModelConfig.class);
            if (config.getCode() == null) {
                config.setCode(filename.replace(".yaml", "").replace(".yml", ""));
            }
            return config;
        } catch (Exception e) {
            log.error("Failed to parse model config from {}: {}", filename, e.getMessage());
            e.printStackTrace();
            throw new YonchainException("加载模型失败",e);
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 获取提供商配置
     */
    public ProviderConfig getProviderConfig(String providerCode) {
        return providerConfigs.get(providerCode);
    }

    /**
     * 获取模型配置
     */
    public ModelConfig getModelConfig(String modelCode) {
        return modelConfigs.get(modelCode);
    }

    /**
     * 获取所有提供商配置
     */
    public Collection<ProviderConfig> getAllProviderConfigs() {
        return providerConfigs.values();
    }

    /**
     * 获取所有模型配置
     */
    public Collection<ModelConfig> getAllModelConfigs() {
        return modelConfigs.values();
    }

    /**
     * 根据提供商获取模型配置
     */
    public Collection<ModelConfig> getModelConfigsByProvider(String providerCode) {
        return modelConfigs.values().stream()
                .filter(config -> providerCode.equals(config.getProvider()))
                .collect(Collectors.toList());
    }

    // ==================== 配置类 ====================

    /**
     * 提供商配置
     */
    @Data
    public static class ProviderConfig {
        private String code;
        private String name;
        private String description;
        private String icon;
        private String website;
        private String apiDocUrl;
        private List<String> supportedFeatures;
        private Map<String, Object> configSchema;
    }

    /**
     * 模型配置
     */
    @Data
    public static class ModelConfig {
        private String code;
        private String name;
        private String description;
        private String icon;
        private String provider;
        private String type;
        private String version;
        private Integer sortOrder;
        private List<String> capabilities;
        private Map<String, Object> configSchema;
    }

    // ==================== 配置视图类 ====================

    /**
     * 提供商配置视图（静态配置 + 租户状态）
     */
    public static class ProviderConfigView {
        private String code;
        private String name;
        private String description;
        private String icon;
        private String website;
        private String apiDocUrl;
        private List<String> supportedFeatures;
        private boolean configured;  // 租户是否已配置
        private boolean enabled;     // 租户是否启用

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        public String getApiDocUrl() { return apiDocUrl; }
        public void setApiDocUrl(String apiDocUrl) { this.apiDocUrl = apiDocUrl; }
        public List<String> getSupportedFeatures() { return supportedFeatures; }
        public void setSupportedFeatures(List<String> supportedFeatures) { this.supportedFeatures = supportedFeatures; }
        public boolean isConfigured() { return configured; }
        public void setConfigured(boolean configured) { this.configured = configured; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    /**
     * 模型配置视图（静态配置 + 租户配置）
     */
    public static class ModelConfigView {
        private String code;
        private String name;
        private String description;
        private String provider;
        private String type;
        private String version;
     //   private Map<String, Object> capabilities;
        private Map<String, Object> configSchema;
        private Map<String, Object> defaultParameters;
        private Map<String, Object> limits;
        private boolean configured;  // 租户是否已配置
        private boolean enabled;     // 租户是否启用
        private Map<String, Object> tenantDefaultParams;  // 租户默认参数
        private Integer sortOrder;   // 租户排序

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        //public Map<String, Object> getCapabilities() { return capabilities; }
      //  public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
        public Map<String, Object> getParameterSchema() { return configSchema; }
        public void setParameterSchema(Map<String, Object> configSchema) { this.configSchema = configSchema; }
        public Map<String, Object> getDefaultParameters() { return defaultParameters; }
        public void setDefaultParameters(Map<String, Object> defaultParameters) { this.defaultParameters = defaultParameters; }
        public Map<String, Object> getLimits() { return limits; }
        public void setLimits(Map<String, Object> limits) { this.limits = limits; }
        public boolean isConfigured() { return configured; }
        public void setConfigured(boolean configured) { this.configured = configured; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Map<String, Object> getTenantDefaultParams() { return tenantDefaultParams; }
        public void setTenantDefaultParams(Map<String, Object> tenantDefaultParams) { this.tenantDefaultParams = tenantDefaultParams; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }
}