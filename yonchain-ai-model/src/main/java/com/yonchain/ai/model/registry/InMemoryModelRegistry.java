package com.yonchain.ai.model.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于内存的模型注册表实现
 * <p>
 * 完全复制DefaultChatModelRegistry的设计
 * 
 * @author Cgy
 */
@Slf4j
@Component
public class InMemoryModelRegistry implements ModelRegistry {

    private final ConcurrentMap<String, ChatModel> models = new ConcurrentHashMap<>();

    @Override
    public void registerModel(String modelId, ChatModel chatModel) {
        if (modelId == null || modelId.trim().isEmpty()) {
            throw new IllegalArgumentException("模型ID不能为空");
        }
        if (chatModel == null) {
            throw new IllegalArgumentException("ChatModel不能为空");
        }
        
        models.put(modelId, chatModel);
        log.debug("注册模型: {}", modelId);
    }

    @Override
    public ChatModel getModel(String modelId) {
        if (modelId == null || modelId.trim().isEmpty()) {
            return null;
        }
        return models.get(modelId);
    }

    @Override
    public ChatModel removeModel(String modelId) {
        if (modelId == null || modelId.trim().isEmpty()) {
            return null;
        }
        
        ChatModel removed = models.remove(modelId);
        if (removed != null) {
            log.debug("移除模型: {}", modelId);
        }
        return removed;
    }

    @Override
    public boolean hasModel(String modelId) {
        if (modelId == null || modelId.trim().isEmpty()) {
            return false;
        }
        return models.containsKey(modelId);
    }
}