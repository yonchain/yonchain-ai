package com.yonchain.ai.model.config;

import com.yonchain.ai.model.service.impl.ModelManagerServiceImpl;
import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * 模型管理模块自动配置类
 */
@Configuration
@ComponentScan(basePackages = {"com.yonchain.ai.model"})
@MapperScan(basePackages = {"com.yonchain.ai.model.mapper"})
public class ModelAutoConfiguration {

    private final ModelManagerServiceImpl modelManagerService;

    public ModelAutoConfiguration(ModelManagerServiceImpl modelManagerService) {
        this.modelManagerService = modelManagerService;
    }

    /**
     * 初始化模型管理服务
     */
    @PostConstruct
    public void init() {
       // modelManagerService.init();
    }

    /**
     * 注册Spring AI配置
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringAIConfiguration springAIConfiguration() {
        return new SpringAIConfiguration();
    }
}