package com.yonchain.ai.model.registry;

import org.springframework.ai.chat.model.ChatModel;

/**
 * 模型注册表接口
 * <p>
 * 负责注册和管理ChatModel实例
 * 完全复制ChatModelRegistry的设计
 * 
 * @author Cgy
 */
public interface ModelRegistry {

    /**
     * 注册ChatModel实例
     * 
     * @param modelId 模型ID
     * @param chatModel ChatModel实例
     */
    void registerModel(String modelId, ChatModel chatModel);
    
    /**
     * 获取ChatModel实例
     * 
     * @param modelId 模型ID
     * @return ChatModel实例，如果未找到则返回null
     */
    ChatModel getModel(String modelId);
    
    /**
     * 移除ChatModel实例
     * 
     * @param modelId 模型ID
     * @return 被移除的ChatModel实例，如果未找到则返回null
     */
    ChatModel removeModel(String modelId);
    
    /**
     * 检查是否存在指定的ChatModel实例
     * 
     * @param modelId 模型ID
     * @return 如果存在则返回true，否则返回false
     */
    boolean hasModel(String modelId);
}