package com.yonchain.ai.model;

import com.yonchain.ai.model.chat.DelegatingChatModel;
import com.yonchain.ai.model.registry.ModelRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfiguration {


    @Bean
    public ChatModel chatModel(ModelRegistry modelRegistry) {
        return new DelegatingChatModel(modelRegistry);
    }
}
