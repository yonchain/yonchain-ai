package com.yonchain.ai.model.config;

import com.yonchain.ai.model.core.ModelClient;
import com.yonchain.ai.model.core.ModelClientFactory;
import com.yonchain.ai.model.core.ModelClientFactoryBuilder;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.factory.impl.DeepSeekNamespaceFactory;
import com.yonchain.ai.model.factory.impl.OpenAINamespaceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 模型客户端自动配置类
 */
@Configuration
public class ModelClientAutoConfiguration {
    
    /**
     * 创建ModelClientFactory
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ModelClientFactory modelClientFactory() {
        return new ModelClientFactoryBuilder()
            .build("model-config.xml");
    }
    
    /**
     * 创建ModelClient
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ModelClient modelClient(ModelClientFactory factory, 
                                  List<ModelFactory> modelFactories) {
        
        // 注册模型工厂
        for (ModelFactory modelFactory : modelFactories) {
            factory.getConfiguration()
                   .getNamespaceFactoryRegistry()
                   .registerFactory(modelFactory);
        }
        
        return factory.createClient();
    }
    
    /**
     * OpenAI命名空间工厂
     */
    @Bean
    @ConditionalOnProperty(name = "yonchain.ai.models.openai.enabled", havingValue = "true", matchIfMissing = true)
    public OpenAINamespaceFactory openAINamespaceFactory() {
        return new OpenAINamespaceFactory();
    }
    
    /**
     * DeepSeek命名空间工厂
     */
    @Bean
    @ConditionalOnProperty(name = "yonchain.ai.models.deepseek.enabled", havingValue = "true", matchIfMissing = true)
    public DeepSeekNamespaceFactory deepSeekNamespaceFactory() {
        return new DeepSeekNamespaceFactory();
    }
}
