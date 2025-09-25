package com.yonchain.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.filter.AudioModelFilter;
import com.yonchain.ai.filter.ChatModelFilter;
import com.yonchain.ai.filter.EmbeddingModelFilter;
import com.yonchain.ai.filter.ImageModelFilter;
import com.yonchain.ai.model.core.ModelClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 过滤器配置类
 * 注册各种模型类型的专门过滤器
 */
@Configuration
public class FilterConfiguration {
    
    /**
     * 注册聊天模型过滤器
     */
    @Bean
    public FilterRegistrationBean<ChatModelFilter> chatModelFilterRegistration(
            ModelClient modelClient, ObjectMapper objectMapper) {
        
        FilterRegistrationBean<ChatModelFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ChatModelFilter(modelClient, objectMapper));
        registration.addUrlPatterns("/v1/chat/*", "/chat/*");
        registration.setName("chatModelFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        
        return registration;
    }
    
    /**
     * 注册图像模型过滤器
     */
    @Bean
    public FilterRegistrationBean<ImageModelFilter> imageModelFilterRegistration(
            ModelClient modelClient, ObjectMapper objectMapper) {
        
        FilterRegistrationBean<ImageModelFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ImageModelFilter(modelClient, objectMapper));
        registration.addUrlPatterns("/v1/images/*", "/images/*");
        registration.setName("imageModelFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        
        return registration;
    }
    
    /**
     * 注册嵌入模型过滤器
     */
    @Bean
    public FilterRegistrationBean<EmbeddingModelFilter> embeddingModelFilterRegistration(
            ModelClient modelClient, ObjectMapper objectMapper) {
        
        FilterRegistrationBean<EmbeddingModelFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new EmbeddingModelFilter(modelClient, objectMapper));
        registration.addUrlPatterns("/v1/embeddings", "/embeddings");
        registration.setName("embeddingModelFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 30);
        
        return registration;
    }
    
    /**
     * 注册音频模型过滤器
     */
    @Bean
    public FilterRegistrationBean<AudioModelFilter> audioModelFilterRegistration(
            ModelClient modelClient, ObjectMapper objectMapper) {
        
        FilterRegistrationBean<AudioModelFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AudioModelFilter(modelClient, objectMapper));
        registration.addUrlPatterns("/v1/audio/*", "/audio/*");
        registration.setName("audioModelFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 40);
        
        return registration;
    }
}

