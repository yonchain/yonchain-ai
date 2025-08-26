 package com.yonchain.ai.model.service;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.*;
import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.mapper.ModelMapper;
import com.yonchain.ai.model.mapper.ModelProviderMapper;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.util.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

/**
 * 模型服务实现类
 * <p>
 * 设计原则：
 * 1. 静态配置由YAML管理（提供商元数据、模型定义、能力配置）
 * 2. 动态配置由数据库管理（租户API密钥、个性化参数、启用状态）
 * 3. 运行时合并静态和动态配置提供完整视图
 * 4. 所有模型数据都从注册表获取
 * 5. 模型或提供商的API Key、Base URL的修改都同步到注册表
 */
@Slf4j
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelRegistry modelRegistry;

    @Autowired
    private ModelFactory modelFactory;

    @Autowired(required = false)
    private ModelMapper modelMapper;

    @Autowired(required = false)
    private ModelProviderMapper modelProviderMapper;

    @Autowired(required = false)
    private ApplicationEventPublisher eventPublisher;

    // ==================== 模型查询方法（从注册表获取数据）====================

    @Override
    public ModelInfo getModelById(String id) {
        return  modelRegistry.getModel(id);
    }

    @Override
    public ModelInfo getModel(String provider, String modelCode) {
        // 从注册表获取模型信息
        String modelId = modelCode + "-" + provider;
        // 直接返回注册表中的模型信息
        return modelRegistry.getModel(modelId);
    }

    @Override
    public List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam) {
        // 获取模型列表
        List<ModelInfo> modelInfos;
        if (queryParam != null && queryParam.containsKey("provider")) {
            String provider = String.valueOf(queryParam.get("provider"));
            modelInfos = modelRegistry.getModelsByProvider(provider);
        } else {
           throw new YonchainException("缺少提供商参数");
        }

        // 设置租户启用状态
        for (ModelInfo modelInfo : modelInfos) {
            boolean enabled = isModelEnabled(tenantId, modelInfo.getProvider(), modelInfo.getCode());
            modelInfo.setEnabled(enabled);
        }

        return modelInfos;
    }


    @Override
    public List<ModelProvider> getProviders(String tenantId, Map<String, Object> queryParam) {
        // 从注册表获取所有提供商信息
        List<ModelProvider> providers = modelRegistry.getProviders();

        // 设置租户启用状态
        for (ModelProvider provider : providers) {
            boolean enabled = isProviderEnabled(tenantId, provider.getCode());
            provider.setEnabled(enabled);
            provider.setModelCount(modelRegistry.getModelsByProvider(provider.getCode()).size());
        }

        return providers;
    }


    // ==================== 租户配置管理方法 ====================

    /**
     * 获取租户提供商配置（从注册表获取数据）
     */
    @Override
    public ProviderConfigResponse getProviderConfig(String tenantId, String providerCode) {
        ProviderConfigResponse response = new ProviderConfigResponse();

        // 设置提供商基本信息
        response.setCode(providerCode);

        // 从注册表获取提供商信息
        // 从注册表获取提供商信息
        ModelProvider provider = modelRegistry.getProviders()
                .stream()
                .filter(p -> p.getCode().equals(providerCode))
                .findFirst()
                .orElse(null);

        if (provider == null) {
            log.warn("未找到提供商: {}", providerCode);
            return response;
        }

        BeanUtils.copyProperties(provider, response);
        
        // 获取基础配置模式
        List<ModelConfigItem> configItems = provider.getConfigSchemas();
        
        // 获取租户级配置数据
        boolean enabled = provider.getEnabled() != null ? provider.getEnabled() : false;
        String lastUpdated = null;

        // 如果数据库配置可用，获取租户特定配置并回填到configItems
        if (modelProviderMapper != null) {
            ModelProviderEntity entity = modelProviderMapper.selectByTenantAndCode(tenantId, providerCode);
            if (entity != null) {
                enabled = entity.getEnabled();
                lastUpdated = entity.getUpdateTime() != null ? entity.getUpdateTime().toString() : null;

                // 将数据库中的配置值回填到configItems
                if (StringUtils.hasText(entity.getCustomConfig())) {
                    Map<String, Object> savedConfig = convertJsonToMapObject(entity.getCustomConfig());
                    configItems = fillConfigItemsWithSavedValues(configItems, savedConfig);
                }

            }
        }
        
        response.setConfigItems(configItems);
        response.setEnabled(enabled);
        response.setLastUpdated(lastUpdated);

        return response;
    }

    private List<ModelConfigItem> convertJsonToConfigItems(String customConfig) {
        List<ModelConfigItem> configItems = new ArrayList<>();
        if (StringUtils.hasText(customConfig)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                configItems = objectMapper.readValue(customConfig, new TypeReference<List<ModelConfigItem>>() {
                });
            } catch (Exception e) {
                log.error("解析自定义配置失败", e);
            }
        }
        return configItems;
    }

    /**
    /**
     * 保存租户提供商配置（同步到注册表）
     */
    @Override
    public void saveProviderConfig(String tenantId, String providerCode, Map<String, Object> config) {
        if (modelProviderMapper == null) {
            log.warn("数据库配置未启用，无法保存提供商配置");
            return;
        }

        ModelProviderEntity entity = modelProviderMapper.selectByTenantAndCode(tenantId, providerCode);
        if (entity == null) {
            entity = new ModelProviderEntity();
            entity.setId(IdUtil.generateId());
            entity.setTenantId(tenantId);
            entity.setProviderCode(providerCode);
            entity.setCustomConfig(convertMapToJson(config));
            entity.setEnabled((Boolean) config.get("enabled"));
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            modelProviderMapper.insert(entity);
        } else {
            entity.setEnabled((Boolean) config.get("enabled"));

            // 提取API Key和Base URL
            Map<String, Object> configMap = (Map<String, Object>) config.get("config");
            if (configMap != null) {
                if (configMap.containsKey("apiKey")) {
                    entity.setApiKey((String) configMap.get("apiKey"));
                }
                if (configMap.containsKey("baseUrl")) {
                    entity.setBaseUrl((String) configMap.get("baseUrl"));
                }
            }

            entity.setCustomConfig(convertMapToJson(configMap));
            entity.setUpdateTime(LocalDateTime.now());
            modelProviderMapper.update(entity);
        }

        // 从注册表获取提供商信息
        // 从注册表获取提供商信息
        ModelProvider provider = modelRegistry.getProviders()
                .stream()
                .filter(p -> p.getCode().equals(providerCode))
                .findFirst()
                .orElse(null);

        if (provider != null) {
            // 更新提供商信息
            provider.setEnabled(entity.getEnabled());

            // 同步到注册表
            syncProviderToRegistry(provider);
        }

        // 发布提供商配置变更事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new ProviderConfigChangeEvent(tenantId, providerCode, provider));
        }
    }

    /**
     * 保存租户模型配置（同步到注册表）
     */
    @Override
    public void saveModelConfig(String tenantId, ModelInfo modelInfo) {
        // 参数验证
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        if (modelInfo == null) {
            throw new IllegalArgumentException("模型信息不能为空");
        }
        if (!StringUtils.hasText(modelInfo.getCode())) {
            throw new IllegalArgumentException("模型代码不能为空");
        }

        if (modelMapper == null) {
            log.warn("数据库配置未启用，无法保存模型配置: tenantId={}, modelCode={}", tenantId, modelInfo.getCode());
            return;
        }

        try {
            // 验证模型是否存在于注册表中
            String modelId = modelInfo.getCode() + "-" + modelInfo.getProvider();
            ModelInfo registryInfo = modelRegistry.getModel(modelId);
            if (registryInfo == null) {
                throw new IllegalArgumentException("模型不存在: " + modelInfo.getCode());
            }

            ModelEntity entity = modelMapper.selectByTenantAndModelCode(tenantId, modelInfo.getCode());

            // 构建配置Map
            Map<String, Object> config = new HashMap<>();
            if (modelInfo.getCapabilities() != null) {
                config.put("capabilities", modelInfo.getCapabilities());
            }

            String configJson = convertMapToJson(config);

            if (entity == null) {
                // 创建新配置
                entity = new ModelEntity();
                entity.setId(IdUtil.generateId());
                entity.setTenantId(tenantId);
                entity.setModelCode(modelInfo.getCode());
                entity.setProviderCode(modelInfo.getProvider());
                entity.setModelConfig(configJson);
                entity.setEnabled(modelInfo.getEnabled());
                entity.setCreateTime(LocalDateTime.now());
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.insert(entity);
                log.info("创建模型配置成功: tenantId={}, modelCode={}", tenantId, modelInfo.getCode());

                // 同步到注册表
                syncModelToRegistry(modelInfo);
            } else {
                // 更新现有配置
                entity.setEnabled(modelInfo.getEnabled());
                entity.setModelConfig(configJson);
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.update(entity);
                log.info("更新模型配置成功: tenantId={}, modelCode={}", tenantId, modelInfo.getCode());

                // 同步到注册表
                syncModelToRegistry(modelInfo);

                // 发布模型配置变更事件
                if (eventPublisher != null) {
                    eventPublisher.publishEvent(new ModelConfigChangeEvent(tenantId, modelInfo.getCode(), modelInfo));
                }
            }
        } catch (Exception e) {
            log.error("保存模型配置失败: tenantId={}, modelCode={}", tenantId, modelInfo.getCode(), e);
            throw new RuntimeException("保存模型配置失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateModelStatus(String tenantId, String provider, String modelCode, boolean enabled) {
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        if (!StringUtils.hasText(provider)) {
            throw new IllegalArgumentException("提供商代码不能为空");
        }
        if (!StringUtils.hasText(modelCode)) {
            throw new IllegalArgumentException("模型代码不能为空");
        }

        if (modelMapper == null) {
            log.warn("数据库配置未启用，无法更新模型状态: tenantId={}, provider={}, modelCode={}", tenantId, provider, modelCode);
            return;
        }

        try {
            // 查询租户是否存在该模型数据（使用提供商代码作为条件）
            ModelEntity entity = modelMapper.selectByTenantProviderAndModelCode(tenantId, provider, modelCode);

            // 如果存在则更新，否则新增
            if (entity != null) {
                // 更新现有配置
                entity.setEnabled(enabled);
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.update(entity);
                log.info("更新模型状态成功: tenantId={}, provider={}, modelCode={}, enabled={}",
                        tenantId, provider, modelCode, enabled);

                // 从注册表获取模型信息
                String modelId = modelCode + "-" + provider;
                ModelInfo modelInfo = modelRegistry.getModel(modelId);
                if (modelInfo != null) {
                    // 更新模型状态
                    modelInfo.setEnabled(enabled);
                    
                    // 同步到注册表
                    syncModelToRegistry(modelInfo);

                    // 发布模型配置变更事件
                    if (eventPublisher != null) {
                        eventPublisher.publishEvent(new ModelConfigChangeEvent(tenantId, modelCode, modelInfo));
                    }
                }
            } else {
                // 创建新配置
                entity = new ModelEntity();
                entity.setId(IdUtil.generateId());
                entity.setTenantId(tenantId);
                entity.setModelCode(modelCode);
                entity.setProviderCode(provider);
                entity.setModelConfig("{}"); // 默认空配置
                entity.setEnabled(enabled);
                entity.setCreateTime(LocalDateTime.now());
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.insert(entity);
                log.info("创建模型状态成功: tenantId={}, provider={}, modelCode={}, enabled={}",
                        tenantId, provider, modelCode, enabled);

                // 从注册表获取模型信息
                String modelId = modelCode + "-" + provider;
                ModelInfo modelInfo = modelRegistry.getModel(modelId);
                if (modelInfo != null) {
                    // 更新模型状
                    modelInfo.setEnabled(enabled);
                    
                    // 同步到注册表
                    syncModelToRegistry(modelInfo);
                }
            }
        } catch (Exception e) {
            log.error("更新模型状态失败: tenantId={}, provider={}, modelCode={}", tenantId, provider, modelCode, e);
            throw new RuntimeException("更新模型状态失败: " + e.getMessage(), e);
        }
    }


    /**
     * 检查租户是否已配置指定提供商
     */
    private boolean isProviderEnabled(String tenantId, String providerCode) {
        try {
            if (modelProviderMapper == null) {
                return true; // 默认启用
            }

            ModelProviderEntity config = modelProviderMapper.selectByTenantAndCode(tenantId, providerCode);
            return config != null && config.getEnabled();
        } catch (Exception e) {
            log.warn("查询提供商配置失败: tenantId={}, providerCode={}", tenantId, providerCode, e);
            return false;
        }
    }

    /**
     * 检查租户是否已配置指定模型
     */
    private boolean isModelEnabled(String tenantId, String provider, String modelCode) {
        if (modelMapper == null) {
            return true; // 默认启用
        }

        ModelEntity config = modelMapper.selectByTenantProviderAndModelCode(tenantId, provider, modelCode);
        if (config != null) {
            return config.getEnabled();
        }
        //默认开启
        return true;
    }

    /**
     * 同步模型配置到注册表
     */
    private void syncModelToRegistry(ModelInfo modelInfo) {
        try {
            String modelId = modelInfo.getCode() + "-" + modelInfo.getProvider();
            ModelInfo registryInfo = modelRegistry.getModel(modelId);

            if (registryInfo != null) {
                // 重新注册到注册表
                modelRegistry.registerModel(registryInfo);

                // 清除模型工厂缓存，确保下次获取时使用最新配置
                modelFactory.invalidateModel(modelId);

                log.info("已同步模型配置到注册表: {}", modelId);
            }
        } catch (Exception e) {
            log.error("同步模型配置到注册表失败", e);
        }
    }


    /**
     * 同步提供商配置到注册表（使用ModelProvider接口）
     */
    private void syncProviderToRegistry(ModelProvider provider) {
        try {
            // 重新注册到注册表
            modelRegistry.registerProvider(provider);

            // 清除模型工厂缓存，确保下次获取时使用最新配置
         //   modelFactory.invalidateModel(modelId);

            log.info("已同步提供商配置到注册表: {}", provider.getCode());
        } catch (Exception e) {
            log.error("同步提供商配置到注册表失败", e);
        }
    }

    /**
     * 应用模型查询过滤条件
     */
    private List<ModelInfo> applyModelInfoFilters(List<ModelInfo> models, Map<String, Object> queryParam) {
        if (queryParam == null || queryParam.isEmpty()) {
            return models;
        }

        return models.stream()
                .filter(model -> {
                    // 按提供商过滤
                    if (queryParam.containsKey("provider")) {
                        String provider = (String) queryParam.get("provider");
                        if (StringUtils.hasText(provider) && !provider.equals(model.getProvider())) {
                            return false;
                        }
                    }

                    // 按类型过滤
                    if (queryParam.containsKey("type")) {
                        String type = (String) queryParam.get("type");
                        if (StringUtils.hasText(type) && !type.equals(model.getType())) {
                            return false;
                        }
                    }

                    // 按配置状态过滤
                    if (queryParam.containsKey("configured")) {
                        Boolean configured = (Boolean) queryParam.get("configured");
                        if (configured != null && !configured.equals(model.getEnabled())) {
                            return false;
                        }
                    }

                    // 按名称模糊搜索
                    if (queryParam.containsKey("keyword")) {
                        String keyword = (String) queryParam.get("keyword");
                        if (StringUtils.hasText(keyword)) {
                            String lowerKeyword = keyword.toLowerCase();
                            return model.getName() != null && model.getName().toLowerCase().contains(lowerKeyword) ||
                                    (model.getDescription() != null && model.getDescription().toLowerCase().contains(lowerKeyword));
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将JSON字符串转换为Map<String, Object>
     */
    private Map<String, Object> convertJsonToMapObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new YonchainException("转换失败", e);
        }
    }

    /**
     * 将Map转换为JSON字符串
     */
    private String convertMapToJson(Map<String, Object> map) {
        if (map == null) {
            return "{}";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("转换Map到JSON失败", e);
            return "{}";
        }
    }

    /**
     * 内部类：提供商配置变更事件
     */
    public static class ProviderConfigChangeEvent {
        private String tenantId;
        private String providerCode;
        private ModelProvider provider;

        public ProviderConfigChangeEvent(String tenantId, String providerCode, ModelProvider provider) {
            this.tenantId = tenantId;
            this.providerCode = providerCode;
            this.provider = provider;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getProviderCode() {
            return providerCode;
        }

        public ModelProvider getProvider() {
            return provider;
        }
    }


    // ==================== 配置回填辅助方法 ====================

    /**
     * 将保存的配置值回填到配置项中
     */
    private List<ModelConfigItem> fillConfigItemsWithSavedValues(List<ModelConfigItem> configItems, Map<String, Object> savedConfig) {
        if (configItems == null || savedConfig == null) {
            return configItems;
        }

        return configItems.stream().map(item -> {
            ModelConfigItem newItem = new ModelConfigItem();
            BeanUtils.copyProperties(item, newItem);
            
            // 如果保存的配置中有对应的值，则回填
            if (savedConfig.containsKey(item.getName())) {
                Object value = savedConfig.get(item.getName());
                if (value != null) {
                    newItem.setValue(value.toString());
                }
            }
            
            return newItem;
        }).collect(Collectors.toList());
    }


    /**
     * 内部类：模型配置变更事件
     */
    public static class ModelConfigChangeEvent {
        private String tenantId;
        private String modelCode;
        private ModelInfo model;

        public ModelConfigChangeEvent(String tenantId, String modelCode, ModelInfo model) {
            this.tenantId = tenantId;
            this.modelCode = modelCode;
            this.model = model;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getModelCode() {
            return modelCode;
        }

        public ModelInfo getModel() {
            return model;
        }
    }
}
