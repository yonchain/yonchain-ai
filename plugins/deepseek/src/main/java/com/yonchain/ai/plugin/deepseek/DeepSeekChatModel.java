package com.yonchain.ai.plugin.deepseek;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.retry.support.RetryTemplate;

public class DeepSeekChatModel extends org.springframework.ai.deepseek.DeepSeekChatModel implements ChatModel {

    public DeepSeekChatModel(DeepSeekApi deepSeekApi, DeepSeekChatOptions defaultOptions, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry) {
        super(deepSeekApi, defaultOptions, toolCallingManager, retryTemplate, observationRegistry);
    }
}
