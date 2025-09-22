package com.yonchain.ai.tmpl.service;

import com.yonchain.ai.api.model.*;
import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.tmpl.entity.ModelEntity;
import com.yonchain.ai.tmpl.entity.ModelProviderEntity;
import com.yonchain.ai.tmpl.mapper.ModelMapper;
import com.yonchain.ai.tmpl.mapper.ModelProviderMapper;
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
                        modelProviderEntity.getSupportedModelTypes(), new TypeReference<List<String>>() {});
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
                        modelProviderEntity.getCredentialSchema(), new TypeReference<List<ModelConfigItem>>() {});
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
        // 映射常见的配置字段到实体的特定字段
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
        }
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
    public void saveProviderForUI(String pluginId, ModelProviderInfo modelProviderInfo) {
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
    public void saveModelsForUI(String pluginId, List<Object> modelMetadataListObj, String providerCode) {
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
                saveModelForUI(pluginId, metadata, providerCode);
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
            // 删除模型记录
            List<ModelEntity> models = modelMapper.selectList("29d181ca-9562-4cc2-a4f3-be605a728143", Map.of("plugin_id", pluginId));
            for (ModelEntity model : models) {
                modelMapper.deleteById(model.getId());
                log.debug("Deleted model: {} from plugin: {}", model.getModelCode(), pluginId);
            }
            
            // 删除提供商记录（如果没有其他插件使用）
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
    private void saveModelForUI(String pluginId, ModelMetadata metadata, String providerCode) {
        try {
            // 检查模型是否已存在
            ModelEntity existingModel = modelMapper.selectByTenantProviderAndModelCode("29d181ca-9562-4cc2-a4f3-be605a728143", providerCode, metadata.getModelId());
            
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
        
        // 设置默认的基础URL
        String defaultEndpoint = getDefaultEndpointForProvider(modelProviderInfo.getCode());
        if (defaultEndpoint != null) {
            entity.setBaseUrl(defaultEndpoint);
        }
        
        return entity;
    }

    /**
     * 更新提供商实体
     */
    private void updateProviderEntity(ModelProviderEntity entity, ModelProviderInfo modelProviderInfo, String pluginId) {
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdatedBy("plugin:" + pluginId);
        entity.setPluginId(pluginId); // 更新插件ID
        
        // 保持用户已配置的信息不变，只更新基础信息
        if (entity.getBaseUrl() == null) {
            String defaultEndpoint = getDefaultEndpointForProvider(modelProviderInfo.getCode());
            if (defaultEndpoint != null) {
                entity.setBaseUrl(defaultEndpoint);
            }
        }
    }

    /**
     * 创建模型实体
     */
    private ModelEntity createModelEntity(ModelMetadata metadata, String providerCode, String pluginId) {
        ModelEntity entity = new ModelEntity();
        entity.setId(IdUtil.generateId());
        entity.setTenantId("29d181ca-9562-4cc2-a4f3-be605a728143");
        entity.setModelCode(metadata.getModelId());
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
                modelConfig = objectMapper.readValue(entity.getModelConfig(), new TypeReference<Map<String, Object>>() {});
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
                        modelEntity.getModelConfig(), new TypeReference<Map<String, Object>>() {});
                
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
                        providerEntity.getSupportedModelTypes(), new TypeReference<List<String>>() {});
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
                        providerEntity.getCredentialSchema(), new TypeReference<List<ModelConfigItem>>() {});
                
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
        
        // 从特定字段获取配置值
        if (providerEntity.getApiKey() != null) {
            configValues.put("api_key", providerEntity.getApiKey());
        }
        if (providerEntity.getBaseUrl() != null) {
            configValues.put("endpoint_url", providerEntity.getBaseUrl());
        }
        if (providerEntity.getProxyUrl() != null) {
            configValues.put("proxy_url", providerEntity.getProxyUrl());
        }
        
        // 从customConfig JSON字段获取其他配置值
        if (providerEntity.getCustomConfig() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> customConfig = objectMapper.readValue(
                        providerEntity.getCustomConfig(), new TypeReference<Map<String, Object>>() {});
                
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
                        new TypeReference<Map<String, Object>>() {});
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
     * 获取提供商的默认endpoint
     */
    private String getDefaultEndpointForProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "deepseek":
                return "https://api.deepseek.com/v1";
            case "openai":
                return "https://api.openai.com/v1";
            case "anthropic":
                return "https://api.anthropic.com/v1";
            case "grok":
                return "https://api.x.ai/v1";
            default:
                return null;
        }
    }
}
