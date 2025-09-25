package com.yonchain.ai.model.core;

import com.yonchain.ai.model.config.XMLConfigBuilder;
import com.yonchain.ai.model.config.YAMLConfigBuilder;
import com.yonchain.ai.model.core.impl.DefaultModelClientFactory;
import com.yonchain.ai.model.util.Resources;

import java.io.InputStream;

/**
 * ModelClientFactory构建器
 * 
 * 仿照MyBatis的SqlSessionFactoryBuilder设计，支持从不同的配置源构建ModelClientFactory
 */
public class ModelClientFactoryBuilder {
    
    /**
     * 从XML配置文件构建ModelClientFactory
     */
    public ModelClientFactory build(String resource) {
        return build(Resources.getResourceAsStream(resource));
    }
    
    /**
     * 从输入流构建ModelClientFactory
     */
    public ModelClientFactory build(InputStream inputStream) {
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream);
        return build(parser.parse());
    }
    
    /**
     * 从YAML配置文件构建ModelClientFactory
     */
    public ModelClientFactory buildFromYaml(String resource) {
        return buildFromYaml(Resources.getResourceAsStream(resource));
    }
    
    /**
     * 从YAML输入流构建ModelClientFactory
     */
    public ModelClientFactory buildFromYaml(InputStream inputStream) {
        YAMLConfigBuilder parser = new YAMLConfigBuilder(inputStream);
        return build(parser.parse());
    }
    
    /**
     * 从配置对象构建ModelClientFactory
     */
    public ModelClientFactory build(ModelConfiguration configuration) {
        return new DefaultModelClientFactory(configuration);
    }
    
    /**
     * 构建默认的ModelClientFactory（用于测试）
     */
    public ModelClientFactory buildDefault() {
        return new DefaultModelClientFactory(new ModelConfiguration());
    }
}

