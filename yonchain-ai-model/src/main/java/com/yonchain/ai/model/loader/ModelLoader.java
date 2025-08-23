package com.yonchain.ai.model.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yonchain.ai.api.model.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 静态模型配置加载器
 * <p>
 * 负责从YAML文件加载模型提供商和模型的静态配置信息
 */
@Slf4j
@Component
public class ModelLoader {

    private final Map<String, DefaultModelProvider> providerConfigs = new HashMap<>();
    private final Map<String, DefaultModel> modelConfigs = new HashMap<>();
    private final Map<String, List<DefaultModel>> providerModelMap = new HashMap<>();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @PostConstruct
    public void init() {
        try {
            // 加载所有提供商配置
            loadProviderConfigs();
            // 加载所有模型配置
            loadModelConfigs();
            // 构建提供商-模型映射关系并计算模型数量
            buildProviderModelMapping();
            log.info("静态模型配置加载完成: {} 个提供商, {} 个模型", providerConfigs.size(), modelConfigs.size());
        } catch (Exception e) {
            log.error("加载静态模型配置失败", e);
        }
    }

    /**
     * 加载所有提供商配置
     */
    private void loadProviderConfigs() throws IOException {
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
    }

    /**
     * 加载所有模型配置
     */
    private void loadModelConfigs() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:models/**/**.yaml");

        for (Resource resource : resources) {
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, Object> yamlMap = yamlMapper.readValue(inputStream, Map.class);
                
                // 检查是否为模型配置文件
                if (yamlMap.containsKey("model")) {
                    DefaultModel model = loadModelConfig((Map<String, Object>) yamlMap.get("model"));
                    modelConfigs.put(model.getCode(), model);
                    log.debug("加载模型配置: {}", model.getCode());
                }
            } catch (Exception e) {
                log.warn("加载模型配置失败: {}", resource.getFilename(), e);
            }
        }
    }

    /**
     * 构建提供商-模型映射关系并计算模型数量
     */
    private void buildProviderModelMapping() {
        // 清空现有映射
        providerModelMap.clear();
        
        // 按提供商分组模型
        Map<String, List<DefaultModel>> groupedModels = modelConfigs.values().stream()
                .filter(model -> StringUtils.hasText(model.getProvider()))
                .collect(Collectors.groupingBy(DefaultModel::getProvider));
        
        // 构建映射并设置模型数量
        for (Map.Entry<String, List<DefaultModel>> entry : groupedModels.entrySet()) {
            String providerCode = entry.getKey();
            List<DefaultModel> models = entry.getValue();
            
            // 存储映射关系
            providerModelMap.put(providerCode, models);
            
            // 设置提供商的模型数量
            DefaultModelProvider provider = providerConfigs.get(providerCode);
            if (provider != null) {
                provider.setModelCount(models.size());
                log.debug("提供商 {} 包含 {} 个模型", providerCode, models.size());
            }
        }
        
        // 为没有模型的提供商设置数量为0
        for (DefaultModelProvider provider : providerConfigs.values()) {
            if (provider.getModelCount() == null) {
                provider.setModelCount(0);
            }
        }
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
        
        // 能力配置
        if (yamlMap.containsKey("capabilities")) {
            model.setCapabilities((List<String>)yamlMap.get("capabilities"));
        }
        
        // 配置模式
        if (yamlMap.containsKey("configSchemas")) {
            List<ModelConfigItem> configItems = convertListToModelConfigItems((List<Map<String, Object>>) yamlMap.get("configSchemas"));
            model.setConfigSchema(configItems);
        }
        
        return model;
    }

    /**
     * 将Map格式的configSchema转换为List<ModelConfigItem>
     */
    @SuppressWarnings("unchecked")
    private List<ModelConfigItem> convertMapToModelConfigItems(Map<String, Object> configSchema) {
        if (configSchema == null) {
            return Collections.emptyList();
        }
        
        List<ModelConfigItem> items = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : configSchema.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> itemConfig = (Map<String, Object>) entry.getValue();
            
            ModelConfigItem item = new ModelConfigItem();
            item.setName(key);
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

    /**
     * 获取所有提供商配置
     */
    public Collection<DefaultModelProvider> getAllProviderConfigs() {
        return providerConfigs.values();
    }

    /**
     * 获取所有模型配置
     */
    public Collection<DefaultModel> getAllModelConfigs() {
        return modelConfigs.values();
    }

    /**
     * 根据ID获取提供商配置
     */
    public DefaultModelProvider getProviderConfig(String providerId) {
        return providerConfigs.get(providerId);
    }

    /**
     * 根据ID获取模型配置
     */
    public DefaultModel getModelConfig(String modelId) {
        return modelConfigs.get(modelId);
    }

    /**
     * 获取指定提供商的所有模型配置（优化版本，直接从映射获取）
     */
    public List<DefaultModel> getModelConfigsByProvider(String providerCode) {
        if (!StringUtils.hasText(providerCode)) {
            return Collections.emptyList();
        }
        
        List<DefaultModel> models = providerModelMap.get(providerCode);
        return models != null ? new ArrayList<>(models) : Collections.emptyList();
    }

    /**
     * 获取指定提供商的模型数量
     */
    public int getModelCountByProvider(String providerCode) {
        if (!StringUtils.hasText(providerCode)) {
            return 0;
        }
        
        List<DefaultModel> models = providerModelMap.get(providerCode);
        return models != null ? models.size() : 0;
    }

    /**
     * 获取所有提供商的模型统计信息
     */
    public Map<String, Integer> getProviderModelStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, List<DefaultModel>> entry : providerModelMap.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }

    /**
     * 检查提供商是否存在
     */
    public boolean hasProvider(String providerCode) {
        return StringUtils.hasText(providerCode) && providerConfigs.containsKey(providerCode);
    }

    /**
     * 检查模型是否存在
     */
    public boolean hasModel(String modelCode) {
        return StringUtils.hasText(modelCode) && modelConfigs.containsKey(modelCode);
    }

    /**
     * 检查指定提供商是否有模型
     */
    public boolean hasModelsForProvider(String providerCode) {
        return getModelCountByProvider(providerCode) > 0;
    }
}