package com.yonchain.ai.model.chat;

import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.model.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;


/**
 * 委托式聊天模型，用于动态调用不同的模型实现
 * 参考Spring Security的DelegatingPasswordEncoder设计
 * 优化为直接从ModelFactory获取模型实例
 */
public class DelegatingChatModel implements ChatModel {

    private static final Logger logger = LoggerFactory.getLogger(DelegatingChatModel.class);

    private final String defaultModelId;
    private final ModelFactory modelFactory;

    public DelegatingChatModel(ModelFactory modelFactory) {
        Assert.notNull(modelFactory, "模型工厂不能为空");
        this.defaultModelId = "deepseek-chat";
        this.modelFactory = modelFactory;
    }

    public DelegatingChatModel(String defaultModelId, ModelFactory modelFactory) {
        Assert.hasText(defaultModelId, "默认模型ID不能为空");
        Assert.notNull(modelFactory, "模型工厂不能为空");
        this.defaultModelId = defaultModelId;
        this.modelFactory = modelFactory;
    }

    /**
     * 使用Prompt中指定的模型或默认模型生成回复
     *
     * @param prompt 提示信息
     * @return 聊天回复
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        String modelId = getModelIdFromPrompt(prompt);
        return getModel(modelId).call(Prompt.builder()
                .messages(prompt.getUserMessage())
                .chatOptions(ChatOptions.builder()
                        .model(modelId.substring(0, modelId.lastIndexOf('-')))
                        .build())
                .build());
    }

    /**
     * 使用Prompt中指定的模型或默认模型生成流式回复
     *
     * @param prompt 提示信息
     * @return 聊天回复流
     */
    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        String modelId = getModelIdFromPrompt(prompt);
        return getModel(modelId).stream(prompt);
    }

    /**
     * 根据模型ID获取对应的ChatModel实现
     * 从模型工厂获取模型实例
     *
     * @param modelId 模型ID
     * @return 对应的ChatModel实现
     */
    protected ChatModel getModel(String modelId) {
        // 从模型工厂获取模型
        ChatModel model = modelFactory.getModel(modelId);

        // 如果没有找到，则使用默认模型
        if (model == null) {
            logger.debug("未找到模型: {}, 尝试使用默认模型: {}", modelId, defaultModelId);

            // 从模型工厂获取默认模型
            model = modelFactory.getModel(defaultModelId);

            // 如果默认模型也不存在，则抛出异常
            if (model == null) {
                throw new YonchainIllegalStateException("无法找到默认模型: " + modelId);
            }
        }

        return model;
    }

    /**
     * 从Prompt中提取模型ID
     * 如果Prompt中没有指定模型ID，则返回默认模型ID
     *
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
            return modelId;
        }

        return defaultModelId;
    }

    /**
     * 获取默认模型ID
     *
     * @return 默认模型ID
     */
    public String getDefaultModelId() {
        return defaultModelId;
    }
}