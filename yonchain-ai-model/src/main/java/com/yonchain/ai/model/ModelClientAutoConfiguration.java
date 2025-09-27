package com.yonchain.ai.model;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 模型客户端自动配置类
 */
@Configuration
public class ModelClientAutoConfiguration {
    
    /**
     * 创建ModelClientFactory
     */
    @Bean
    //@Primary
    @ConditionalOnMissingBean
    public ModelClientFactory modelClientFactory() {
        return new ModelClientFactoryBuilder()
            .build("model-config.xml");
    }
    
    /**
     * 创建ModelClient
     */
    @Bean
    //@Primary
    @ConditionalOnMissingBean
    public ModelClient modelClient(ModelClientFactory factory) {
        // 工厂注册现在通过XML配置文件的factory属性自动完成
        return factory.createClient();
    }
    
    // 工厂实例现在通过XML配置文件的factory属性自动创建和注册
}
