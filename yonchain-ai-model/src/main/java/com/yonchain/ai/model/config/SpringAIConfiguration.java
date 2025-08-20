package com.yonchain.ai.model.config;

import com.yonchain.ai.model.factory.AIModelClientFactory;
import com.yonchain.ai.model.factory.ModelFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
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

/*    *//**
     * OpenAI配置属性
     *//*
    @Bean
    @ConfigurationProperties(prefix = "yonchain.ai.model.openai")
    public OpenAIProperties openAIProperties() {
        return new OpenAIProperties();
    }
    */
/*    *//**
     * 创建模型工厂
     *//*
    @Bean
    public ModelFactory modelFactory() {
        return new ModelFactory();
    }

    *//**
     * 创建AI模型客户端工厂
     *//*
    @Bean
    public AIModelClientFactory aiModelClientFactory() {
        return new AIModelClientFactory();
    }*/
/*
    *//**
     * 创建OpenAI聊天客户端（通过配置文件）
     * 仅当配置文件中存在API密钥时创建
     *//*
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "configFileChatClient")
    @ConditionalOnProperty(prefix = "yonchain.ai.model.openai", name = "api-key")
    public ChatClient configFileChatClient(OpenAIProperties properties, ModelFactory modelFactory) {
        try {
            // 使用模型工厂创建ChatModel
            ChatModel chatModel = modelFactory.createOpenAiChatModel(
                properties.getApiKey(),
                properties.getBaseUrl(),
                "gpt-3.5-turbo",
                0.7f
            );
            
            // 创建并返回聊天客户端
            return ChatClient.create(chatModel);
        } catch (Exception e) {
            throw new RuntimeException("创建配置文件ChatClient失败: " + e.getMessage(), e);
        }
    }*/
}
