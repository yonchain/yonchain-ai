package com.yonchain.ai.chat;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;

public class DefaultChatModelSeviceImpl implements ChatModelService{


    @Override
    public ChatModel getModel(String model) {
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey("sk-3ef709a6aa404b00af299c288264a48f")
                .baseUrl("https://api.deepseek.com")
                .build();
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-chat")
                .temperature(0.7)
                .maxTokens(4096)
                .build();
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(options)
                .build();
    }
}
