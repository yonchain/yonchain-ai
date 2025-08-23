package com.yonchain.ai.model.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.ModelConfigItem;
import com.yonchain.ai.api.model.ModelProviderConfigItem;
import com.yonchain.ai.api.model.ModelProviderCapabilities;
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
    
    private final Map<String, StaticModelProvider> providerConfigs = new HashMap<>();
    private final Map<String, StaticModelInfo> modelConfigs = new HashMap<>();

    @PostConstruct
    public void loadConfigurations() {
        loadAllConfigurations();
        log.info("Loaded {} providers and {} models", providerConfigs.size(), modelConfigs.size());
    }

    /**
     * 统一加载所有配置文件，避免重复扫描
     */
    private void loadAllConfigurations() {
        try {
            Resource[] resources = resourceResolver.getResources("classpath*:models/*.yaml");
            for (Resource resource : resources) {
                try {
                    String filename = resource.getFilename();
                    if (filename != null) {
                        Map<String, Object> yamlData = yamlMapper.readValue(resource.getInputStream(), Map.class);
                        
                        if (yamlData.containsKey("provider")) {
                            // 提供商配置文件（如 openai.yaml, anthropic.yaml）
                            StaticModelProvider config = loadProviderConfig((Map<String, Object>) yamlData.get("provider"), filename);
                            if (config != null) {
                                providerConfigs.put(config.getCode(), config);
                                log.debug("Loaded provider config: {}", config.getCode());
                            }
                        } else if(yamlData.containsKey("model")) {
                            // 模型配置文件（如 gpt-4.yaml, claude-3-sonnet.yaml）
                            StaticModelInfo config = loadModelConfig((Map<String, Object>) yamlData.get("model"), filename);
                            if (config != null) {
                                modelConfigs.put(config.getCode(), config);
                                log.debug("Loaded model config: {}", config.getCode());
                            }
                        }else {
                            throw new YonchainException("不支持的配置");
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to load config from {}: {}", resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan configuration files", e);
        }
    }

    /**
     * 从YAML数据加载提供商配置
     */
    private StaticModelProvider loadProviderConfig(Map<String, Object> yamlData, String filename) {
        try {
            StaticModelProvider config = yamlMapper.convertValue(yamlData, StaticModelProvider.class);
            if (config.getCode() == null) {
                config.setCode(filename.replace(".yaml", "").replace(".yml", ""));
            }
            return config;
        } catch (Exception e) {
            log.error("加载模型提供商失败", e);
            throw new YonchainException("加载模型提供商失败", e);
        }
    }

    /**
     * 从YAML数据加载模型配置
     */
    private StaticModelInfo loadModelConfig(Map<String, Object> yamlData, String filename) {
        try {
            StaticModelInfo config = yamlMapper.convertValue(yamlData, StaticModelInfo.class);
            if (config.getCode() == null) {
                config.setCode(filename.replace(".yaml", "").replace(".yml", ""));
            }
            return config;
        } catch (Exception e) {
            log.error("加载模型失败", e);
            throw new YonchainException("加载模型失败", e);
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 获取提供商配置
     */
    public StaticModelProvider getProviderConfig(String providerCode) {
        return providerConfigs.get(providerCode);
    }

    /**
     * 获取模型配置
     */
    public StaticModelInfo getModelConfig(String modelCode) {
        return modelConfigs.get(modelCode);
    }

    /**
     * 获取所有提供商配置
     */
    public Collection<StaticModelProvider> getAllProviderConfigs() {
        return providerConfigs.values();
    }

    /**
     * 获取所有模型配置
     */
    public Collection<StaticModelInfo> getAllModelConfigs() {
        return modelConfigs.values();
    }

    /**
     * 根据提供商获取模型配置
     */
    public Collection<StaticModelInfo> getModelConfigsByProvider(String providerCode) {
        return modelConfigs.values().stream()
                .filter(config -> providerCode.equals(config.getProvider()))
                .collect(Collectors.toList());
    }

    // ==================== 实现类 ====================

    /**
     * 静态模型提供商实现类
     */
    @Data
    public static class StaticModelProvider {
        private String id;
        private String tenantId;
        private String code;
        private String name;
        private String description;
        private String icon;
        private Integer sortOrder;
        private List<String> supportedModelTypes;
        private Map<String, Object> configSchema;
        private ModelProviderCapabilities capabilities;
        private Boolean enabled;


    }

    /**
     * 静态模型信息实现类
     */
    @Data
    public static class StaticModelInfo {
        private String id;
        private String tenantId;
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
        private Boolean enabled;
    }
}