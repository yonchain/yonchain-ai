package com.yonchain.ai.model.service.impl;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.*;
import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import com.yonchain.ai.model.entity.ModelInstanceConfig;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.mapper.ModelMapper;
import com.yonchain.ai.model.mapper.ModelProviderMapper;
import com.yonchain.ai.model.mapper.ModelInstanceConfigMapper;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.util.IdUtil;
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
    private ModelInstanceConfigMapper modelInstanceConfigMapper;

    @Autowired(required = false)
    private ApplicationEventPublisher eventPublisher;

    // ==================== 模型查询方法（从注册表获取数据）====================

    @Override
    public ModelInfo getModelById(String id) {
        // 从注册表获取模型信息
        ModelRegistry.ModelInfo modelInfo = modelRegistry.getModelInfo(id);
        if (modelInfo == null) {
            return null;
        }

        // 将ModelRegistry.ModelInfo转换为ModelInfo
        DefaultModel model = new DefaultModel();
        model.setId(id);
        // 解析modelId，格式为：modelCode-providerCode
        String[] parts = id.split("-");
        if (parts.length == 2) {
            model.setCode(parts[0]);
            model.setProvider(parts[1]);
        } else {
            model.setCode(id);
        }

        // 设置模型启用状态
        model.setEnabled(modelInfo.getModel().getEnabled());

        return model;
    }

    @Override
    public ModelInfo getModel(String provider, String modelCode) {
        // 从注册表获取模型信息
        String modelId = modelCode + "-" + provider;
        ModelRegistry.ModelInfo modelInfo = modelRegistry.getModelInfo(modelId);
        if (modelInfo == null) {
            return null;
        }

        // 将ModelRegistry.ModelInfo转换为ModelInfo
        DefaultModel model = new DefaultModel();
        model.setId(modelId);
        model.setCode(modelCode);
        model.setProvider(provider);

        // 设置模型启用状态
        model.setEnabled(modelInfo.getModel().getEnabled());

        return model;
    }

    @Override
    public List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam) {
        // 从注册表获取所有模型信息
        Map<String, ModelRegistry.ModelInfo> modelInfos = modelRegistry.getAllModelInfos();

        List<ModelInfo> models = new ArrayList<>();
        for (Map.Entry<String, ModelRegistry.ModelInfo> entry : modelInfos.entrySet()) {
            String modelId = entry.getKey();
            ModelRegistry.ModelInfo info = entry.getValue();

            // 解析modelId，格式为：modelCode-providerCode
            String[] parts = modelId.split("-");
            if (parts.length == 2) {
                String modelCode = parts[0];
                String providerCode = parts[1];

                // 检查是否符合查询条件
                if (queryParam != null && queryParam.containsKey("provider") &&
                        !providerCode.equals(queryParam.get("provider"))) {
                    continue;
                }

                DefaultModel modelInfo = new DefaultModel();
                modelInfo.setId(modelId);
                modelInfo.setCode(modelCode);
                modelInfo.setProvider(providerCode);

                // 添加租户配置状态标签
                boolean enabled = isModelEnabled(tenantId, providerCode, modelCode);
                modelInfo.setEnabled(enabled);

                models.add(modelInfo);
            }
        }

        return models;
    }

    @Override
    public ModelInfo getModelConfig(String tenantId, String modelCode) {
        log.debug("获取租户模型配置: tenantId={}, modelCode={}", tenantId, modelCode);

        if (!StringUtils.hasText(tenantId)) {
            log.error("租户ID不能为空");
            throw new IllegalArgumentException("租户ID不能为空");
        }

        if (!StringUtils.hasText(modelCode)) {
            log.error("模型代码不能为空");
            throw new IllegalArgumentException("模型代码不能为空");
        }

        // 从注册表获取模型信息
        Map<String, ModelRegistry.ModelInfo> modelInfos = modelRegistry.getAllModelInfos();
        for (Map.Entry<String, ModelRegistry.ModelInfo> entry : modelInfos.entrySet()) {
            String modelId = entry.getKey();
            ModelRegistry.ModelInfo info = entry.getValue();

            String[] parts = modelId.split("-");
            if (parts.length == 2 && parts[0].equals(modelCode)) {
                // 创建ModelInfo
                DefaultModel modelInfo = new DefaultModel();
                modelInfo.setId(modelId);
                modelInfo.setCode(modelCode);
                modelInfo.setProvider(parts[1]);
                modelInfo.setEnabled(info.getModel().getEnabled());

                // 如果数据库配置可用，获取租户特定配置
                if (modelMapper != null) {
                    ModelEntity modelEntity = modelMapper.selectByTenantAndModelCode(tenantId, modelCode);
                    if (modelEntity != null) {
                        modelInfo.setEnabled(modelEntity.getEnabled());

                        // 设置动态配置
                        Map<String, Object> dynamicConfig = convertJsonToMapObject(modelEntity.getModelConfig());
                        if (dynamicConfig.containsKey("capabilities")) {
                            // modelInfo.setCapabilities((Map<String, Object>) dynamicConfig.get("capabilities"));
                        }
                    }
                }

                log.debug("成功获取模型配置: {}", modelInfo);
                return modelInfo;
            }
        }

        log.warn("未找到模型配置: tenantId={}, modelCode={}", tenantId, modelCode);
        return null;
    }

    @Override
    public List<ModelProvider> getProviders(String tenantId, Map<String, Object> queryParam) {
        // 从注册表获取所有模型信息
        Map<String, ModelRegistry.ModelInfo> modelInfos = modelRegistry.getAllModelInfos();

        // 提取所有提供商信息
        Map<String, ModelProviderEntity> providerMap = new HashMap<>();
        for (Map.Entry<String, ModelRegistry.ModelInfo> entry : modelInfos.entrySet()) {
            String modelId = entry.getKey();
            ModelRegistry.ModelInfo info = entry.getValue();

            String[] parts = modelId.split("-");
            if (parts.length == 2) {
                String providerCode = parts[1];
                providerMap.putIfAbsent(providerCode, info.getProvider());
            }
        }

        // 转换为ModelProvider列表
        List<ModelProvider> providers = new ArrayList<>();
        for (Map.Entry<String, ModelProviderEntity> entry : providerMap.entrySet()) {
            String providerCode = entry.getKey();
            ModelProviderEntity providerEntity = entry.getValue();

            DefaultModelProvider provider = new DefaultModelProvider();
            provider.setId(providerCode);
            provider.setCode(providerCode);

            // 添加租户配置状态标签
            boolean enabled = isProviderEnabled(tenantId, providerCode);
            provider.setEnabled(enabled);

            providers.add(provider);
        }

        return providers;
    }

    @Override
    public Page<ModelInfo> pageModels(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        // 获取所有模型
        List<ModelInfo> allModels = getModels(tenantId, queryParam);

        // 应用查询过滤条件
        List<ModelInfo> filteredModels = applyModelInfoFilters(allModels, queryParam);

        // 手动分页
        int total = filteredModels.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        List<ModelInfo> pageData = filteredModels.subList(startIndex, endIndex);

        Page<ModelInfo> page = new Page<>();
        page.setRecords(pageData);
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page.setTotal(total);

        return page;
    }

    @Override
    public Page<ModelProvider> pageProviders(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        // 获取所有提供商
        List<ModelProvider> allProviders = getProviders(tenantId, queryParam);

        // 应用查询过滤条件
        List<ModelProvider> filteredProviders = applyModelProviderFilters(allProviders, queryParam);

        // 手动分页
        int total = filteredProviders.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        List<ModelProvider> pageData = filteredProviders.subList(startIndex, endIndex);

        Page<ModelProvider> page = new Page<>();
        page.setRecords(pageData);
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page.setTotal(total);

        return page;
    }

    @Override
    public List<ModelProvider> getProvidersByTenantId(String tenantId) {
        // 委托给getProviders方法，保持向后兼容
        return getProviders(tenantId, null);
    }

    @Override
    public ModelProvider getProviderById(String providerId) {
        // 从注册表获取所有模型信息
        Map<String, ModelRegistry.ModelInfo> modelInfos = modelRegistry.getAllModelInfos();

        // 查找包含指定提供商的模型信息
        for (Map.Entry<String, ModelRegistry.ModelInfo> entry : modelInfos.entrySet()) {
            String modelId = entry.getKey();
            ModelRegistry.ModelInfo info = entry.getValue();

            String[] parts = modelId.split("-");
            if (parts.length == 2) {
                String providerCode = parts[1];
                if (providerCode.equals(providerId)) {
                    DefaultModelProvider provider = new DefaultModelProvider();
                    provider.setId(providerId);
                    provider.setCode(providerId);
                    provider.setEnabled(info.getProvider().getEnabled());
                    return provider;
                }
            }
        }

        return null;
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
        ModelProviderEntity providerEntity = null;
        Map<String, ModelRegistry.ModelInfo> modelInfos = modelRegistry.getAllModelInfos();
        for (ModelRegistry.ModelInfo info : modelInfos.values()) {
            if (info.getProvider().getProviderCode().equals(providerCode)) {
                providerEntity = info.getProvider();
                break;
            }
        }

        if (providerEntity == null) {
            return response;
        }

        // 获取租户级配置数据
        boolean enabled = providerEntity.getEnabled() != null ? providerEntity.getEnabled() : false;
        String lastUpdated = null;

        // 如果数据库配置可用，获取租户特定配置
        if (modelProviderMapper != null) {
            ModelProviderEntity entity = modelProviderMapper.selectByTenantAndCode(tenantId, providerCode);
            if (entity != null) {
                enabled = entity.getEnabled();
                lastUpdated = entity.getUpdateTime() != null ? entity.getUpdateTime().toString() : null;
            }
        }

        response.setEnabled(enabled);
        response.setLastUpdated(lastUpdated);

        return response;
    }

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

            // 同步到注册表
            syncProviderToRegistry(entity);

            // 发布提供商配置变更事件
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new ProviderConfigChangeEvent(tenantId, providerCode, entity));
            }
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
            ModelRegistry.ModelInfo registryInfo = modelRegistry.getModelInfo(modelId);
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
                syncModelToRegistry(entity);
            } else {
                // 更新现有配置
                entity.setEnabled(modelInfo.getEnabled());
                entity.setModelConfig(configJson);
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.update(entity);
                log.info("更新模型配置成功: tenantId={}, modelCode={}", tenantId, modelInfo.getCode());

                // 同步到注册表
                syncModelToRegistry(entity);

                // 发布模型配置变更事件
                if (eventPublisher != null) {
                    eventPublisher.publishEvent(new ModelConfigChangeEvent(tenantId, modelInfo.getCode(), entity));
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

                // 同步到注册表
                syncModelToRegistry(entity);

                // 发布模型配置变更事件
                if (eventPublisher != null) {
                    eventPublisher.publishEvent(new ModelConfigChangeEvent(tenantId, modelCode, entity));
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

                // 同步到注册表
                syncModelToRegistry(entity);
            }
        } catch (Exception e) {
            log.error("更新模型状态失败: tenantId={}, provider={}, modelCode={}", tenantId, provider, modelCode, e);
            throw new RuntimeException("更新模型状态失败: " + e.getMessage(), e);
        }
    }

    // ==================== 模型实例管理方法 ====================

    @Override
    public String createModel(ModelInfo model) {
        if (modelInstanceConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法创建模型配置");
        }

        // 创建租户模型实例配置
        ModelInstanceConfig config = new ModelInstanceConfig();
        config.setId(generateId());
        config.setTenantId(model.getTenantId());
        config.setModelCode(model.getCode());
        config.setProviderCode(model.getProvider());
        config.setEnabled(true);
        config.setCreatedBy(getCurrentUserId());
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());

        modelInstanceConfigMapper.insert(config);

        // 同步到注册表
        syncModelInstanceToRegistry(config);

        return config.getId();
    }

    @Override
    public void updateModel(ModelInfo model) {
        if (modelInstanceConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法更新模型配置");
        }

        ModelInstanceConfig config = modelInstanceConfigMapper.selectByTenantAndCode(
                model.getTenantId(), model.getCode());

        if (config != null) {
            config.setEnabled(true);
            config.setUpdatedBy(getCurrentUserId());
            config.setUpdateTime(LocalDateTime.now());

            modelInstanceConfigMapper.update(config);

            // 同步到注册表
            syncModelInstanceToRegistry(config);
        }
    }

    @Override
    public void deleteModel(String id) {
        if (modelInstanceConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法删除模型配置");
        }

        modelInstanceConfigMapper.deleteById(id);
    }

    // ==================== 提供商管理方法 ====================

    @Override
    public void createProvider(ModelProvider modelProvider) {
        if (modelProviderMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法创建提供商配置");
        }

        ModelProviderEntity entity = new ModelProviderEntity();
        entity.setId(IdUtil.generateId());
        entity.setTenantId(modelProvider.getTenantId());
        entity.setProviderCode(modelProvider.getCode());
        entity.setEnabled(modelProvider.getEnabled());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        modelProviderMapper.insert(entity);

        // 同步到注册表
        syncProviderToRegistry(entity);
    }

    @Override
    public void updateProvider(ModelProvider modelProvider) {
        if (modelProviderMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法更新提供商配置");
        }

        ModelProviderEntity entity = modelProviderMapper.selectByTenantAndCode(
                modelProvider.getTenantId(), modelProvider.getCode());

        if (entity != null) {
            entity.setEnabled(modelProvider.getEnabled());
            entity.setUpdateTime(LocalDateTime.now());
            modelProviderMapper.update(entity);

            // 同步到注册表
            syncProviderToRegistry(entity);
        }
    }

    @Override
    public void deleteProvider(String providerId) {
        if (modelProviderMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法删除提供商配置");
        }

        modelProviderMapper.deleteById(providerId);
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
    private void syncModelToRegistry(ModelEntity entity) {
        try {
            String modelId = entity.getModelCode() + "-" + entity.getProviderCode();
            ModelRegistry.ModelInfo modelInfo = modelRegistry.getModelInfo(modelId);

            if (modelInfo != null) {
                // 更新模型实体
                ModelEntity modelEntity = modelInfo.getModel();
                modelEntity.setEnabled(entity.getEnabled());
                modelEntity.setModelConfig(entity.getModelConfig());

                // 重新注册到注册表
                modelRegistry.registerModelInfo(modelId, modelEntity, modelInfo.getProvider());

                // 清除模型工厂缓存，确保下次获取时使用最新配置
                modelFactory.invalidateModel(modelId);

                log.info("已同步模型配置到注册表: {}", modelId);
            }
        } catch (Exception e) {
            log.error("同步模型配置到注册表失败", e);
        }
    }

    /**
     * 同步提供商配置到注册表
     */
    private void syncProviderToRegistry(ModelProviderEntity entity) {
        try {
            // 查找所有使用该提供商的模型
            Map<String, ModelRegistry.ModelInfo> modelInfos = modelRegistry.getAllModelInfos();
            for (Map.Entry<String, ModelRegistry.ModelInfo> entry : modelInfos.entrySet()) {
                String modelId = entry.getKey();
                ModelRegistry.ModelInfo info = entry.getValue();

                if (info.getProvider().getProviderCode().equals(entity.getProviderCode())) {
                    // 更新提供商实体
                    ModelProviderEntity providerEntity = info.getProvider();
                    providerEntity.setEnabled(entity.getEnabled());

                    // 如果有API Key或Base URL更新，同步这些值
                    if (entity.getApiKey() != null) {
                        providerEntity.setApiKey(entity.getApiKey());
                    }
                    if (entity.getBaseUrl() != null) {
                        providerEntity.setBaseUrl(entity.getBaseUrl());
                    }
                    if (entity.getCustomConfig() != null) {
                        providerEntity.setCustomConfig(entity.getCustomConfig());
                    }

                    // 重新注册到注册表
                    modelRegistry.registerModelInfo(modelId, info.getModel(), providerEntity);

                    // 清除模型工厂缓存，确保下次获取时使用最新配置
                    modelFactory.invalidateModel(modelId);
                }
            }

            log.info("已同步提供商配置到注册表: {}", entity.getProviderCode());
        } catch (Exception e) {
            log.error("同步提供商配置到注册表失败", e);
        }
    }

    /**
     * 同步模型实例配置到注册表
     */
    private void syncModelInstanceToRegistry(ModelInstanceConfig config) {
        try {
            String modelId = config.getModelCode() + "-" + config.getProviderCode();
            ModelRegistry.ModelInfo modelInfo = modelRegistry.getModelInfo(modelId);

            if (modelInfo != null) {
                // 更新模型实体
                ModelEntity modelEntity = modelInfo.getModel();
                modelEntity.setEnabled(config.getEnabled());

                // 重新注册到注册表
                modelRegistry.registerModelInfo(modelId, modelEntity, modelInfo.getProvider());

                // 清除模型工厂缓存，确保下次获取时使用最新配置
                modelFactory.invalidateModel(modelId);

                log.info("已同步模型实例配置到注册表: {}", modelId);
            }
        } catch (Exception e) {
            log.error("同步模型实例配置到注册表失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================

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
     * 应用提供商查询过滤条件
     */
    private List<ModelProvider> applyModelProviderFilters(List<ModelProvider> providers, Map<String, Object> queryParam) {
        if (queryParam == null || queryParam.isEmpty()) {
            return providers;
        }

        return providers.stream()
                .filter(provider -> {
                    // 按配置状态过滤
                    if (queryParam.containsKey("configured")) {
                        Boolean configured = (Boolean) queryParam.get("configured");
                        if (configured != null && !configured.equals(provider.getEnabled())) {
                            return false;
                        }
                    }

                    // 按名称模糊搜索
                    if (queryParam.containsKey("keyword")) {
                        String keyword = (String) queryParam.get("keyword");
                        if (StringUtils.hasText(keyword)) {
                            String lowerKeyword = keyword.toLowerCase();
                            return provider.getName() != null && provider.getName().toLowerCase().contains(lowerKeyword) ||
                                    (provider.getDescription() != null && provider.getDescription().toLowerCase().contains(lowerKeyword));
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    // ==================== 工具方法 ====================

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String getCurrentUserId() {
        // TODO: 从安全上下文获取当前用户ID
        return "system";
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
        private ModelProviderEntity entity;

        public ProviderConfigChangeEvent(String tenantId, String providerCode, ModelProviderEntity entity) {
            this.tenantId = tenantId;
            this.providerCode = providerCode;
            this.entity = entity;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getProviderCode() {
            return providerCode;
        }

        public ModelProviderEntity getEntity() {
            return entity;
        }
    }

    /**
     * 内部类：模型配置变更事件
     */
    public static class ModelConfigChangeEvent {
        private String tenantId;
        private String modelCode;
        private ModelEntity entity;

        public ModelConfigChangeEvent(String tenantId, String modelCode, ModelEntity entity) {
            this.tenantId = tenantId;
            this.modelCode = modelCode;
            this.entity = entity;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getModelCode() {
            return modelCode;
        }

        public ModelEntity getEntity() {
            return entity;
        }
    }

}
