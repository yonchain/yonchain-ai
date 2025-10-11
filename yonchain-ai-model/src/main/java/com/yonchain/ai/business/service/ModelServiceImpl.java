package com.yonchain.ai.business.service;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.*;
import com.yonchain.ai.model.ModelClient;
import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.business.ModelConfig;
import com.yonchain.ai.business.ModelMetadata;
import com.yonchain.ai.business.entity.ModelEntity;
import com.yonchain.ai.business.entity.ModelProviderEntity;
import com.yonchain.ai.business.mapper.ModelMapper;
import com.yonchain.ai.business.mapper.ModelProviderMapper;
import com.yonchain.ai.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 模型服务实现类
 */
@Slf4j
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired(required = false)
    private ModelMapper modelMapper;

    @Autowired(required = false)
    private ModelProviderMapper modelProviderMapper;

    @Autowired(required = false)
    private ModelClient modelClient;


    @Override
    public ModelInfo getModelById(String id) {
        log.debug("Getting model by id: {}", id);

        try {
            ModelEntity modelEntity = modelMapper.selectById(id);
            if (modelEntity == null) {
                log.debug("Model not found with id: {}", id);
                return null;
            }

            return convertToModelInfo(modelEntity);

        } catch (Exception e) {
            log.error("Failed to get model by id: {}", id, e);
            throw new RuntimeException("Failed to get model: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam) {
        log.debug("Getting models for tenant: {} with params: {}", tenantId, queryParam);

        try {
            List<ModelEntity> modelEntities = modelMapper.selectList(tenantId, queryParam);

            return modelEntities.stream()
                    .map(this::convertToModelInfo)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to get models for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to get models: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ModelProviderInfo> getProviders(String tenantId, Map<String, Object> queryParam) {
        return modelProviderMapper.selectList("29d181ca-9562-4cc2-a4f3-be605a728143", queryParam)
                .stream()
                .map(this::convertToModelProviderInfo)
                .toList();
    }

    private ModelProviderInfo convertToModelProviderInfo(ModelProviderEntity modelProviderEntity) {
        ModelProviderInfo modelProviderInfo = new DefaultModelProvider();

        // 设置基本信息
        modelProviderInfo.setId(modelProviderEntity.getId());
        modelProviderInfo.setTenantId(modelProviderEntity.getTenantId());
        modelProviderInfo.setCode(modelProviderEntity.getProviderCode());
        modelProviderInfo.setName(modelProviderEntity.getName());
        modelProviderInfo.setDescription(modelProviderEntity.getDescription());
        modelProviderInfo.setIcon(modelProviderEntity.getIcon());
        modelProviderInfo.setSortOrder(modelProviderEntity.getSortOrder());
        modelProviderInfo.setEnabled(modelProviderEntity.getEnabled());

        // 解析支持的模型类型
        if (modelProviderEntity.getSupportedModelTypes() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> supportedTypes = objectMapper.readValue(
                        modelProviderEntity.getSupportedModelTypes(), new TypeReference<List<String>>() {
                        });
                modelProviderInfo.setSupportedModelTypes(supportedTypes);
            } catch (Exception e) {
                log.warn("Failed to parse supported model types for provider: {}",
                        modelProviderEntity.getProviderCode(), e);
            }
        }

        // 解析配置Schema
        if (modelProviderEntity.getCredentialSchema() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<ModelConfigItem> configSchemas = objectMapper.readValue(
                        modelProviderEntity.getCredentialSchema(), new TypeReference<List<ModelConfigItem>>() {
                        });
                modelProviderInfo.setConfigSchemas(configSchemas);
            } catch (Exception e) {
                log.warn("Failed to parse credential schema for provider: {}",
                        modelProviderEntity.getProviderCode(), e);
            }
        }

        // 查询该提供商下的模型数量
        try {
            List<ModelEntity> models = modelMapper.selectByTenantAndProviderCode(
                    modelProviderEntity.getTenantId(), modelProviderEntity.getProviderCode());
            modelProviderInfo.setModelCount(models != null ? models.size() : 0);
        } catch (Exception e) {
            log.warn("Failed to count models for provider: {}",
                    modelProviderEntity.getProviderCode(), e);
            modelProviderInfo.setModelCount(0);
        }

        return modelProviderInfo;
    }

    @Override
    public ProviderConfigResponse getProviderConfig(String tenantId, String providerCode) {
        log.debug("Getting provider config for tenant: {} provider: {}", tenantId, providerCode);

        try {
            ModelProviderEntity providerEntity = modelProviderMapper.selectByProviderCode(tenantId, providerCode);
            if (providerEntity == null) {
                log.debug("Provider not found for tenant: {} provider: {}", tenantId, providerCode);
                return null;
            }

            return convertToProviderConfigResponse(providerEntity);

        } catch (Exception e) {
            log.error("Failed to get provider config for tenant: {} provider: {}", tenantId, providerCode, e);
            throw new RuntimeException("Failed to get provider config: " + e.getMessage(), e);
        }
    }

    @Override
    public ModelInfo getModel(String provider, String modelCode) {
        log.debug("Getting model for provider: {} modelCode: {}", provider, modelCode);

        try {
            ModelEntity modelEntity = modelMapper.selectByTenantProviderAndModelCode("29d181ca-9562-4cc2-a4f3-be605a728143", provider, modelCode);
            if (modelEntity == null) {
                log.debug("Model not found for provider: {} modelCode: {}", provider, modelCode);
                return null;
            }

            return convertToModelInfo(modelEntity);

        } catch (Exception e) {
            log.error("Failed to get model for provider: {} modelCode: {}", provider, modelCode, e);
            throw new RuntimeException("Failed to get model: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveProviderConfig(String tenantId, String providerCode, Map<String, Object> config) {
        log.info("Saving provider config for tenant: {} provider: {}", tenantId, providerCode);

        try {
            ModelProviderEntity providerEntity = modelProviderMapper.selectByProviderCode(tenantId, providerCode);
            if (providerEntity == null) {
                log.error("Provider not found for tenant: {} provider: {}", tenantId, providerCode);
                throw new RuntimeException("Provider not found: " + providerCode);
            }

            // 处理配置数据：提取实际的配置值
            Map<String, Object> actualConfig = extractConfigValues(config);

            // 更新特定字段的配置信息
            updateSpecificConfigFields(providerEntity, actualConfig);

            // 更新启用状态
            if (config.containsKey("enabled")) {
                providerEntity.setEnabled((Boolean) config.get("enabled"));
            }

            // 保存完整的配置到customConfig字段
            saveCustomConfig(providerEntity, actualConfig, providerCode);

            providerEntity.setUpdateTime(LocalDateTime.now());
            providerEntity.setUpdatedBy("system");

            modelProviderMapper.update(providerEntity);

            // 8. 配置保存后，直接注册模型到ModelRegistry（API key是必填的）
            registerModelsToRegistry(tenantId, providerEntity);

            log.info("Successfully saved provider config for tenant: {} provider: {}", tenantId, providerCode);

        } catch (Exception e) {
            log.error("Failed to save provider config for tenant: {} provider: {}", tenantId, providerCode, e);
            throw new RuntimeException("Failed to save provider config: " + e.getMessage(), e);
        }
    }

    /**
     * 提取配置值：支持多种输入格式
     * 1. 扁平化格式：{"api_key": "xxx", "endpoint_url": "xxx"}
     * 2. 嵌套格式：{"config": {"api_key": "xxx", "endpoint_url": "xxx"}}
     */
    private Map<String, Object> extractConfigValues(Map<String, Object> config) {
        Map<String, Object> actualConfig = new HashMap<>();

        // 如果包含config字段，则从中提取配置值
        if (config.containsKey("config") && config.get("config") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedConfig = (Map<String, Object>) config.get("config");
            actualConfig.putAll(nestedConfig);
        } else {
            // 否则直接使用顶级字段（排除enabled等控制字段）
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                if (!"enabled".equals(entry.getKey())) {
                    actualConfig.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return actualConfig;
    }

    /**
     * 更新特定字段的配置信息
     */
    private void updateSpecificConfigFields(ModelProviderEntity providerEntity, Map<String, Object> actualConfig) {
       /* // 映射常见的配置字段到实体的特定字段
        if (actualConfig.containsKey("api_key")) {
            providerEntity.setApiKey((String) actualConfig.get("api_key"));
        }
        if (actualConfig.containsKey("endpoint_url")) {
            providerEntity.setBaseUrl((String) actualConfig.get("endpoint_url"));
        }
        if (actualConfig.containsKey("base_url")) {
            providerEntity.setBaseUrl((String) actualConfig.get("base_url"));
        }
        if (actualConfig.containsKey("proxy_url")) {
            providerEntity.setProxyUrl((String) actualConfig.get("proxy_url"));
        }
        
        // 兼容旧的字段名
        if (actualConfig.containsKey("apiKey")) {
            providerEntity.setApiKey((String) actualConfig.get("apiKey"));
        }
        if (actualConfig.containsKey("baseUrl")) {
            providerEntity.setBaseUrl((String) actualConfig.get("baseUrl"));
        }
        if (actualConfig.containsKey("proxyUrl")) {
            providerEntity.setProxyUrl((String) actualConfig.get("proxyUrl"));
        }*/
    }

    /**
     * 保存自定义配置到JSON字段
     */
    private void saveCustomConfig(ModelProviderEntity providerEntity, Map<String, Object> actualConfig, String providerCode) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            providerEntity.setCustomConfig(objectMapper.writeValueAsString(actualConfig));
        } catch (Exception e) {
            log.warn("Failed to serialize custom config for provider: {}", providerCode, e);
        }
    }

    @Override
    @Transactional
    public void updateModelStatus(String tenantId, String provider, String modelCode, boolean enabled) {
        log.info("Updating model status for tenant: {} provider: {} model: {} enabled: {}",
                tenantId, provider, modelCode, enabled);

        try {
            ModelEntity modelEntity = modelMapper.selectByTenantProviderAndModelCode(tenantId, provider, modelCode);
            if (modelEntity == null) {
                log.error("Model not found for tenant: {} provider: {} model: {}", tenantId, provider, modelCode);
                throw new RuntimeException("Model not found: " + modelCode);
            }

            modelEntity.setEnabled(enabled);
            modelEntity.setUpdateTime(LocalDateTime.now());
            modelEntity.setUpdatedBy("system");

            modelMapper.update(modelEntity);

            log.info("Successfully updated model status for tenant: {} provider: {} model: {} enabled: {}",
                    tenantId, provider, modelCode, enabled);

        } catch (Exception e) {
            log.error("Failed to update model status for tenant: {} provider: {} model: {}",
                    tenantId, provider, modelCode, e);
            throw new RuntimeException("Failed to update model status: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveModelConfig(String tenantId, ModelInfo modelInfo) {
        log.info("Saving model config for tenant: {} model: {}", tenantId, modelInfo.getCode());

        try {
            ModelEntity modelEntity = modelMapper.selectByTenantProviderAndModelCode(
                    tenantId, modelInfo.getProvider(), modelInfo.getCode());

            if (modelEntity != null) {
                // 更新现有模型
                updateModelEntityFromInfo(modelEntity, modelInfo);
                modelMapper.update(modelEntity);
                log.debug("Updated existing model: {}", modelInfo.getCode());
            } else {
                // 创建新模型
                modelEntity = createModelEntityFromInfo(tenantId, modelInfo);
                modelMapper.insert(modelEntity);
                log.debug("Created new model: {}", modelInfo.getCode());
            }

            log.info("Successfully saved model config for tenant: {} model: {}", tenantId, modelInfo.getCode());

        } catch (Exception e) {
            log.error("Failed to save model config for tenant: {} model: {}", tenantId, modelInfo.getCode(), e);
            throw new RuntimeException("Failed to save model config: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveProvider(String pluginId, ModelProviderInfo modelProviderInfo) {
        log.info("Saving provider for UI: {} from plugin: {}", modelProviderInfo.getCode(), pluginId);

        try {
            // 检查提供商是否已存在
            ModelProviderEntity existingProvider = modelProviderMapper.selectByProviderCode("29d181ca-9562-4cc2-a4f3-be605a728143", modelProviderInfo.getCode());

            if (existingProvider != null) {
                log.debug("Provider already exists, updating: {}", modelProviderInfo.getCode());
                updateProviderEntity(existingProvider, modelProviderInfo, pluginId);
                modelProviderMapper.update(existingProvider);
            } else {
                log.debug("Creating new provider: {}", modelProviderInfo.getCode());
                ModelProviderEntity providerEntity = createProviderEntity(modelProviderInfo, pluginId);
                modelProviderMapper.insert(providerEntity);
            }

            log.info("Successfully saved provider: {}", modelProviderInfo.getCode());

        } catch (Exception e) {
            log.error("Failed to save provider for UI: {} from plugin: {}", modelProviderInfo.getCode(), pluginId, e);
            throw new RuntimeException("Failed to save provider: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveModels(String pluginId, List<Object> modelMetadataListObj, String providerCode) {
        @SuppressWarnings("unchecked")
        List<ModelMetadata> modelMetadataList = (List<ModelMetadata>) (List<?>) modelMetadataListObj;
        log.info("Saving {} models for UI from plugin: {}",
                modelMetadataList != null ? modelMetadataList.size() : 0, pluginId);

        if (modelMetadataList == null || modelMetadataList.isEmpty()) {
            log.debug("No models to save for plugin: {}", pluginId);
            return;
        }

        try {
            for (ModelMetadata metadata : modelMetadataList) {
                saveModel(pluginId, metadata, providerCode);
            }

            log.info("Successfully saved {} models for plugin: {}", modelMetadataList.size(), pluginId);

        } catch (Exception e) {
            log.error("Failed to save models for UI from plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to save models: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void removePluginData(String pluginId) {
        log.info("Removing plugin data for plugin: {}", pluginId);

        try {
            // 1. 从ModelRegistry中注销模型
            unregisterModelsFromRegistry(pluginId);

            // 2. 删除模型记录
            List<ModelEntity> models = modelMapper.selectList("29d181ca-9562-4cc2-a4f3-be605a728143", Map.of("plugin_id", pluginId));
            for (ModelEntity model : models) {
                modelMapper.deleteById(model.getId());
                log.debug("Deleted model: {} from plugin: {}", model.getModelCode(), pluginId);
            }

            // 3. 删除提供商记录（如果没有其他插件使用）
            List<ModelProviderEntity> providers = modelProviderMapper.selectList("29d181ca-9562-4cc2-a4f3-be605a728143", Map.of("pluginId", pluginId));
            for (ModelProviderEntity provider : providers) {
                // 检查是否还有其他模型使用这个提供商
                List<ModelEntity> remainingModels = modelMapper.selectByTenantAndProviderCode("29d181ca-9562-4cc2-a4f3-be605a728143", provider.getProviderCode());
                if (remainingModels.isEmpty()) {
                    modelProviderMapper.deleteById(provider.getId());
                    log.debug("Deleted provider: {} from plugin: {}", provider.getProviderCode(), pluginId);
                }
            }

            log.info("Successfully removed plugin data for plugin: {}", pluginId);

        } catch (Exception e) {
            log.error("Failed to remove plugin data for plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to remove plugin data: " + e.getMessage(), e);
        }
    }

    /**
     * 保存单个模型到数据库
     */
    private void saveModel(String pluginId, ModelMetadata metadata, String providerCode) {
        try {
            // 检查模型是否已存在
            ModelEntity existingModel = modelMapper.selectByTenantProviderAndModelCode("29d181ca-9562-4cc2-a4f3-be605a728143", providerCode, metadata.getName());

            if (existingModel != null) {
                log.debug("Model already exists, updating: {}", metadata.getModelId());
                updateModelEntity(existingModel, metadata, pluginId);
                modelMapper.update(existingModel);
            } else {
                log.debug("Creating new model: {}", metadata.getModelId());
                ModelEntity modelEntity = createModelEntity(metadata, providerCode, pluginId);
                modelMapper.insert(modelEntity);
            }

        } catch (Exception e) {
            log.error("Failed to save model: {} from plugin: {}", metadata.getModelId(), pluginId, e);
            throw e;
        }
    }

    /**
     * 创建提供商实体
     */
    private ModelProviderEntity createProviderEntity(ModelProviderInfo modelProviderInfo, String pluginId) {
        ModelProviderEntity entity = new ModelProviderEntity();
        entity.setId(IdUtil.generateId());
        entity.setTenantId("29d181ca-9562-4cc2-a4f3-be605a728143");
        entity.setProviderCode(modelProviderInfo.getCode());
        entity.setPluginId(pluginId); // 设置插件ID
        entity.setEnabled(false); // 默认未启用，等待用户配置
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreatedBy("plugin:" + pluginId);
        entity.setUpdatedBy("plugin:" + pluginId);

        // 从ModelProviderInfo设置元数据
        entity.setName(modelProviderInfo.getName());
        entity.setDescription(modelProviderInfo.getDescription());
        entity.setIcon(modelProviderInfo.getIcon());
        entity.setSortOrder(modelProviderInfo.getSortOrder());

        // 序列化支持的模型类型
        try {
            if (modelProviderInfo.getSupportedModelTypes() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                entity.setSupportedModelTypes(objectMapper.writeValueAsString(modelProviderInfo.getSupportedModelTypes()));
            }
        } catch (Exception e) {
            log.warn("Failed to serialize supported model types for provider: {}", modelProviderInfo.getCode(), e);
        }

        // 序列化配置Schema
        try {
            if (modelProviderInfo.getConfigSchemas() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                entity.setCredentialSchema(objectMapper.writeValueAsString(modelProviderInfo.getConfigSchemas()));
            }
        } catch (Exception e) {
            log.warn("Failed to serialize config schemas for provider: {}", modelProviderInfo.getCode(), e);
        }
        
      /*  // 设置默认的基础URL
        String defaultEndpoint = getDefaultEndpointForProvider(modelProviderInfo.getCode());
        if (defaultEndpoint != null) {
            entity.setBaseUrl(defaultEndpoint);
        }
        */
        return entity;
    }

    /**
     * 更新提供商实体
     */
    private void updateProviderEntity(ModelProviderEntity entity, ModelProviderInfo modelProviderInfo, String pluginId) {
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdatedBy("plugin:" + pluginId);
        entity.setPluginId(pluginId); // 更新插件ID
        
      /*  // 保持用户已配置的信息不变，只更新基础信息
        if (entity.getBaseUrl() == null) {
            String defaultEndpoint = getDefaultEndpointForProvider(modelProviderInfo.getCode());
            if (defaultEndpoint != null) {
                entity.setBaseUrl(defaultEndpoint);
            }
        }*/
    }

    /**
     * 创建模型实体
     */
    private ModelEntity createModelEntity(ModelMetadata metadata, String providerCode, String pluginId) {
        ModelEntity entity = new ModelEntity();
        entity.setId(IdUtil.generateId());
        entity.setTenantId("29d181ca-9562-4cc2-a4f3-be605a728143");
        entity.setModelCode(metadata.getName());
        entity.setProviderCode(providerCode);
        entity.setEnabled(false); // 默认未启用，等待用户配置
        entity.setSortOrder(100); // 默认排序权重
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreatedBy("plugin:" + pluginId);
        entity.setUpdatedBy("plugin:" + pluginId);

        // 设置模型配置JSON
        try {
            Map<String, Object> modelConfig = new HashMap<>();
            modelConfig.put("name", metadata.getName());
            modelConfig.put("description", metadata.getDescription());
            modelConfig.put("type", metadata.getType() != null ? metadata.getType().toString() : null);
            modelConfig.put("capabilities", metadata.getSupportedFeatures());
            modelConfig.put("maxTokens", metadata.getMaxTokens());
            modelConfig.put("icon", null); // ModelMetadata没有icon字段

            ObjectMapper objectMapper = new ObjectMapper();
            entity.setModelConfig(objectMapper.writeValueAsString(modelConfig));
        } catch (Exception e) {
            log.warn("Failed to serialize model config for: {}", metadata.getModelId(), e);
        }

        return entity;
    }

    /**
     * 更新模型实体
     */
    private void updateModelEntity(ModelEntity entity, ModelMetadata metadata, String pluginId) {
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdatedBy("plugin:" + pluginId);
        // 保持现有的排序权重

        // 更新模型配置JSON（保持用户配置不变）
        try {
            Map<String, Object> modelConfig = new HashMap<>();
            if (entity.getModelConfig() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                modelConfig = objectMapper.readValue(entity.getModelConfig(), new TypeReference<Map<String, Object>>() {
                });
            }

            // 更新基础信息
            modelConfig.put("name", metadata.getName());
            modelConfig.put("description", metadata.getDescription());
            modelConfig.put("type", metadata.getType() != null ? metadata.getType().toString() : null);
            modelConfig.put("capabilities", metadata.getSupportedFeatures());
            modelConfig.put("maxTokens", metadata.getMaxTokens());
            modelConfig.put("icon", null); // ModelMetadata没有icon字段

            ObjectMapper objectMapper = new ObjectMapper();
            entity.setModelConfig(objectMapper.writeValueAsString(modelConfig));
        } catch (Exception e) {
            log.warn("Failed to update model config for: {}", metadata.getModelId(), e);
        }
    }

    /**
     * 将ModelEntity转换为ModelInfo
     */
    private ModelInfo convertToModelInfo(ModelEntity modelEntity) {
        DefaultModel modelInfo = new DefaultModel();
        modelInfo.setId(modelEntity.getId());
        modelInfo.setTenantId(modelEntity.getTenantId());
        modelInfo.setCode(modelEntity.getModelCode());
        modelInfo.setProvider(modelEntity.getProviderCode());
        modelInfo.setEnabled(modelEntity.getEnabled());
        modelInfo.setSortOrder(modelEntity.getSortOrder());

        // 解析modelConfig JSON
        if (modelEntity.getModelConfig() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> modelConfig = objectMapper.readValue(
                        modelEntity.getModelConfig(), new TypeReference<Map<String, Object>>() {
                        });

                modelInfo.setName((String) modelConfig.get("name"));
                modelInfo.setDescription((String) modelConfig.get("description"));
                modelInfo.setType((String) modelConfig.get("type"));
                modelInfo.setIcon((String) modelConfig.get("icon"));

                @SuppressWarnings("unchecked")
                List<String> capabilities = (List<String>) modelConfig.get("capabilities");
                modelInfo.setCapabilities(capabilities);

            } catch (Exception e) {
                log.warn("Failed to parse model config for model: {}", modelEntity.getModelCode(), e);
            }
        }

        return modelInfo;
    }

    /**
     * 将ModelProviderEntity转换为ProviderConfigResponse
     */
    private ProviderConfigResponse convertToProviderConfigResponse(ModelProviderEntity providerEntity) {
        ProviderConfigResponse response = new ProviderConfigResponse();
        response.setCode(providerEntity.getProviderCode());
        response.setName(providerEntity.getName());
        response.setDescription(providerEntity.getDescription());
        response.setIcon(providerEntity.getIcon());
        response.setEnabled(providerEntity.getEnabled());
        response.setLastUpdated(providerEntity.getUpdateTime() != null ?
                providerEntity.getUpdateTime().toString() : null);

        // 解析supportedModelTypes
        if (providerEntity.getSupportedModelTypes() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> supportedTypes = objectMapper.readValue(
                        providerEntity.getSupportedModelTypes(), new TypeReference<List<String>>() {
                        });
                response.setSupportedModelTypes(supportedTypes);
            } catch (Exception e) {
                log.warn("Failed to parse supported model types for provider: {}",
                        providerEntity.getProviderCode(), e);
            }
        }

        // 解析credentialSchema并填充实际配置值
        if (providerEntity.getCredentialSchema() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<ModelConfigItem> configItems = objectMapper.readValue(
                        providerEntity.getCredentialSchema(), new TypeReference<List<ModelConfigItem>>() {
                        });

                // 获取已保存的配置值并填充到configItems中
                Map<String, Object> savedConfig = getSavedConfigValues(providerEntity);
                fillConfigItemValues(configItems, savedConfig);

                response.setConfigItems(configItems);
            } catch (Exception e) {
                log.warn("Failed to parse credential schema for provider: {}",
                        providerEntity.getProviderCode(), e);
            }
        }

        return response;
    }

    /**
     * 获取已保存的配置值
     */
    private Map<String, Object> getSavedConfigValues(ModelProviderEntity providerEntity) {
        Map<String, Object> configValues = new HashMap<>();
        
      /*  // 从特定字段获取配置值
        if (providerEntity.getApiKey() != null) {
            configValues.put("api_key", providerEntity.getApiKey());
        }
        if (providerEntity.getBaseUrl() != null) {
            configValues.put("endpoint_url", providerEntity.getBaseUrl());
        }
        if (providerEntity.getProxyUrl() != null) {
            configValues.put("proxy_url", providerEntity.getProxyUrl());
        }*/

        // 从customConfig JSON字段获取其他配置值
        if (providerEntity.getCustomConfig() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> customConfig = objectMapper.readValue(
                        providerEntity.getCustomConfig(), new TypeReference<Map<String, Object>>() {
                        });

                // 合并自定义配置，但优先使用特定字段的值
                for (Map.Entry<String, Object> entry : customConfig.entrySet()) {
                    if (!configValues.containsKey(entry.getKey())) {
                        configValues.put(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to parse custom config for provider: {}",
                        providerEntity.getProviderCode(), e);
            }
        }

        return configValues;
    }

    /**
     * 将保存的配置值填充到配置项中
     */
    private void fillConfigItemValues(List<ModelConfigItem> configItems, Map<String, Object> savedConfig) {
        if (configItems == null || savedConfig == null) {
            return;
        }

        for (ModelConfigItem item : configItems) {
            if (savedConfig.containsKey(item.getName())) {
                item.setValue(savedConfig.get(item.getName()));
            }
        }
    }

    /**
     * 从ModelInfo创建ModelEntity
     */
    private ModelEntity createModelEntityFromInfo(String tenantId, ModelInfo modelInfo) {
        ModelEntity entity = new ModelEntity();
        entity.setId(IdUtil.generateId());
        entity.setTenantId(tenantId);
        entity.setModelCode(modelInfo.getCode());
        entity.setProviderCode(modelInfo.getProvider());
        entity.setEnabled(modelInfo.getEnabled() != null ? modelInfo.getEnabled() : false);
        entity.setSortOrder(modelInfo.getSortOrder() != null ? modelInfo.getSortOrder() : 100);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreatedBy("system");
        entity.setUpdatedBy("system");

        // 设置模型配置JSON
        setModelConfigJson(entity, modelInfo);

        return entity;
    }

    /**
     * 从ModelInfo更新ModelEntity
     */
    private void updateModelEntityFromInfo(ModelEntity entity, ModelInfo modelInfo) {
        entity.setEnabled(modelInfo.getEnabled() != null ? modelInfo.getEnabled() : entity.getEnabled());
        entity.setSortOrder(modelInfo.getSortOrder() != null ? modelInfo.getSortOrder() : entity.getSortOrder());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdatedBy("system");

        // 更新模型配置JSON
        setModelConfigJson(entity, modelInfo);
    }

    /**
     * 设置模型配置JSON
     */
    private void setModelConfigJson(ModelEntity entity, ModelInfo modelInfo) {
        try {
            Map<String, Object> modelConfig = new HashMap<>();

            // 如果已有配置，先读取现有配置
            if (entity.getModelConfig() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                modelConfig = objectMapper.readValue(entity.getModelConfig(),
                        new TypeReference<Map<String, Object>>() {
                        });
            }

            // 更新配置信息
            if (modelInfo.getName() != null) {
                modelConfig.put("name", modelInfo.getName());
            }
            if (modelInfo.getDescription() != null) {
                modelConfig.put("description", modelInfo.getDescription());
            }
            if (modelInfo.getType() != null) {
                modelConfig.put("type", modelInfo.getType());
            }
            if (modelInfo.getIcon() != null) {
                modelConfig.put("icon", modelInfo.getIcon());
            }
            if (modelInfo.getCapabilities() != null) {
                modelConfig.put("capabilities", modelInfo.getCapabilities());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            entity.setModelConfig(objectMapper.writeValueAsString(modelConfig));
        } catch (Exception e) {
            log.warn("Failed to serialize model config for: {}", modelInfo.getCode(), e);
        }
    }


    /**
     * 注册该提供商下的所有模型到ModelRegistry
     */
    private void registerModelsToRegistry(String tenantId, ModelProviderEntity providerEntity) {
        try {
            log.info("Registering models to ModelRegistry for provider: {}", providerEntity.getProviderCode());

            // 1. 获取该提供商下的所有已启用模型
            List<ModelEntity> modelEntities = modelMapper.selectByTenantAndProviderCode(tenantId, providerEntity.getProviderCode());
            if (modelEntities == null || modelEntities.isEmpty()) {
                log.debug("No models found for provider: {}", providerEntity.getProviderCode());
                return;
            }

            // 2. 为每个模型创建完整的ModelMetadata并注册
            for (ModelEntity modelEntity : modelEntities) {
                ModelMetadata metadata = createModelMetadataFromEntity(modelEntity, providerEntity);
                if (metadata != null) {
                    ModelDefinition modelDefinition = new ModelDefinition();
                    modelDefinition.setNamespace(metadata.getProvider());
                    modelDefinition.setId(metadata.getModelId().substring(metadata.getModelId().indexOf(":")+1));
                    modelDefinition.setAuthValue(metadata.getConfig().getApiKey());
                    modelDefinition.setBaseUrl(metadata.getConfig().getEndpoint());
                    modelDefinition.setType(metadata.getType().getCode());
                    modelClient.getConfiguration().registerModel(modelDefinition);
                    log.debug("Registered model to ModelRegistry: {}", metadata.getModelId());
                }
            }

            log.info("Successfully registered {} models for provider: {}",
                    modelEntities.stream().mapToInt(m -> m.getEnabled() ? 1 : 0).sum(),
                    providerEntity.getProviderCode());

        } catch (Exception e) {
            log.error("Failed to register models to ModelRegistry for provider: {}",
                    providerEntity.getProviderCode(), e);
            // 不抛出异常，因为这不应该影响配置保存的成功
            throw new YonchainException("同步注册异常", e);
        }
    }

    /**
     * 从数据库实体创建完整的ModelMetadata对象
     */
    private ModelMetadata createModelMetadataFromEntity(ModelEntity modelEntity, ModelProviderEntity providerEntity) {
        try {
            ModelMetadata metadata = new ModelMetadata();

            // 设置基本信息
            metadata.setName(modelEntity.getModelCode());
            metadata.setProvider(providerEntity.getProviderCode());
            metadata.setModelId(providerEntity.getProviderCode() + ":" + modelEntity.getModelCode());
            metadata.setDisplayName(modelEntity.getModelCode());
            metadata.setAvailable(true);

            // 从modelConfig JSON解析模型信息
            if (modelEntity.getModelConfig() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> modelConfigMap = objectMapper.readValue(
                            modelEntity.getModelConfig(), new TypeReference<Map<String, Object>>() {
                            });

                    metadata.setDescription((String) modelConfigMap.get("description"));

                    // 解析模型类型
                    String typeStr = (String) modelConfigMap.get("type");
                    if (typeStr != null) {
                        try {
                            metadata.setType(ModelType.valueOf(typeStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid model type: {} for model: {}, defaulting to TEXT",
                                    typeStr, modelEntity.getModelCode());
                            metadata.setType(ModelType.CHAT);
                        }
                    } else {
                        metadata.setType(ModelType.CHAT); // 默认类型
                    }

                    // 解析最大token数
                    Object maxTokensObj = modelConfigMap.get("maxTokens");
                    if (maxTokensObj instanceof Number) {
                        metadata.setMaxTokens(((Number) maxTokensObj).intValue());
                    }

                    // 解析支持的功能
                    @SuppressWarnings("unchecked")
                    List<String> capabilities = (List<String>) modelConfigMap.get("capabilities");
                    if (capabilities != null) {
                        capabilities.forEach(metadata::addSupportedFeature);
                    }

                } catch (Exception e) {
                    log.warn("Failed to parse model config for: {}, using defaults",
                            modelEntity.getModelCode(), e);
                    metadata.setType(ModelType.CHAT);
                }
            } else {
                metadata.setType(ModelType.CHAT);
            }

            // 创建运行时配置
            ModelConfig modelConfig = createModelConfigFromProvider(modelEntity, providerEntity);
            metadata.setConfig(modelConfig);

            return metadata;

        } catch (Exception e) {
            log.error("Failed to create ModelMetadata for model: {}", modelEntity.getModelCode(), e);
            throw new YonchainException("注册异常", e);
        }
    }

    /**
     * 从提供商配置创建ModelConfig对象
     */
    private ModelConfig createModelConfigFromProvider(ModelEntity modelEntity, ModelProviderEntity providerEntity) {
        ModelConfig config = new ModelConfig();

        // 设置基本信息
        config.setName(modelEntity.getModelCode());
        config.setProvider(providerEntity.getProviderCode());


        // 从modelConfig JSON解析其他配置
        if (modelEntity.getModelConfig() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> modelConfigMap = objectMapper.readValue(
                        modelEntity.getModelConfig(), new TypeReference<Map<String, Object>>() {
                        });

                // 解析模型类型
                String typeStr = (String) modelConfigMap.get("type");
                if (typeStr != null) {
                    try {
                        config.setType(ModelType.valueOf(typeStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        config.setType(ModelType.CHAT);
                    }
                } else {
                    config.setType(ModelType.CHAT);
                }

                // 解析最大token数
                Object maxTokensObj = modelConfigMap.get("maxTokens");
                if (maxTokensObj instanceof Number) {
                    config.setMaxTokens(((Number) maxTokensObj).intValue());
                }


                objectMapper = new ObjectMapper();
                modelConfigMap = objectMapper.readValue(
                        providerEntity.getCustomConfig(), new TypeReference<Map<String, Object>>() {
                        });
                config.setApiKey((String) modelConfigMap.get("api_key"));
                config.setEndpoint((String) modelConfigMap.get("bash_url"));
            } catch (Exception e) {
                log.warn("Failed to parse model config for: {}, using defaults",
                        modelEntity.getModelCode(), e);
                config.setType(ModelType.CHAT);
            }
        } else {
            config.setType(ModelType.CHAT);
        }

        // 从customConfig JSON解析自定义配置
        if (providerEntity.getCustomConfig() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> customConfig = objectMapper.readValue(
                        providerEntity.getCustomConfig(), new TypeReference<Map<String, Object>>() {
                        });

                // 将自定义配置添加到ModelConfig的properties中
                for (Map.Entry<String, Object> entry : customConfig.entrySet()) {
                    config.setProperty(entry.getKey(), entry.getValue());
                }

            } catch (Exception e) {
                log.warn("Failed to parse custom config for provider: {}",
                        providerEntity.getProviderCode(), e);
            }
        }

        return config;
    }

    @Override
    public void reloadAllConfiguredModels() {
        if (modelClient == null) {
            log.warn("ModelClient not available, skipping model reload");
            return;
        }
        
        log.info("Starting to reload all configured models to ModelRegistry");
        
        try {
            // 1. 获取所有已启用且已配置的提供商
            List<ModelProviderEntity> enabledProviders = getEnabledAndConfiguredProviders();
            
            if (enabledProviders.isEmpty()) {
                log.info("No enabled and configured providers found");
                return;
            }
            
            // 2. 为每个提供商注册模型
            int totalRegistered = 0;
            for (ModelProviderEntity provider : enabledProviders) {
                try {
                    int registeredCount = registerModelsForProvider(provider);
                    totalRegistered += registeredCount;
                    log.debug("Reloaded {} models for provider: {}", registeredCount, provider.getProviderCode());
                } catch (Exception e) {
                    log.error("Failed to reload models for provider: {}", provider.getProviderCode(), e);
                }
            }
            
            log.info("Successfully reloaded {} models from {} providers to ModelRegistry", 
                    totalRegistered, enabledProviders.size());
            
        } catch (Exception e) {
            log.error("Failed to reload configured models", e);
            throw new RuntimeException("Failed to reload configured models: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取所有已启用且已配置的提供商
     */
    private List<ModelProviderEntity> getEnabledAndConfiguredProviders() {
        try {
            Map<String, Object> queryParams = new HashMap<>();
            //queryParams.put("enabled", true);
            
            List<ModelProviderEntity> providers = modelProviderMapper.selectList(
                    "29d181ca-9562-4cc2-a4f3-be605a728143", queryParams);
            
            // 过滤出已配置必要信息的提供商
            return providers.stream()
                    .filter(this::isProviderProperlyConfigured)
                    .toList();
            
        } catch (Exception e) {
            log.error("Failed to get enabled and configured providers", e);
            return List.of();
        }
    }
    
    /**
     * 检查提供商是否已正确配置
     */
    private boolean isProviderProperlyConfigured(ModelProviderEntity provider) {
        try {
            // 检查是否有自定义配置
            if (provider.getCustomConfig() == null || provider.getCustomConfig().trim().isEmpty()) {
                log.debug("Provider {} has no custom config", provider.getProviderCode());
                return false;
            }
            
            // 解析配置，检查是否有API Key
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> config = objectMapper.readValue(
                    provider.getCustomConfig(), new TypeReference<Map<String, Object>>() {});
            
            String apiKey = (String) config.get("api_key");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.debug("Provider {} has no api_key configured", provider.getProviderCode());
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.warn("Failed to check provider configuration: {}", provider.getProviderCode(), e);
            return false;
        }
    }
    
    /**
     * 为指定提供商注册所有模型
     */
    private int registerModelsForProvider(ModelProviderEntity provider) {
        try {
            List<ModelEntity> modelEntities = modelMapper.selectByTenantAndProviderCode(
                    "29d181ca-9562-4cc2-a4f3-be605a728143", provider.getProviderCode());
            
            if (modelEntities == null || modelEntities.isEmpty()) {
                log.debug("No models found for provider: {}", provider.getProviderCode());
                return 0;
            }
            
            int registeredCount = 0;
            for (ModelEntity modelEntity : modelEntities) {
                try {
                    ModelMetadata metadata = createModelMetadataFromEntity(modelEntity, provider);
                    if (metadata != null) {
                        // 检查模型是否已经注册，避免重复注册
                        if (!isModelAlreadyRegistered(metadata.getModelId())) {
                            // 创建ModelDefinition用于注册
                            ModelDefinition modelDefinition = convertToModelDefinition(metadata, provider);
                            modelClient.getConfiguration().registerModel(modelDefinition);
                            registeredCount++;
                            log.debug("Registered model to ModelRegistry: {}", metadata.getModelId());
                        } else {
                            log.debug("Model already registered, skipping: {}", metadata.getModelId());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to register model: {} for provider: {}", 
                            modelEntity.getModelCode(), provider.getProviderCode(), e);
                }
            }
            
            return registeredCount;
            
        } catch (Exception e) {
            log.error("Failed to register models for provider: {}", provider.getProviderCode(), e);
            return 0;
        }
    }
    
    /**
     * 检查模型是否已经注册
     */
    private boolean isModelAlreadyRegistered(String fullModelId) {
        try {
            String[] parts = fullModelId.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            return modelClient.getConfiguration().getModelDefinition(parts[0], parts[1]).isPresent();
            
        } catch (Exception e) {
            log.debug("Error checking if model is registered: {}", fullModelId, e);
            return false;
        }
    }
    
    /**
     * 将ModelMetadata转换为ModelDefinition
     */
    private ModelDefinition convertToModelDefinition(ModelMetadata metadata, ModelProviderEntity provider) {
        ModelDefinition definition = new ModelDefinition();
        
        // 设置基本信息
        String[] idParts = metadata.getModelId().split(":");
        if (idParts.length == 2) {
            definition.setNamespace(idParts[0]);
            definition.setId(idParts[1]);
        } else {
            definition.setNamespace(metadata.getProvider());
            definition.setId(metadata.getName());
        }
        
        // 设置类型
        if (metadata.getType() != null) {
            definition.setType(metadata.getType().name().toLowerCase());
        } else {
            definition.setType("chat");
        }
        
        // 从provider的customConfig中获取认证信息
        if (provider.getCustomConfig() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> config = objectMapper.readValue(
                        provider.getCustomConfig(), new TypeReference<Map<String, Object>>() {});
                
                // 设置API Key
                String apiKey = (String) config.get("api_key");
                if (apiKey != null) {
                    definition.setAuthValue(apiKey);
                }
                
                // 设置Base URL
                String endpointUrl = (String) config.get("endpoint_url");
                if (endpointUrl != null) {
                    definition.setBaseUrl(endpointUrl);
                }
                
                // 设置其他选项
                Map<String, Object> options = new HashMap<>();
                for (Map.Entry<String, Object> entry : config.entrySet()) {
                    if (!"api_key".equals(entry.getKey()) && !"endpoint_url".equals(entry.getKey())) {
                        options.put(entry.getKey(), entry.getValue());
                    }
                }
                if (!options.isEmpty()) {
                    definition.setOptions(options);
                }
                
            } catch (Exception e) {
                log.warn("Failed to parse provider config for model definition: {}", 
                        metadata.getModelId(), e);
            }
        }
        
        return definition;
    }

    /**
     * 从ModelRegistry中注销插件的所有模型
     */
    private void unregisterModelsFromRegistry(String pluginId) {
        try {
            log.info("Unregistering models from ModelRegistry for plugin: {}", pluginId);

            // 1. 获取该插件下的所有模型
            List<ModelEntity> modelEntities = modelMapper.selectList("29d181ca-9562-4cc2-a4f3-be605a728143", Map.of("plugin_id", pluginId));
            if (modelEntities == null || modelEntities.isEmpty()) {
                log.debug("No models found for plugin: {}", pluginId);
                return;
            }

            // 2. 获取该插件下的所有提供商，用于构建modelId
            List<ModelProviderEntity> providerEntities = modelProviderMapper.selectList("29d181ca-9562-4cc2-a4f3-be605a728143", Map.of("pluginId", pluginId));
            Map<String, String> providerCodeMap = new HashMap<>();
            for (ModelProviderEntity provider : providerEntities) {
                providerCodeMap.put(provider.getProviderCode(), provider.getProviderCode());
            }

            // 3. 从ModelRegistry中注销每个模型
            int unregisteredCount = 0;
            for (ModelEntity modelEntity : modelEntities) {
                String providerCode = modelEntity.getProviderCode();
                if (providerCodeMap.containsKey(providerCode)) {
                    String modelId = providerCode + ":" + modelEntity.getModelCode();
                    try {
                        modelClient.getConfiguration().unregisterModel(modelId);
                        log.debug("Unregistered model from ModelRegistry: {}", modelId);
                        unregisteredCount++;
                    } catch (Exception e) {
                        log.warn("Failed to unregister model: {} from ModelRegistry", modelId, e);
                        // 继续处理其他模型，不中断整个流程
                    }
                }
            }

            log.info("Successfully unregistered {} models from ModelRegistry for plugin: {}",
                    unregisteredCount, pluginId);

        } catch (Exception e) {
            log.error("Failed to unregister models from ModelRegistry for plugin: {}", pluginId, e);
            // 不抛出异常，因为这不应该影响数据库删除的成功
        }
    }
}
