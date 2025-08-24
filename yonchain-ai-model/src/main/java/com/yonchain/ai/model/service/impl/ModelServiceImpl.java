package com.yonchain.ai.model.service.impl;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.*;
import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import com.yonchain.ai.model.entity.ModelInstanceConfig;
import com.yonchain.ai.model.mapper.ModelMapper;
import com.yonchain.ai.model.mapper.ModelProviderMapper;
import com.yonchain.ai.model.mapper.ModelInstanceConfigMapper;
import com.yonchain.ai.model.loader.ModelLoader;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
 */
@Slf4j
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelLoader staticConfigLoader;

    @Autowired(required = false)
    private ModelMapper modelMapper;

    @Autowired(required = false)
    private ModelProviderMapper modelProviderMapper;

    @Autowired(required = false)
    private ModelInstanceConfigMapper modelInstanceConfigMapper;

    // ==================== 模型查询方法（静态数据 + 配置状态标签）====================

    @Override
    public ModelInfo getModelById(String id) {
        // 从静态配置获取模型基础信息
        DefaultModel staticConfig = staticConfigLoader.getModelConfig(id);
        if (staticConfig == null) {
            return null;
        }

        return convertToModelInfo(staticConfig);
    }

    @Override
    public List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam) {
        // 只返回静态配置的模型列表，不包含租户动态数据
        Collection<DefaultModel> staticConfigs;

        if (queryParam.containsKey("provider") && StringUtils.hasText((String) queryParam.get("provider"))) {
            staticConfigs = staticConfigLoader.getModelConfigsByProvider((String) queryParam.get("provider"));
        } else {
            staticConfigs = staticConfigLoader.getAllModelConfigs();
        }

        return staticConfigs.stream()
                .map(staticConfig -> {
                    DefaultModel modelInfo = (DefaultModel) convertToModelInfo(staticConfig);
                    // 添加租户配置状态标签
                    boolean enabled = isModelEnabled(tenantId, staticConfig.getCode());
                    modelInfo.setEnabled(enabled);
                    return modelInfo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ModelInfo> pageModels(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        // 获取所有静态模型配置
        Collection<DefaultModel> allStaticModels = staticConfigLoader.getAllModelConfigs();

        // 转换为ModelInfo并添加配置状态标签
        List<ModelInfo> allModels = allStaticModels.stream()
                .map(staticConfig -> {
                    DefaultModel modelInfo = (DefaultModel) convertToModelInfo(staticConfig);
                    // 添加租户配置状态标签
                    boolean enabled = isProviderEnabled(tenantId, staticConfig.getCode());
                    modelInfo.setEnabled(enabled);
                    return modelInfo;
                })
                .collect(Collectors.toList());

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

    // ==================== 提供商查询方法（静态数据 + 配置状态标签）====================

    @Override
    public ModelProvider getProviderById(String providerId) {
        // 从静态配置获取提供商基础信息
        DefaultModelProvider staticConfig = staticConfigLoader.getProviderConfig(providerId);
        if (staticConfig == null) {
            return null;
        }

        return convertToModelProvider(staticConfig);
    }

    @Override
    public List<ModelProvider> getProviders(String tenantId, Map<String, Object> queryParam) {
        Collection<DefaultModelProvider> staticConfigs = staticConfigLoader.getAllProviderConfigs();

        return staticConfigs.stream()
                .map(staticConfig -> {
                    DefaultModelProvider provider = (DefaultModelProvider) convertToModelProvider(staticConfig);
                    // 添加租户配置状态标签
                    boolean enabled = isProviderEnabled(tenantId, staticConfig.getCode());
                    provider.setEnabled(enabled);
                    return provider;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ModelProvider> pageProviders(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        // 获取所有静态提供商配置
        Collection<DefaultModelProvider> allStaticProviders = staticConfigLoader.getAllProviderConfigs();

        // 转换为ModelProvider并添加配置状态标签
        List<ModelProvider> allProviders = allStaticProviders.stream()
                .map(staticConfig -> {
                    DefaultModelProvider provider = (DefaultModelProvider) convertToModelProvider(staticConfig);
                    // 添加租户配置状态标签
                    boolean enabled = isProviderEnabled(tenantId, staticConfig.getCode());
                    provider.setEnabled(enabled);
                    return provider;
                })
                .collect(Collectors.toList());

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

    // ==================== 租户配置管理方法 ====================

    /**
     * 获取租户提供商配置（静态定义与动态数据合并）
     */
    @Override
    public ProviderConfigResponse getProviderConfig(String tenantId, String providerCode) {
        ProviderConfigResponse response = new ProviderConfigResponse();

        // 1. 获取静态配置定义（来自YAML）
        DefaultModelProvider staticConfig = staticConfigLoader.getProviderConfig(providerCode);
        if (staticConfig == null) {
            return response;
        }

        // 设置提供商基本信息
        response.setCode(staticConfig.getCode());
        response.setName(staticConfig.getName());
        response.setDescription(staticConfig.getDescription());
        response.setIcon(staticConfig.getIcon());
        response.setSupportedModelTypes(staticConfig.getSupportedModelTypes());
        response.setConfigItems(staticConfig.getConfigSchemas());

        // 2. 获取租户级配置数据
        Map<String, Object> tenantConfigData = new HashMap<>();
        boolean configured = false;
        boolean enabled = false;
        String lastUpdated = null;

        ModelProviderEntity entity = modelProviderMapper.selectByTenantAndCode(tenantId, providerCode);
        if (entity != null) {
            List<ModelConfigItem> configItems = convertJsonToModelConfigList(staticConfig.getConfigSchemas(), entity.getCustomConfig());
            response.setConfigItems(configItems);
            enabled = entity.getEnabled();
            lastUpdated = entity.getUpdateTime() != null ? entity.getUpdateTime().toString() : null;
        }

        response.setEnabled(enabled);
        response.setLastUpdated(lastUpdated);

        return response;
    }

    /**
     * 保存租户提供商配置
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
            entity.setCustomConfig(convertMapToJson((Map<String, Object>) config.get("config")));
            entity.setUpdateTime(LocalDateTime.now());
            modelProviderMapper.update(entity);
        }
    }

    /**
     * 保存租户模型配置
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
            // 验证模型是否存在于静态配置中
            DefaultModel staticConfig = staticConfigLoader.getModelConfig(modelInfo.getCode());
            if (staticConfig == null) {
                throw new IllegalArgumentException("模型不存在: " + modelInfo.getCode());
            }

            ModelEntity entity = modelMapper.selectByTenantAndModelCode(tenantId, modelInfo.getCode());

            // 构建配置Map
            Map<String, Object> config = new HashMap<>();
            if (modelInfo.getCapabilities() != null) {
                config.put("capabilities", modelInfo.getCapabilities());
            }
           /* if (modelInfo.getEnabled() != null) {
                config.put("enabled", modelInfo.getEnabled());
            }*/

            String configJson = convertMapToJson(config);

            if (entity == null) {
                // 创建新配置
                entity = new ModelEntity();
                entity.setTenantId(tenantId);
                entity.setModelCode(modelInfo.getCode());
                entity.setModelConfig(configJson);
                entity.setEnabled(modelInfo.getEnabled());
                entity.setCreateTime(LocalDateTime.now());
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.insert(entity);
                log.info("创建模型配置成功: tenantId={}, modelCode={}", tenantId, modelInfo.getCode());
            } else {
                // 更新现有配置
                entity.setEnabled(modelInfo.getEnabled());
                entity.setModelConfig(configJson);
                entity.setUpdateTime(LocalDateTime.now());
                modelMapper.update(entity);
                log.info("更新模型配置成功: tenantId={}, modelCode={}", tenantId, modelInfo.getCode());
            }
        } catch (Exception e) {
            log.error("保存模型配置失败: tenantId={}, modelCode={}", tenantId, modelInfo.getCode(), e);
            throw new RuntimeException("保存模型配置失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ModelInfo getModel(String modelCode) {
        return getModelById(modelCode);
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

        if (modelMapper == null) {
            log.warn("数据库配置未启用，返回空配置: tenantId={}, modelCode={}", tenantId, modelCode);
            return null;
        }

        try {
            // 根据租户和模型代码获取模型配置
            ModelEntity modelEntity = modelMapper.selectByTenantAndModelCode(tenantId, modelCode);
            if (modelEntity == null) {
                log.warn("未找到模型配置: tenantId={}, modelCode={}", tenantId, modelCode);
                return null;
            }

            // 获取静态配置作为基础
            DefaultModel staticConfig = staticConfigLoader.getModelConfig(modelCode);
            if (staticConfig == null) {
                log.warn("未找到静态模型配置: modelCode={}", modelCode);
                return null;
            }

            // 转换为 ModelInfo，合并静态和动态配置
            DefaultModel modelInfo = (DefaultModel) convertToModelInfo(staticConfig);
            modelInfo.setEnabled(modelEntity.getEnabled());

            // 设置动态配置
            Map<String, Object> dynamicConfig = convertJsonToMapObject(modelEntity.getModelConfig());
            if (dynamicConfig.containsKey("capabilities")) {
                // modelInfo.setCapabilities((Map<String, Object>) dynamicConfig.get("capabilities"));
            }

            log.debug("成功获取模型配置: {}", modelInfo);
            return modelInfo;
        } catch (Exception e) {
            log.error("获取模型配置失败: tenantId={}, modelCode={}", tenantId, modelCode, e);
            return null;
        }
    }

    // ==================== 提供商管理方法 ====================

    @Override
    public void createProvider(ModelProvider modelProvider) {
        if (modelProviderMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法创建提供商配置");
        }

        ModelProviderEntity entity = new ModelProviderEntity();
        entity.setTenantId(modelProvider.getTenantId());
        entity.setProviderCode(modelProvider.getCode());
        entity.setEnabled(modelProvider.getEnabled());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        modelProviderMapper.insert(entity);
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
            ModelProviderEntity config = modelProviderMapper.selectByTenantAndCode(tenantId, providerCode);
            return config.getEnabled();
        } catch (Exception e) {
            log.warn("查询提供商配置失败: tenantId={}, providerCode={}", tenantId, providerCode, e);
            return false;
        }
    }

    /**
     * 检查租户是否已配置指定模型
     */
    private boolean isModelEnabled(String tenantId, String modelCode) {
        try {
            ModelEntity config = modelMapper.selectByTenantAndModelCode(tenantId, modelCode);
            return config.getEnabled();
        } catch (Exception e) {
            log.warn("查询模型配置失败: tenantId={}, modelCode={}", tenantId, modelCode, e);
            return false;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换静态模型配置为ModelInfo
     */
    private ModelInfo convertToModelInfo(DefaultModel staticConfig) {
        // 由于已经是DefaultModel类型，直接返回即可
        return staticConfig;
    }

    /**
     * 转换静态提供商配置为ModelProvider
     */
    private ModelProvider convertToModelProvider(DefaultModelProvider staticConfig) {
        // 由于已经是DefaultModelProvider类型，直接返回即可
        // 确保模型数量字段已设置（ModelLoader在初始化时已经设置了这个值）
        return staticConfig;
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
                            return model.getName().toLowerCase().contains(lowerKeyword) ||
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
                            return provider.getName().toLowerCase().contains(lowerKeyword) ||
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
     * 将JSON字符串转换为List<ModelConfigItem>
     * 例如：{"apiKey":"","baseUrl":"https://api.deepseek.com","proxyUrl":""}
     * @param json JSON字符串
     * @param configItems 需要填充的配置项列表
     * @return 填充后的配置项列表
     */
    private List<ModelConfigItem> convertJsonToModelConfigList( List<ModelConfigItem> configItems,String json) {
        if (json == null || json.trim().isEmpty()) {
            return configItems;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 解析JSON为Map
            Map<String, Object> configMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            
            // 将JSON数据填充到已有的configItems中
            for (ModelConfigItem item : configItems) {
                if (configMap.containsKey(item.getName())) {
                    item.setValue(configMap.get(item.getName()));
                }
            }
            
            return configItems;
        } catch (Exception e) {
           throw new YonchainException("转换失败",e);
        }
    }
    
    /**
     * 将JSON字符串转换为List<ModelConfigItem>
     * 例如：{"apiKey":"","baseUrl":"https://api.deepseek.com","proxyUrl":""}
     */
    private List<ModelConfigItem> convertJsonToModelConfigList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 解析JSON为Map
            Map<String, Object> configMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            List<ModelConfigItem> configItems = new ArrayList<>();
            
            // 将Map中的每个键值对转换为ModelConfigItem
            for (Map.Entry<String, Object> entry : configMap.entrySet()) {
                ModelConfigItem item = new ModelConfigItem();
                item.setName(entry.getKey());
                item.setValue(entry.getValue());
                // 设置其他必要的属性
                item.setType(getTypeFromValue(entry.getValue()));
                configItems.add(item);
            }
            
            return configItems;
        } catch (Exception e) {
           throw new YonchainException("转换失败",e);
        }
    }
    
    /**
     * 根据值推断类型
     */
    private String getTypeFromValue(Object value) {
        if (value == null) {
            return "string"; // 默认为字符串类型
        } else if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Long) {
                return "integer";
            } else {
                return "number";
            }
        } else if (value instanceof Boolean) {
            return "boolean";
        } else {
            return "string";
        }
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
}