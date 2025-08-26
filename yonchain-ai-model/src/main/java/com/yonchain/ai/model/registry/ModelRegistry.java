package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;

import java.util.List;
import java.util.Map;

/**
 * 模型注册表接口
 * <p>
 * 负责注册和管理模型静态信息
 * 
 * @author Cgy
 */
public interface ModelRegistry {

    /**
     * 获取所有已注册的模型ID
     * 
     * @return 所有已注册的模型ID列表
     */
    List<String> getAllModelIds();
    
    /**
     * 注册模型静态信息
     * 
     * @param modelId 模型ID
     * @param model 模型实体
     * @param provider 提供商实体
     */
    void registerModelInfo(String modelId, ModelEntity model, ModelProviderEntity provider);
    
    /**
     * 获取模型静态信息
     * 
     * @param modelId 模型ID
     * @return 模型静态信息，包含模型实体和提供商实体，如果未找到则返回null
     */
    ModelInfo getModelInfo(String modelId);
    
    /**
     * 获取所有模型静态信息
     * 
     * @return 所有模型静态信息的映射，key为模型ID
     */
    Map<String, ModelInfo> getAllModelInfos();
    
    /**
     * 模型静态信息类
     */
    class ModelInfo {
        private final ModelEntity model;
        private final ModelProviderEntity provider;
        
        public ModelInfo(ModelEntity model, ModelProviderEntity provider) {
            this.model = model;
            this.provider = provider;
        }
        
        public ModelEntity getModel() {
            return model;
        }
        
        public ModelProviderEntity getProvider() {
            return provider;
        }
    }
}