package com.yonchain.ai.model.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yonchain.ai.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * YAML模型加载器
 * <p>
 * 从YAML文件加载模型和提供商的配置信息
 * 遵循单一职责原则，只负责加载，不负责存储
 * 
 * @author Cgy
 */
@Slf4j
@Component
public class YamlModelLoader implements ModelLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Override
    public Collection<? extends ModelProvider> loadProviders() {
        Map<String, DefaultModelProvider> providerConfigs = new HashMap<>();
        
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:models/**/**.yaml");

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    Map<String, Object> yamlMap = yamlMapper.readValue(inputStream, Map.class);

                    // 检查是否为提供商配置文件
                    if (yamlMap.containsKey("provider")) {
                        DefaultModelProvider provider = loadProviderConfig((Map<String, Object>) yamlMap.get("provider"));
                        providerConfigs.put(provider.getCode(), provider);
                        log.debug("加载提供商配置: {}", provider.getCode());
                    }
                } catch (Exception e) {
                    log.warn("加载提供商配置失败: {}", resource.getFilename(), e);
                }
            }
            
            log.info("从YAML加载了 {} 个提供商配置", providerConfigs.size());
        } catch (Exception e) {
            log.error("加载提供商配置失败", e);
        }
        
        return providerConfigs.values();
    }

    @Override
    public Collection<? extends ModelInfo> loadModels() {
        Map<String, DefaultModel> modelConfigs = new HashMap<>();
        
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:models/**/**.yaml");

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    Map<String, Object> yamlMap = yamlMapper.readValue(inputStream, Map.class);

                    // 检查是否为模型配置文件
                    if (yamlMap.containsKey("model")) {
                        DefaultModel model = loadModelConfig((Map<String, Object>) yamlMap.get("model"));
                        String key = model.getProvider() + "&" + model.getCode();
                        modelConfigs.put(key, model);
                        log.debug("加载模型配置: {}", model.getCode());
                    }
                } catch (Exception e) {
                    log.warn("加载模型配置失败: {}", resource.getFilename(), e);
                }
            }
            
            log.info("从YAML加载了 {} 个模型配置", modelConfigs.size());
        } catch (Exception e) {
            log.error("加载模型配置失败", e);
        }
        
        return modelConfigs.values();
    }

    /**
     * 解析提供商配置
     */
    @SuppressWarnings("unchecked")
    private DefaultModelProvider loadProviderConfig(Map<String, Object> yamlMap) {
        DefaultModelProvider provider = new DefaultModelProvider();

        // 基本信息
        provider.setCode((String) yamlMap.get("code"));
        provider.setName((String) yamlMap.get("name"));
        provider.setDescription((String) yamlMap.get("description"));
        provider.setIcon((String) yamlMap.get("icon"));
        provider.setEnabled((Boolean) yamlMap.getOrDefault("enabled", true));

        // 支持的模型类型
        if (yamlMap.containsKey("supportedModelTypes")) {
            List<String> types = (List<String>) yamlMap.get("supportedModelTypes");
            provider.setSupportedModelTypes(types);
        }

        // 配置模式
        if (yamlMap.containsKey("configSchemas")) {
            List<ModelConfigItem> configItems = convertListToModelConfigItems((List<Map<String, Object>>) yamlMap.get("configSchemas"));
            provider.setConfigSchemas(configItems);
        }
        return provider;
    }

    /**
     * 解析模型配置
     */
    @SuppressWarnings("unchecked")
    private DefaultModel loadModelConfig(Map<String, Object> yamlMap) {
        DefaultModel model = new DefaultModel();

        // 基本信息
        model.setCode((String) yamlMap.get("code"));
        model.setName((String) yamlMap.get("name"));
        model.setDescription((String) yamlMap.get("description"));
        model.setProvider((String) yamlMap.get("provider"));
        model.setType((String) yamlMap.get("modelType"));
        model.setIcon((String) yamlMap.get("icon"));
        model.setEnabled((Boolean) yamlMap.getOrDefault("enabled", true));

        // 能力配置
        if (yamlMap.containsKey("capabilities")) {
            model.setCapabilities((List<String>) yamlMap.get("capabilities"));
        }

        // 配置模式
        if (yamlMap.containsKey("configSchemas")) {
            List<ModelConfigItem> configItems = convertListToModelConfigItems((List<Map<String, Object>>) yamlMap.get("configSchemas"));
            model.setConfigSchemas(configItems);
        }

        return model;
    }

    /**
     * 将List格式的configSchemas转换为List<ModelConfigItem>
     */
    @SuppressWarnings("unchecked")
    private List<ModelConfigItem> convertListToModelConfigItems(List<Map<String, Object>> configSchemas) {
        if (configSchemas == null) {
            return Collections.emptyList();
        }

        List<ModelConfigItem> items = new ArrayList<>();

        for (Map<String, Object> itemConfig : configSchemas) {
            ModelConfigItem item = new ModelConfigItem();
            item.setName((String) itemConfig.get("name"));
            item.setType((String) itemConfig.get("type"));
            item.setTitle((String) itemConfig.get("title"));
            item.setDescription((String) itemConfig.get("description"));

            if (itemConfig.containsKey("defaultValue")) {
                item.setDefaultValue(itemConfig.get("defaultValue"));
            } else if (itemConfig.containsKey("default")) {
                item.setDefaultValue(itemConfig.get("default"));
            }

            if (itemConfig.containsKey("required")) {
                item.setRequired((Boolean) itemConfig.get("required"));
            }

            if (itemConfig.containsKey("min")) {
                item.setMin((Number) itemConfig.get("min"));
            } else if (itemConfig.containsKey("minimum")) {
                item.setMin((Number) itemConfig.get("minimum"));
            }

            if (itemConfig.containsKey("max")) {
                item.setMax((Number) itemConfig.get("max"));
            } else if (itemConfig.containsKey("maximum")) {
                item.setMax((Number) itemConfig.get("maximum"));
            }

            items.add(item);
        }

        return items;
    }
}