package com.yonchain.ai.model.registry;

import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存模型注册表实现
 * <p>
 * 负责在内存中注册和管理模型静态信息
 * 
 * @author Cgy
 */
@Component
public class InMemoryModelRegistry implements ModelRegistry {

    private static final Logger log = LoggerFactory.getLogger(InMemoryModelRegistry.class);

    // 提供商存储：key为providerCode，value为提供商信息
    private final Map<String, ModelProvider> providers = new ConcurrentHashMap<>();
    
    // 模型存储：key为providerCode，value为该提供商下的模型列表
    private final Map<String, List<ModelInfo>> modelsByProvider = new ConcurrentHashMap<>();

    @Override
    public List<ModelProvider> getProviders() {
        return new ArrayList<>(providers.values());
    }

    @Override
    public ModelInfo getModels(String modelId) {
        return null;
    }

    @Override
    public List<ModelInfo> getModelsByProvider(String providerCode) {
        return new ArrayList<>(modelsByProvider.getOrDefault(providerCode, new ArrayList<>()));
    }
    
    @Override
    public void registerProvider(ModelProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("提供商不能为空");
        }
        
        String providerCode = provider.getCode();
        if (providerCode == null) {
            throw new IllegalArgumentException("提供商代码不能为空");
        }
        
        providers.put(providerCode, provider);
        // 确保该提供商有对应的模型列表
        modelsByProvider.putIfAbsent(providerCode, new ArrayList<>());
        
        log.debug("注册提供商: {}", providerCode);
    }
    
    @Override
    public void registerModel(ModelInfo model) {
        if (model == null) {
            throw new IllegalArgumentException("模型不能为空");
        }
        
        String providerCode = model.getProviderCode();
        if (providerCode == null) {
            throw new IllegalArgumentException("模型的提供商代码不能为空");
        }
        
        // 确保提供商存在
        modelsByProvider.putIfAbsent(providerCode, new ArrayList<>());
        modelsByProvider.get(providerCode).add(model);
        
        log.debug("注册模型: {} (提供商: {})", model.getCode(), providerCode);
    }
    
    @Override
    public void registerProviders(Collection<? extends ModelProvider> providerList) {
        if (providerList != null) {
            for (ModelProvider provider : providerList) {
                registerProvider(provider);
            }
            log.info("批量注册提供商完成，数量: {}", providerList.size());
        }
    }
    
    @Override
    public void registerModels(Collection<? extends ModelInfo> modelList) {
        if (modelList != null) {
            for (ModelInfo model : modelList) {
                registerModel(model);
            }
            log.info("批量注册模型完成，数量: {}", modelList.size());
        }
    }
}