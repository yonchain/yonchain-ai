package com.yonchain.ai.model.config;

import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.factory.AIModelClientFactory;
import com.yonchain.ai.model.factory.ModelApiFactory;
import com.yonchain.ai.model.factory.ModelFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring AI配置类
 * 支持两种创建客户端的方式：
 * 1. 通过配置文件创建（应用启动时）
 * 2. 通过数据库配置创建（运行时动态创建）
 */
@Configuration
public class SpringAIConfiguration {

    /**
     * OpenAI配置属性
     */
    @Bean
    @ConfigurationProperties(prefix = "yonchain.ai.model.openai")
    public OpenAIProperties openAIProperties() {
        return new OpenAIProperties();
    }

    /**
     * 创建模型API工厂
     */
    @Bean
    public ModelApiFactory modelApiFactory() {
        return new ModelApiFactory();
    }
    
    /**
     * 创建模型工厂
     */
    @Bean
    public ModelFactory modelFactory() {
        return new ModelFactory();
    }

    /**
     * 创建AI模型客户端工厂
     */
    @Bean
    public AIModelClientFactory aiModelClientFactory() {
        return new AIModelClientFactory();
    }

    /**
     * 创建OpenAI API客户端（通过配置文件）
     * 仅当配置文件中存在API密钥时创建
     */
    @Bean
    @ConditionalOnMissingBean(name = "configFileOpenAiApi")
    @ConditionalOnProperty(prefix = "yonchain.ai.model.openai", name = "api-key")
    public OpenAiApi configFileOpenAiApi(OpenAIProperties properties) {
        // 创建OpenAI API客户端
        return OpenAiApi.builder()
            .apiKey(properties.getApiKey())
            .baseUrl(properties.getBaseUrl() != null && !properties.getBaseUrl().isEmpty() ? properties.getBaseUrl() : "https://api.openai.com")
            .build();
    }

    /**
     * 创建OpenAI聊天模型（通过配置文件）
     * 仅当配置文件中存在API密钥时创建
     */
    @Bean
    @ConditionalOnMissingBean(name = "configFileChatModel")
    @ConditionalOnProperty(prefix = "yonchain.ai.model.openai", name = "api-key")
    public ChatModel configFileChatModel(OpenAiApi configFileOpenAiApi, OpenAIProperties properties) {
        // 创建一个临时的提供商对象
        ModelProvider provider = new ModelProvider();
        provider.setCode("openai");
        provider.setId(0L);
        provider.setApiKey(properties.getApiKey());
        provider.setBaseUrl(properties.getBaseUrl());
        provider.setProxyUrl(properties.getProxyUrl());
        
        // 创建一个临时的AIModel对象
        com.yonchain.ai.model.entity.AIModel model = new com.yonchain.ai.model.entity.AIModel();
        model.setCode("gpt-3.5-turbo");
        model.setProviderCode("openai");
        
        // 使用模型工厂创建ChatModel
        return modelFactory().getOpenAiChatModel(model, provider, configFileOpenAiApi);
    }

    /**
     * 创建OpenAI聊天客户端（通过配置文件）
     * 仅当配置文件中存在API密钥时创建
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "configFileChatClient")
    @ConditionalOnProperty(prefix = "yonchain.ai.model.openai", name = "api-key")
    public ChatClient configFileChatClient(ChatModel configFileChatModel) {
        // 创建并返回聊天客户端
        return ChatClient.create(configFileChatModel);
    }
}
