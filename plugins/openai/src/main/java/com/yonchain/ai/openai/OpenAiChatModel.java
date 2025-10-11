package com.yonchain.ai.openai;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;

public class OpenAiChatModel extends org.springframework.ai.openai.OpenAiChatModel implements ChatModel {

    public OpenAiChatModel(OpenAiApi openAiApi, OpenAiChatOptions defaultOptions, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry) {
        super(openAiApi, defaultOptions, toolCallingManager, retryTemplate, observationRegistry);
    }
}
