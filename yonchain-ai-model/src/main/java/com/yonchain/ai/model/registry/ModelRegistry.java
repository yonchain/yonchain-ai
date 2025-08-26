package com.yonchain.ai.model.registry;

import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;

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
     * @param model 模型信息
     * @param provider 提供商信息
     */
    void registerModelInfo(String modelId, ModelInfo model, ModelProvider provider);
    
    /**
     * 获取模型静态信息
     * 
     * @param modelId 模型ID
     * @return 模型静态信息，包含模型信息和提供商信息，如果未找到则返回null
     */
    RegistryModelInfo getModelInfo(String modelId);
    
    /**
     * 获取所有模型静态信息
     * 
     * @return 所有模型静态信息的映射，key为模型ID
     */
    Map<String, RegistryModelInfo> getAllModelInfos();
    
    /**
     * 模型静态信息类
     */
    class RegistryModelInfo {
        private final ModelInfo model;
        private final ModelProvider provider;
        
        public RegistryModelInfo(ModelInfo model, ModelProvider provider) {
            this.model = model;
            this.provider = provider;
        }
        
        public ModelInfo getModel() {
            return model;
        }
        
        public ModelProvider getProvider() {
            return provider;
        }
    }
}