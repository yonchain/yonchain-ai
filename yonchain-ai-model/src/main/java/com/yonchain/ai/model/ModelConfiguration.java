package com.yonchain.ai.model;

import com.yonchain.ai.model.chat.DelegatingChatModel;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.loader.ModelLoader;
import com.yonchain.ai.model.loader.YamlModelLoader;
import com.yonchain.ai.model.registry.InMemoryModelRegistry;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.model.registry.ModelRegistryInitializer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfiguration {

    @Bean
    public ChatModel chatModel(ModelRegistry modelRegistry, ModelFactory modelFactory) {
        return new DelegatingChatModel(modelRegistry, modelFactory);
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean
    public ModelLoader modelLoader() {
        return new YamlModelLoader();
    }

    @Bean
    public ModelRegistry modelRegistry() {
        return new InMemoryModelRegistry();
    }

    @Bean
    public ModelRegistryInitializer modelRegistryInitializer(ModelRegistry modelRegistry, ModelLoader modelLoader) {
        return new ModelRegistryInitializer(modelRegistry, modelLoader);
    }

    @Bean
    public ModelFactory modelFactory(ModelRegistry modelRegistry){
        return new ModelFactory(modelRegistry);
    }

}
