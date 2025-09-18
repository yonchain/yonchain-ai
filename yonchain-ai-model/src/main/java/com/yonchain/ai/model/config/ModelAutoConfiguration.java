package com.yonchain.ai.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.chat.ChatModelService;
import com.yonchain.ai.chat.impl.DefaultChatModelService;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.factory.impl.DefaultModelFactory;
import com.yonchain.ai.image.ImageModelService;
import com.yonchain.ai.image.impl.DefaultImageModelService;
import com.yonchain.ai.model.provider.ModelProvider;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.model.registry.impl.LocalModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 模型自动配置类
 * 负责自动装配模型相关的Bean
 */
@Configuration
@EnableConfigurationProperties({ModelProperties.class})
public class ModelAutoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelAutoConfiguration.class);
    
    /**
     * 配置ObjectMapper Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    /**
     * 配置模型注册中心Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ModelRegistry modelRegistry() {
        logger.info("Creating LocalModelRegistry as default");
        return new LocalModelRegistry();
    }
    
    /**
     * 配置模型工厂Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ModelFactory modelFactory(List<ModelProvider> providers) {
        logger.info("Creating DefaultModelFactory with {} providers", 
            providers != null ? providers.size() : 0);
        return new DefaultModelFactory(providers);
    }
    
    /**
     * 配置聊天模型服务Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ChatModelService chatModelService(ModelRegistry modelRegistry, ModelFactory modelFactory) {
        logger.info("Creating DefaultChatModelService");
        return new DefaultChatModelService(modelRegistry, modelFactory);
    }
    
    /**
     * 配置图像模型服务Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ImageModelService imageModelService(ModelRegistry modelRegistry, ModelFactory modelFactory) {
        logger.info("Creating DefaultImageModelService");
        return new DefaultImageModelService(modelRegistry, modelFactory);
    }
    
    /**
     * 配置模型初始化器
     */
    @Bean
    public ModelInitializer modelInitializer(ModelRegistry modelRegistry, ModelProperties properties) {
        return new ModelInitializer(modelRegistry, properties);
    }
}
