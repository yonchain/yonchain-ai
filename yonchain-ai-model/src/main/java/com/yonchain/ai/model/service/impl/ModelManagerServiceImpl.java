package com.yonchain.ai.model.service.impl;

import com.yonchain.ai.model.config.ModelProviderConfigLoader;
import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.mapper.AIModelMapper;
import com.yonchain.ai.model.mapper.ModelProviderMapper;
import com.yonchain.ai.model.service.ModelManagerService;
import com.yonchain.ai.model.spi.ModelProviderService;
import com.yonchain.ai.model.vo.ModelCapability;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模型管理服务实现类
 */
@Slf4j
@Service
public class ModelManagerServiceImpl implements ModelManagerService {

    @Autowired
    private ModelProviderMapper providerMapper;

    @Autowired
    private AIModelMapper modelMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ModelProviderConfigLoader configLoader;

    /**
     * 缓存已注册的模型提供商服务
     */
    private final Map<String, ModelProviderService> providerServiceMap = new ConcurrentHashMap<>();

    /**
     * 缓存从配置文件加载的提供商数据
     */
    private List<ModelProvider> configProviders;

    /**
     * 初始化方法，在服务启动时加载所有已注册的模型提供商
     */
    public void init() {
        // 获取所有实现了ModelProviderService接口的Bean
        Map<String, ModelProviderService> serviceMap = applicationContext.getBeansOfType(ModelProviderService.class);

        // 加载数据库中的所有提供商
        List<ModelProvider> providers = providerMapper.selectByIds(null);
        Map<String, ModelProvider> providerMap = providers.stream()
                .collect(Collectors.toMap(ModelProvider::getCode, p -> p));

        // 注册系统内置的提供商服务
        for (ModelProviderService service : serviceMap.values()) {
            ModelProvider providerInfo = service.getProviderInfo();
            String providerCode = providerInfo.getCode();

            // 检查数据库中是否已存在该提供商
            ModelProvider existingProvider = providerMap.get(providerCode);
            if (existingProvider == null) {
                // 不存在则添加到数据库
                //   providerInfo.setType(ProviderType.SYSTEM.getCode());
                providerInfo.setEnabled(true);
                providerInfo.setCreateTime(LocalDateTime.now());
                providerInfo.setUpdateTime(LocalDateTime.now());
                providerMapper.insert(providerInfo);

                // 添加该提供商的所有模型
                List<AIModel> models = service.listModels();
                for (AIModel model : models) {
                    model.setProviderId(providerInfo.getId());
                    model.setProviderCode(providerCode);
                    model.setIsSystem(true);
                    model.setEnabled(true);
                    model.setCreateTime(LocalDateTime.now());
                    model.setUpdateTime(LocalDateTime.now());
                    modelMapper.insert(model);
                }
            } else {
                // 存在则更新提供商信息
                existingProvider.setName(providerInfo.getName());
                existingProvider.setDescription(providerInfo.getDescription());
                existingProvider.setIconUrl(providerInfo.getIconUrl());
                existingProvider.setWebsiteUrl(providerInfo.getWebsiteUrl());
                existingProvider.setSupportedModelTypes(providerInfo.getSupportedModelTypes());
                existingProvider.setConfigSchema(providerInfo.getConfigSchema());
                existingProvider.setUpdateTime(LocalDateTime.now());
                providerMapper.updateById(existingProvider);

                // 更新该提供商的所有模型
                List<AIModel> existingModels = modelMapper.selectByProviderId(existingProvider.getId());
                Map<String, AIModel> existingModelMap = existingModels.stream()
                        .collect(Collectors.toMap(AIModel::getCode, m -> m));

                List<AIModel> models = service.listModels();
                for (AIModel model : models) {
                    AIModel existingModel = existingModelMap.get(model.getCode());
                    if (existingModel == null) {
                        // 不存在则添加
                        model.setProviderId(existingProvider.getId());
                        model.setProviderCode(providerCode);
                        model.setIsSystem(true);
                        model.setEnabled(true);
                        model.setCreateTime(LocalDateTime.now());
                        model.setUpdateTime(LocalDateTime.now());
                        modelMapper.insert(model);
                    } else {
                        // 存在则更新
                        existingModel.setName(model.getName());
                        existingModel.setDescription(model.getDescription());
                        existingModel.setIconUrl(model.getIconUrl());
                        existingModel.setModelType(model.getModelType());
                        existingModel.setVersion(model.getVersion());
                        existingModel.setConfigSchema(model.getConfigSchema());
                        existingModel.setCapabilities(model.getCapabilities());
                        existingModel.setUpdateTime(LocalDateTime.now());
                        modelMapper.updateById(existingModel);
                    }
                }
            }

            // 初始化提供商服务
            if (existingProvider != null && existingProvider.getConfig() != null) {
                service.initialize(existingProvider.getConfig());
            }

            // 注册提供商服务
            providerServiceMap.put(providerCode, service);
        }

        // 加载自定义提供商
        for (ModelProvider provider : providers) {
            /*if (ProviderType.CUSTOM.getCode().equals(provider.getType()) && provider.getEnabled()) {
                // TODO: 加载自定义提供商的实现
                // 这里需要根据实际情况实现自定义提供商的加载逻辑
            }*/
        }
    }

    /**
     * 获取配置文件中的提供商数据（懒加载）
     */
    private List<ModelProvider> getConfigProviders() {
        if (configProviders == null) {
            configProviders = configLoader.loadAllProviders();
        }
        return configProviders;
    }

    @Override
    public List<ModelProvider> listProviders() {
        // 从配置文件获取数据，而不是数据库
        return getConfigProviders();
    }

    @Override
    public ModelProvider getProvider(String providerCode) {
        // 从配置文件获取数据，而不是数据库
        return getConfigProviders().stream()
                .filter(provider -> providerCode.equals(provider.getCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<AIModel> listModelsByProvider(String providerCode) {
        // 从配置文件获取数据，而不是数据库
        ModelProvider provider = getProvider(providerCode);
        if (provider == null) {
            return Collections.emptyList();
        }
        return provider.getModels();
    }

    @Override
    public List<AIModel> listAllModels() {
        // 从配置文件获取所有提供商的所有模型
        return getConfigProviders().stream()
                .flatMap(provider -> provider.getModels().stream())
                .collect(Collectors.toList());
    }

    @Override
    public AIModel getModel(String modelCode) {
        // 从配置文件获取数据，而不是数据库
        return listAllModels().stream()
                .filter(model -> modelCode.equals(model.getCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public ModelProvider addProvider(ModelProvider provider, Map<String, Object> config) {
        // 检查提供商代码是否已存在
        ModelProvider existingProvider = providerMapper.selectByCode(provider.getCode());
        if (existingProvider != null) {
            throw new IllegalArgumentException("提供商代码已存在: " + provider.getCode());
        }

        // 设置提供商类型为自定义
        //  provider.setType(ProviderType.CUSTOM.getCode());
        provider.setConfig(config);
        provider.setCreateTime(LocalDateTime.now());
        provider.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        providerMapper.insert(provider);

        // TODO: 注册自定义提供商服务
        // 这里需要根据实际情况实现自定义提供商的注册逻辑

        return provider;
    }

    @Override
    @Transactional
    public ModelProvider updateProvider(ModelProvider provider, Map<String, Object> config) {
        // 检查提供商是否存在
        ModelProvider existingProvider = providerMapper.selectByCode(provider.getCode());
        if (existingProvider == null) {
            throw new IllegalArgumentException("提供商不存在: " + provider.getCode());
        }

        // 检查是否是系统内置提供商
      /*  if (ProviderType.SYSTEM.getCode().equals(existingProvider.getType())) {
            throw new IllegalArgumentException("系统内置提供商不能修改");
        }*/

        // 更新提供商信息
        existingProvider.setName(provider.getName());
        existingProvider.setDescription(provider.getDescription());
        existingProvider.setIconUrl(provider.getIconUrl());
        existingProvider.setWebsiteUrl(provider.getWebsiteUrl());
        existingProvider.setSupportedModelTypes(provider.getSupportedModelTypes());
        existingProvider.setConfig(config);
        existingProvider.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        providerMapper.updateById(existingProvider);

        // 更新提供商服务
        ModelProviderService providerService = providerServiceMap.get(provider.getCode());
        if (providerService != null) {
            providerService.initialize(config);
        }

        return existingProvider;
    }

    @Override
    @Transactional
    public boolean deleteProvider(String providerCode) {
        // 检查提供商是否存在
        ModelProvider existingProvider = providerMapper.selectByCode(providerCode);
        if (existingProvider == null) {
            return false;
        }
        
       /* // 检查是否是系统内置提供商
        if (ProviderType.SYSTEM.getCode().equals(existingProvider.getType())) {
            throw new IllegalArgumentException("系统内置提供商不能删除");
        }*/

        // 删除该提供商的所有模型
        modelMapper.deleteByProviderId(existingProvider.getId());

        // 删除提供商
        providerMapper.deleteById(existingProvider.getId());

        // 移除提供商服务
        providerServiceMap.remove(providerCode);

        return true;
    }

    @Override
    @Transactional
    public AIModel addModel(AIModel model, Map<String, Object> config) {
        // 检查模型代码是否已存在
        AIModel existingModel = modelMapper.selectByCode(model.getCode());
        if (existingModel != null) {
            throw new IllegalArgumentException("模型代码已存在: " + model.getCode());
        }

        // 检查提供商是否存在
        ModelProvider provider = providerMapper.selectByCode(model.getProviderCode());
        if (provider == null) {
            throw new IllegalArgumentException("提供商不存在: " + model.getProviderCode());
        }

        // 设置模型信息
        model.setProviderId(provider.getId());
        model.setIsSystem(false);
        model.setConfig(config);
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        modelMapper.insert(model);

        return model;
    }

    @Override
    @Transactional
    public AIModel updateModel(AIModel model, Map<String, Object> config) {
        // 检查模型是否存在
        AIModel existingModel = modelMapper.selectByCode(model.getCode());
        if (existingModel == null) {
            throw new IllegalArgumentException("模型不存在: " + model.getCode());
        }

        // 检查是否是系统内置模型
        if (existingModel.getIsSystem()) {
            throw new IllegalArgumentException("系统内置模型不能修改");
        }

        // 更新模型信息
        existingModel.setName(model.getName());
        existingModel.setDescription(model.getDescription());
        existingModel.setIconUrl(model.getIconUrl());
        existingModel.setModelType(model.getModelType());
        existingModel.setVersion(model.getVersion());
        existingModel.setConfig(config);
        existingModel.setCapabilities(model.getCapabilities());
        existingModel.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        modelMapper.updateById(existingModel);

        return existingModel;
    }

    @Override
    @Transactional
    public boolean deleteModel(String modelCode) {
        // 检查模型是否存在
        AIModel existingModel = modelMapper.selectByCode(modelCode);
        if (existingModel == null) {
            return false;
        }

        // 检查是否是系统内置模型
        if (existingModel.getIsSystem()) {
            throw new IllegalArgumentException("系统内置模型不能删除");
        }

        // 删除模型
        modelMapper.deleteById(existingModel.getId());

        return true;
    }

    @Override
    public List<ModelCapability> getModelCapabilities(String modelCode) {
        // 从配置文件获取模型信息
        AIModel model = getModel(modelCode);
        if (model == null) {
            return Collections.emptyList();
        }

        // 获取提供商服务
        ModelProviderService providerService = providerServiceMap.get(model.getProviderCode());
        if (providerService == null) {
            // 如果没有提供商服务，从配置文件中获取能力信息
            ModelProvider provider = getProvider(model.getProviderCode());
            if (provider != null && model.getCapabilities() != null) {
                List<ModelCapability> capabilities = new ArrayList<>();
                for (String capabilityCode : model.getCapabilities()) {
                    ModelCapability capability = provider.getCapabilities().get(capabilityCode);
                    if (capability != null) {
                        capabilities.add(capability);
                    }
                }
                return capabilities;
            }
            return Collections.emptyList();
        }

        // 获取模型能力列表
        return providerService.getModelCapabilities(modelCode);
    }

    @Override
    public List<AIModel> listModelsByType(String modelType) {
        // 从配置文件获取指定类型的模型
        return listAllModels().stream()
                .filter(model -> modelType.equals(model.getModelType()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ModelProvider enableProvider(String providerCode, boolean enabled) {
        // 检查提供商是否存在
        ModelProvider existingProvider = providerMapper.selectByCode(providerCode);
        if (existingProvider == null) {
            throw new IllegalArgumentException("提供商不存在: " + providerCode);
        }

        // 更新启用状态
        existingProvider.setEnabled(enabled);
        existingProvider.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        providerMapper.updateById(existingProvider);

        return existingProvider;
    }

    @Override
    @Transactional
    public AIModel enableModel(String modelCode, boolean enabled) {
        // 检查模型是否存在
        AIModel existingModel = modelMapper.selectByCode(modelCode);
        if (existingModel == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 更新启用状态
        existingModel.setEnabled(enabled);
        existingModel.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        modelMapper.updateById(existingModel);

        return existingModel;
    }
}