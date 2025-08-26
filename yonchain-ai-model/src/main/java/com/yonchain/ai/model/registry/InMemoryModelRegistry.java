package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模型注册表实现类
 * <p>
 * 负责注册和管理模型静态信息
 *
 * @author Cgy
 */
@Component
public class InMemoryModelRegistry implements ModelRegistry {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryModelRegistry.class);

    /**
     * 缓存模型静态信息
     * key: 模型ID (modelCode-providerCode)
     */
    private final Map<String, ModelInfo> modelInfoRegistry = new ConcurrentHashMap<>();

    /**
     * 获取所有已注册的模型ID
     *
     * @return 所有已注册的模型ID
     */
    @Override
    public List<String> getAllModelIds() {
        return modelInfoRegistry.keySet().stream().collect(Collectors.toList());
    }
    
    /**
     * 注册模型静态信息
     * 
     * @param modelId 模型ID
     * @param model 模型实体
     * @param provider 提供商实体
     */
    @Override
    public void registerModelInfo(String modelId, ModelEntity model, ModelProviderEntity provider) {
        ModelInfo modelInfo = new ModelInfo(model, provider);
        modelInfoRegistry.put(modelId, modelInfo);
        logger.info("已注册模型静态信息: {}", modelId);
    }
    
    /**
     * 获取模型静态信息
     * 
     * @param modelId 模型ID
     * @return 模型静态信息，包含模型实体和提供商实体，如果未找到则返回null
     */
    @Override
    public ModelInfo getModelInfo(String modelId) {
        return modelInfoRegistry.get(modelId);
    }
    
    /**
     * 获取所有模型静态信息
     * 
     * @return 所有模型静态信息的映射，key为模型ID
     */
    @Override
    public Map<String, ModelInfo> getAllModelInfos() {
        return new HashMap<>(modelInfoRegistry);
    }
    
    /**
     * 移除模型静态信息
     * 
     * @param modelId 模型ID
     * @return 被移除的模型静态信息，如果未找到则返回null
     */
    public ModelInfo removeModelInfo(String modelId) {
        ModelInfo removed = modelInfoRegistry.remove(modelId);
        if (removed != null) {
            logger.info("已移除模型静态信息: {}", modelId);
        }
        return removed;
    }
    
    /**
     * 清除所有模型静态信息
     */
    public void clearAllModelInfos() {
        modelInfoRegistry.clear();
        logger.info("已清除所有模型静态信息");
    }
    
    /**
     * 获取已注册模型静态信息的数量
     * 
     * @return 已注册模型静态信息的数量
     */
    public int getModelInfoCount() {
        return modelInfoRegistry.size();
    }
    
    /**
     * 检查是否存在指定的模型静态信息
     * 
     * @param modelId 模型ID
     * @return 如果存在则返回true，否则返回false
     */
    public boolean hasModelInfo(String modelId) {
        return modelInfoRegistry.containsKey(modelId);
    }
    
    /**
     * 更新模型静态信息
     * 
     * @param modelId 模型ID
     * @param model 模型实体
     * @param provider 提供商实体
     * @return 如果更新成功则返回true，否则返回false
     */
    public boolean updateModelInfo(String modelId, ModelEntity model, ModelProviderEntity provider) {
        if (modelInfoRegistry.containsKey(modelId)) {
            registerModelInfo(modelId, model, provider);
            logger.info("已更新模型静态信息: {}", modelId);
            return true;
        }
        return false;
    }
}