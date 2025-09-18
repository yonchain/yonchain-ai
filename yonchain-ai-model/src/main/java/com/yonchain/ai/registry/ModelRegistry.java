package com.yonchain.ai.registry;

import com.yonchain.ai.model.ModelMetadata;
import com.yonchain.ai.model.ModelType;

import java.util.List;
import java.util.Optional;

/**
 * 模型注册表接口
 * 支持本地和分布式实现
 */
public interface ModelRegistry {
    
    /**
     * 注册模型
     * 
     * @param metadata 模型元数据
     */
    void registerModel(ModelMetadata metadata);
    
    /**
     * 注销模型
     * 
     * @param modelName 模型名称
     */
    void unregisterModel(String modelName);
    
    /**
     * 获取模型元数据
     * 
     * @param modelName 模型名称
     * @return 模型元数据
     */
    Optional<ModelMetadata> getModelMetadata(String modelName);
    
    /**
     * 根据类型获取模型列表
     * 
     * @param modelType 模型类型
     * @return 模型元数据列表
     */
    List<ModelMetadata> getModelsByType(ModelType modelType);
    
    /**
     * 获取所有模型
     * 
     * @return 所有模型元数据列表
     */
    List<ModelMetadata> getAllModels();
    
    /**
     * 检查模型是否存在
     * 
     * @param modelName 模型名称
     * @return 是否存在
     */
    boolean containsModel(String modelName);
    
    /**
     * 检查模型是否可用
     * 
     * @param modelName 模型名称
     * @return 是否可用
     */
    boolean isModelAvailable(String modelName);
    
    /**
     * 刷新注册表
     * 从配置源重新加载模型信息
     */
    void refresh();
    
    /**
     * 批量注册模型
     * 
     * @param metadataList 模型元数据列表
     */
    default void registerModels(List<ModelMetadata> metadataList) {
        metadataList.forEach(this::registerModel);
    }
    
    /**
     * 监听模型变化（可选，用于分布式场景）
     * 
     * @param listener 变化监听器
     */
    default void addModelChangeListener(ModelChangeListener listener) {
        // 默认空实现，具体实现类可以重写
    }
}
