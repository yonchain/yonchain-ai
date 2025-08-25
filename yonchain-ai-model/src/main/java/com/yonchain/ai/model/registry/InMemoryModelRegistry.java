package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import com.yonchain.ai.model.factory.ModelFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型注册表实现类
 * <p>
 * 负责注册和管理ChatModel实例
 *
 * @author Cgy
 */
@Component
public class InMemoryModelRegistry implements ModelRegistry {

    @Autowired
    private ModelFactory modelFactory;

    /**
     * 缓存已注册的ChatModel实例
     * key: 模型ID (modelCode-providerCode)
     */
    private final Map<String, ChatModel> modelRegistry = new ConcurrentHashMap<>();

    /**
     * 注册ChatModel实例
     *
     * @param modelId 模型ID
     * @param chatModel ChatModel实例
     */
    @Override
    public void registerModel(String modelId, ChatModel chatModel) {
        modelRegistry.put(modelId, chatModel);
    }

    /**
     * 获取ChatModel实例
     * 如果注册表中不存在，则尝试通过ModelFactory创建
     *
     * @param modelId 模型ID
     * @return ChatModel实例，如果未找到则返回null
     */
    @Override
    public ChatModel getModel(String modelId) {
        // 首先从注册表中查找
        ChatModel model = modelRegistry.get(modelId);

        // 如果注册表中不存在，则尝试通过ModelFactory创建
        if (model == null) {
            try {
                // 解析modelId，格式为：modelCode-providerCode
                String[] parts = modelId.split("-");
                if (parts.length == 2) {
                    String modelCode = parts[0];
                    String providerCode = parts[1];

                    // 创建模型实体和提供商实体
                    ModelEntity modelEntity = new ModelEntity();
                    modelEntity.setModelCode(modelCode);

                    ModelProviderEntity providerEntity = new ModelProviderEntity();
                    providerEntity.setProviderCode(providerCode);

                    // 通过ModelFactory创建ChatModel
                    model = modelFactory.getChatModel(modelEntity, providerEntity);

                    // 如果创建成功，则添加到注册表
                    if (model != null) {
                        registerModel(modelId, model);
                    }
                }
            } catch (Exception e) {
                // 如果创建失败，则返回null
                return null;
            }
        }

        return model;
    }

    /**
     * 移除ChatModel实例
     *
     * @param modelId 模型ID
     * @return 被移除的ChatModel实例，如果未找到则返回null
     */
    @Override
    public ChatModel removeModel(String modelId) {
        return modelRegistry.remove(modelId);
    }

    /**
     * 检查是否存在指定的ChatModel实例
     *
     * @param modelId 模型ID
     * @return 如果存在则返回true，否则返回false
     */
    @Override
    public boolean hasModel(String modelId) {
        return modelRegistry.containsKey(modelId);
    }

    /**
     * 获取所有已注册的模型ID
     *
     * @return 所有已注册的模型ID
     */
    public List<String> getAllModelIds() {
        return modelRegistry.keySet().stream().toList();
    }

    /**
     * 清除所有已注册的模型
     */
    public void clearAllModels() {
        modelRegistry.clear();
    }

    /**
     * 获取已注册模型的数量
     *
     * @return 已注册模型的数量
     */
    public int getModelCount() {
        return modelRegistry.size();
    }
}