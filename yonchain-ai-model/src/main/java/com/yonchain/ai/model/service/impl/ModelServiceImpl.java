package com.yonchain.ai.model.service.impl;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;
import com.yonchain.ai.api.model.ModelService;
import com.yonchain.ai.model.config.StaticModelConfigLoader;
import com.yonchain.ai.model.dto.ModelInfoDto;
import com.yonchain.ai.model.dto.ModelProviderDto;
import com.yonchain.ai.model.entity.ModelInstanceConfig;
import com.yonchain.ai.model.entity.ModelProviderConfig;
import com.yonchain.ai.model.mapper.ModelInstanceConfigMapper;
import com.yonchain.ai.model.mapper.ModelProviderConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模型服务实现类
 * 整合静态配置和租户数据库配置，提供完整的模型和提供商信息
 */
@Slf4j
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private StaticModelConfigLoader staticConfigLoader;
    
    @Autowired(required = false)
    private ModelProviderConfigMapper providerConfigMapper;
    
    @Autowired(required = false)
    private ModelInstanceConfigMapper modelInstanceConfigMapper;

    @Override
    public ModelInfo getModelById(String id) {
        // 从静态配置获取模型基础信息
        StaticModelConfigLoader.ModelConfig staticConfig = staticConfigLoader.getModelConfig(id);
        if (staticConfig == null) {
            return null;
        }
        
        return convertToModelInfo(staticConfig);
    }

    @Override
    public List<ModelInfo> getModels(String tenantId, String providerName) {
        // 获取租户的模型配置视图
        List<StaticModelConfigLoader.ModelConfigView> modelViews = getTenantModelViews(tenantId, providerName);
        
        return modelViews.stream()
                .map(this::convertToModelInfo)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ModelInfo> pageModels(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        // 获取租户的所有模型配置视图
        List<StaticModelConfigLoader.ModelConfigView> allModels = getTenantModelViews(tenantId, null);
        
        // 应用查询过滤条件
        List<StaticModelConfigLoader.ModelConfigView> filteredModels = applyModelFilters(allModels, queryParam);
        
        // 手动分页
        int total = filteredModels.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<StaticModelConfigLoader.ModelConfigView> pageData = filteredModels.subList(startIndex, endIndex);
        List<ModelInfo> modelInfos = pageData.stream()
                .map(this::convertToModelInfo)
                .collect(Collectors.toList());
        
        Page<ModelInfo> page = new Page<>();
        page.setRecords(modelInfos);
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public String createModel(ModelInfo model) {
        if (modelInstanceConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法创建模型配置");
        }
        
        // 创建租户模型实例配置
        ModelInstanceConfig config = new ModelInstanceConfig();
        config.setId(generateId());
        config.setTenantId(model.getTenantId());
        config.setModelCode(((ModelInfoDto) model).getCode());
        config.setProviderCode(((ModelInfoDto) model).getProvider());
        config.setEnabled(model.getIsValid());
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
        
        ModelInfoDto modelDto = (ModelInfoDto) model;
        ModelInstanceConfig config = modelInstanceConfigMapper.selectByTenantAndCode(
            model.getTenantId(), modelDto.getCode());
        
        if (config != null) {
            config.setEnabled(model.getIsValid());
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

    @Override
    public ModelProvider getProviderById(String providerId) {
        // 从静态配置获取提供商基础信息
        StaticModelConfigLoader.ProviderConfig staticConfig = staticConfigLoader.getProviderConfig(providerId);
        if (staticConfig == null) {
            return null;
        }
        
        return convertToModelProvider(staticConfig);
    }

    @Override
    public Page<ModelProvider> pageProviders(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        // 获取租户的所有提供商配置视图
        List<StaticModelConfigLoader.ProviderConfigView> allProviders = getTenantProviderViews(tenantId);
        
        // 应用查询过滤条件
        List<StaticModelConfigLoader.ProviderConfigView> filteredProviders = applyProviderFilters(allProviders, queryParam);
        
        // 手动分页
        int total = filteredProviders.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<StaticModelConfigLoader.ProviderConfigView> pageData = filteredProviders.subList(startIndex, endIndex);
        List<ModelProvider> providers = pageData.stream()
                .map(this::convertToModelProvider)
                .collect(Collectors.toList());
        
        Page<ModelProvider> page = new Page<>();
        page.setRecords(providers);
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public List<ModelProvider> getProvidersByTenantId(String tenantId) {
        List<StaticModelConfigLoader.ProviderConfigView> providerViews = getTenantProviderViews(tenantId);
        
        return providerViews.stream()
                .map(this::convertToModelProvider)
                .collect(Collectors.toList());
    }

    @Override
    public void createProvider(ModelProvider modelProvider) {
        if (providerConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法创建提供商配置");
        }
        
        // 创建租户提供商配置
        ModelProviderDto providerDto = (ModelProviderDto) modelProvider;
        ModelProviderConfig config = new ModelProviderConfig();
        config.setId(generateId());
        config.setTenantId(modelProvider.getTenantId());
        config.setProviderCode(providerDto.getCode());
        config.setApiKey(providerDto.getApiKey());
        config.setBaseUrl(providerDto.getBaseUrl());
        config.setProxyUrl(providerDto.getProxyUrl());
        config.setEnabled(modelProvider.getIsValid());
        config.setCreatedBy(getCurrentUserId());
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        
        providerConfigMapper.insert(config);
    }

    @Override
    public void updateProvider(ModelProvider modelProvider) {
        if (providerConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法更新提供商配置");
        }
        
        ModelProviderDto providerDto = (ModelProviderDto) modelProvider;
        ModelProviderConfig config = providerConfigMapper.selectByTenantAndCode(
            modelProvider.getTenantId(), providerDto.getCode());
        
        if (config != null) {
            config.setApiKey(providerDto.getApiKey());
            config.setBaseUrl(providerDto.getBaseUrl());
            config.setProxyUrl(providerDto.getProxyUrl());
            config.setEnabled(modelProvider.getIsValid());
            config.setUpdatedBy(getCurrentUserId());
            config.setUpdateTime(LocalDateTime.now());
            
            providerConfigMapper.update(config);
        }
    }

    @Override
    public void deleteProvider(String providerId) {
        if (providerConfigMapper == null) {
            throw new UnsupportedOperationException("数据库配置未启用，无法删除提供商配置");
        }
        
        providerConfigMapper.deleteById(providerId);
    }

    // ==================== 租户配置视图构建方法 ====================

    /**
     * 获取租户可用的提供商配置视图
     */
    private List<StaticModelConfigLoader.ProviderConfigView> getTenantProviderViews(String tenantId) {
        // 获取静态配置
        Collection<StaticModelConfigLoader.ProviderConfig> staticConfigs = 
            staticConfigLoader.getAllProviderConfigs();
        
        // 结合租户配置返回视图
        return staticConfigs.stream()
            .map(staticConfig -> {
                StaticModelConfigLoader.ProviderConfigView view = new StaticModelConfigLoader.ProviderConfigView();
                view.setCode(staticConfig.getCode());
                view.setName(staticConfig.getName());
                view.setDescription(staticConfig.getDescription());
                view.setIcon(staticConfig.getIcon());
                view.setWebsite(staticConfig.getWebsite());
                view.setApiDocUrl(staticConfig.getApiDocUrl());
                view.setSupportedFeatures(staticConfig.getSupportedFeatures());
                
                // 检查租户是否已配置此提供商
                boolean configured = isProviderConfigured(tenantId, staticConfig.getCode());
                view.setConfigured(configured);
                
                if (configured) {
                    boolean enabled = isProviderEnabled(tenantId, staticConfig.getCode());
                    view.setEnabled(enabled);
                } else {
                    // 未配置的提供商默认为禁用状态
                    view.setEnabled(false);
                }
                
                return view;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取租户可用的模型配置视图
     */
    private List<StaticModelConfigLoader.ModelConfigView> getTenantModelViews(String tenantId, String providerCode) {
        Collection<StaticModelConfigLoader.ModelConfig> staticConfigs;
        
        if (providerCode != null) {
            staticConfigs = staticConfigLoader.getModelConfigsByProvider(providerCode);
        } else {
            staticConfigs = staticConfigLoader.getAllModelConfigs();
        }
        
        return staticConfigs.stream()
            .map(staticConfig -> {
                StaticModelConfigLoader.ModelConfigView view = new StaticModelConfigLoader.ModelConfigView();
                view.setCode(staticConfig.getCode());
                view.setName(staticConfig.getName());
                view.setDescription(staticConfig.getDescription());
                view.setProvider(staticConfig.getProvider());
                view.setType(staticConfig.getType());
                view.setVersion(staticConfig.getVersion());
               // view.setCapabilities(staticConfig.getCapabilities());
                view.setParameterSchema(staticConfig.getParameterSchema());
                view.setDefaultParameters(staticConfig.getDefaultParameters());
                view.setLimits(staticConfig.getLimits());
                
                // 获取租户的个性化配置
                Map<String, Object> tenantConfig = getTenantModelConfig(tenantId, staticConfig.getCode());
                if (tenantConfig != null) {
                    view.setConfigured(true);
                    view.setEnabled((Boolean) tenantConfig.getOrDefault("enabled", false));
                    view.setTenantDefaultParams((Map<String, Object>) tenantConfig.get("defaultParams"));
                    view.setSortOrder((Integer) tenantConfig.getOrDefault("sortOrder", 0));
                } else {
                    view.setConfigured(false);
                    // 未配置的模型默认为禁用状态
                    view.setEnabled(false);
                }
                
                return view;
            })
            .collect(Collectors.toList());
    }

    // ==================== 租户配置查询方法 ====================

    /**
     * 检查租户是否已配置指定提供商
     */
    private boolean isProviderConfigured(String tenantId, String providerCode) {
        if (providerConfigMapper == null) {
            return false;
        }
        
        try {
            ModelProviderConfig config = providerConfigMapper.selectByTenantAndCode(tenantId, providerCode);
            return config != null;
        } catch (Exception e) {
            log.warn("查询提供商配置失败: tenantId={}, providerCode={}", tenantId, providerCode, e);
            return false;
        }
    }

    /**
     * 检查租户是否启用指定提供商
     */
    private boolean isProviderEnabled(String tenantId, String providerCode) {
        if (providerConfigMapper == null) {
            return false;
        }
        
        try {
            ModelProviderConfig config = providerConfigMapper.selectByTenantAndCode(tenantId, providerCode);
            return config != null && config.getEnabled();
        } catch (Exception e) {
            log.warn("查询提供商启用状态失败: tenantId={}, providerCode={}", tenantId, providerCode, e);
            return false;
        }
    }

    /**
     * 获取租户的模型配置
     */
    private Map<String, Object> getTenantModelConfig(String tenantId, String modelCode) {
        if (modelInstanceConfigMapper == null) {
            return null;
        }
        
        try {
            ModelInstanceConfig config = modelInstanceConfigMapper.selectByTenantAndCode(tenantId, modelCode);
            if (config == null) {
                return null;
            }
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("enabled", config.getEnabled());
            result.put("sortOrder", config.getSortOrder());
            result.put("defaultParams", config.getDefaultParams());
            
            return result;
        } catch (Exception e) {
            log.warn("查询模型配置失败: tenantId={}, modelCode={}", tenantId, modelCode, e);
            return null;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换静态模型配置为ModelInfo
     */
    private ModelInfo convertToModelInfo(StaticModelConfigLoader.ModelConfig staticConfig) {
        ModelInfoDto modelInfo = new ModelInfoDto();
        modelInfo.setId(staticConfig.getCode());
        modelInfo.setCode(staticConfig.getCode());
        modelInfo.setName(staticConfig.getName());
        modelInfo.setDescription(staticConfig.getDescription());
        modelInfo.setProvider(staticConfig.getProvider());
        modelInfo.setType(staticConfig.getType());
        modelInfo.setVersion(staticConfig.getVersion());
       // modelInfo.setCapabilities(staticConfig.getCapabilities());
        modelInfo.setParameterSchema(staticConfig.getParameterSchema());
        modelInfo.setDefaultParameters(staticConfig.getDefaultParameters());
        modelInfo.setLimits(staticConfig.getLimits());
        
        // 映射到API接口字段
        modelInfo.setModelName(staticConfig.getName());
        modelInfo.setProviderName(staticConfig.getProvider());
        modelInfo.setModelType(staticConfig.getType());
        
        // 静态配置默认为启用状态
        modelInfo.setEnabled(true);
        modelInfo.setIsValid(true);
        modelInfo.setConfigured(true);
        modelInfo.setSortOrder(0);
        modelInfo.setCreatedAt(LocalDateTime.now());
        modelInfo.setUpdatedAt(LocalDateTime.now());
        
        return modelInfo;
    }

    /**
     * 转换模型配置视图为ModelInfo
     */
    private ModelInfo convertToModelInfo(StaticModelConfigLoader.ModelConfigView configView) {
        ModelInfoDto modelInfo = new ModelInfoDto();
        modelInfo.setId(configView.getCode());
        modelInfo.setCode(configView.getCode());
        modelInfo.setName(configView.getName());
        modelInfo.setDescription(configView.getDescription());
        modelInfo.setProvider(configView.getProvider());
        modelInfo.setType(configView.getType());
        modelInfo.setVersion(configView.getVersion());
        //modelInfo.setCapabilities(configView.getCapabilities());
        modelInfo.setParameterSchema(configView.getParameterSchema());
        modelInfo.setDefaultParameters(configView.getDefaultParameters());
        modelInfo.setLimits(configView.getLimits());
        modelInfo.setConfigured(configView.isConfigured());
        modelInfo.setEnabled(configView.isEnabled());
        modelInfo.setSortOrder(configView.getSortOrder());
        modelInfo.setDefaultParams(configView.getTenantDefaultParams());
        
        // 映射到API接口字段
        modelInfo.setModelName(configView.getName());
        modelInfo.setProviderName(configView.getProvider());
        modelInfo.setModelType(configView.getType());
        modelInfo.setIsValid(configView.isEnabled());
        
        return modelInfo;
    }

    /**
     * 转换静态提供商配置为ModelProvider
     */
    private ModelProvider convertToModelProvider(StaticModelConfigLoader.ProviderConfig staticConfig) {
        ModelProviderDto provider = new ModelProviderDto();
        provider.setId(staticConfig.getCode());
        provider.setCode(staticConfig.getCode());
        provider.setName(staticConfig.getName());
        provider.setDescription(staticConfig.getDescription());
        provider.setIcon(staticConfig.getIcon());
        provider.setWebsite(staticConfig.getWebsite());
        provider.setApiDocUrl(staticConfig.getApiDocUrl());
        provider.setSupportedFeatures(staticConfig.getSupportedFeatures());
        provider.setParameterSchema(staticConfig.getParameterSchema());
        
        // 映射到API接口字段
        provider.setProviderName(staticConfig.getName());
        provider.setProviderType(staticConfig.getCode());
        
        // 静态配置默认为启用状态
        provider.setEnabled(true);
        provider.setIsValid(true);
        provider.setConfigured(true);
        provider.setCreatedAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());
        
        return provider;
    }

    /**
     * 转换提供商配置视图为ModelProvider
     */
    private ModelProvider convertToModelProvider(StaticModelConfigLoader.ProviderConfigView configView) {
        ModelProviderDto provider = new ModelProviderDto();
        provider.setId(configView.getCode());
        provider.setCode(configView.getCode());
        provider.setName(configView.getName());
        provider.setDescription(configView.getDescription());
        provider.setIcon(configView.getIcon());
        provider.setWebsite(configView.getWebsite());
        provider.setApiDocUrl(configView.getApiDocUrl());
        provider.setSupportedFeatures(configView.getSupportedFeatures());
        provider.setConfigured(configView.isConfigured());
        provider.setEnabled(configView.isEnabled());
        
        // 映射到API接口字段
        provider.setProviderName(configView.getName());
        provider.setProviderType(configView.getCode());
        provider.setIsValid(configView.isEnabled());
        
        return provider;
    }

    /**
     * 应用模型查询过滤条件
     */
    private List<StaticModelConfigLoader.ModelConfigView> applyModelFilters(List<StaticModelConfigLoader.ModelConfigView> models, Map<String, Object> queryParam) {
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
                    
                    // 按启用状态过滤
                    if (queryParam.containsKey("enabled")) {
                        Boolean enabled = (Boolean) queryParam.get("enabled");
                        if (enabled != null && !enabled.equals(model.isEnabled())) {
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
    private List<StaticModelConfigLoader.ProviderConfigView> applyProviderFilters(List<StaticModelConfigLoader.ProviderConfigView> providers, Map<String, Object> queryParam) {
        if (queryParam == null || queryParam.isEmpty()) {
            return providers;
        }
        
        return providers.stream()
                .filter(provider -> {
                    // 按启用状态过滤
                    if (queryParam.containsKey("enabled")) {
                        Boolean enabled = (Boolean) queryParam.get("enabled");
                        if (enabled != null && !enabled.equals(provider.isEnabled())) {
                            return false;
                        }
                    }
                    
                    // 按配置状态过滤
                    if (queryParam.containsKey("configured")) {
                        Boolean configured = (Boolean) queryParam.get("configured");
                        if (configured != null && !configured.equals(provider.isConfigured())) {
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
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    private String getCurrentUserId() {
        // TODO: 从安全上下文获取当前用户ID
        return "system";
    }
}