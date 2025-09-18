package com.yonchain.ai.registry;

import com.yonchain.ai.model.ModelMetadata;

/**
 * 模型变化监听器
 * 用于分布式场景下的模型变化通知
 */
public interface ModelChangeListener {
    
    /**
     * 模型注册事件
     * 
     * @param metadata 模型元数据
     */
    void onModelRegistered(ModelMetadata metadata);
    
    /**
     * 模型注销事件
     * 
     * @param modelName 模型名称
     */
    void onModelUnregistered(String modelName);
    
    /**
     * 模型更新事件
     * 
     * @param metadata 更新后的模型元数据
     */
    void onModelUpdated(ModelMetadata metadata);
    
    /**
     * 模型可用性变化事件
     * 
     * @param modelName 模型名称
     * @param available 是否可用
     */
    void onModelAvailabilityChanged(String modelName, boolean available);
}
