package com.yonchain.ai.model.chat;

import com.yonchain.ai.model.registry.ModelRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 委托式聊天模型，用于动态调用不同的模型实现
 * 参考Spring Security的DelegatingPasswordEncoder设计
 */
public class DelegatingChatModel implements ChatModel {

    private final String defaultModelId;
    private final ModelRegistry modelRegistry;

    public DelegatingChatModel(ModelRegistry modelRegistry) {
        Assert.notNull(modelRegistry, "模型注册表不能为空");
        this.defaultModelId = "deepseek-chat";
        this.modelRegistry = modelRegistry;
    }

    public DelegatingChatModel(String defaultModelId, ModelRegistry modelRegistry) {
        Assert.hasText(defaultModelId, "默认模型ID不能为空");
        Assert.notNull(modelRegistry, "模型注册表不能为空");
        this.defaultModelId = defaultModelId;
        this.modelRegistry = modelRegistry;

    }

    /**
     * 根据模型ID获取对应的ChatModel实现
     * 从注册表中获取模型
     * @param modelId 模型ID
     * @return 对应的ChatModel实现
     */
    protected ChatModel getModel(String modelId) {
        // 从注册表中获取模型
        ChatModel model = modelRegistry.getModel(modelId);
        
        // 如果没有找到，则使用默认模型
        if (model == null) {
            model = modelRegistry.getModel(this.defaultModelId);
            
            // 如果默认模型也不存在，则抛出异常
            if (model == null) {
                throw new IllegalStateException("无法找到默认模型: " + this.defaultModelId);
            }
        }
        
        return model;
    }

    /**
     * 使用Prompt中指定的模型或默认模型生成回复
     * @param prompt 提示信息
     * @return 聊天回复
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        String modelId = getModelIdFromPrompt(prompt);
        return getModel(modelId).call(prompt);
    }


    /**
     * 使用Prompt中指定的模型或默认模型生成流式回复
     * @param prompt 提示信息
     * @return 聊天回复流
     */
    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        String modelId = getModelIdFromPrompt(prompt);
        return getModel(modelId).stream(prompt);
    }

    /**
     * 从Prompt中提取模型ID
     * 如果Prompt中没有指定模型ID，则返回默认模型ID
     * @param prompt 提示信息
     * @return 模型ID
     */
    protected String getModelIdFromPrompt(Prompt prompt) {
        if (prompt == null) {
            return this.getDefaultModelId();
        }

        // 尝试从Prompt中获取模型ID
        String modelId = prompt.getOptions().getModel();
        if (StringUtils.hasText(modelId)) {
            // 检查是否需要添加提供商前缀
            if (!modelId.contains("-")) {
                // 如果没有提供商前缀，则使用默认提供商
                String[] defaultParts = defaultModelId.split("-");
                if (defaultParts.length > 1) {
                    modelId = modelId + "-" + defaultParts[1];
                }
            }

            // 检查模型是否存在
            if (modelRegistry.hasModel(modelId)) {
                return modelId;
            }
        }

        return defaultModelId;
    }

    /**
     * 获取默认模型ID
     * @return 默认模型ID
     */
    public String getDefaultModelId() {
        return defaultModelId;
    }

    /**
     * 判断指定模型ID是否可用
     * @param modelId 模型ID
     * @return 是否可用
     */
    public boolean isModelAvailable(String modelId) {
        return modelRegistry.hasModel(modelId);
    }

    /**
     * 获取所有可用的模型ID
     * @return 模型ID列表
     */
    public List<String> getAvailableModelIds() {
        return modelRegistry.getAllModelIds();
    }
}