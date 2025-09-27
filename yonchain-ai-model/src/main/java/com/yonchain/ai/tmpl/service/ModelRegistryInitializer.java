package com.yonchain.ai.tmpl.service;

import com.yonchain.ai.api.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 模型注册中心初始化器
 * 
 * 应用启动时自动加载已配置的模型提供商和模型信息，注册到ModelRegistry
 * 
 * @author yonchain
 */
@Component
public class ModelRegistryInitializer implements ApplicationRunner {
    
    private static final Logger log = LoggerFactory.getLogger(ModelRegistryInitializer.class);
    
    @Autowired(required = false)
    private ModelService modelService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (modelService == null) {
            log.warn("ModelService not available, skipping model registry initialization");
            return;
        }
        
        log.info("Starting model registry initialization...");
        
        try {
            // 调用ModelService的重新加载方法
            modelService.reloadAllConfiguredModels();
            
            log.info("Model registry initialization completed successfully");
        } catch (Exception e) {
            log.error("Failed to initialize model registry", e);
            // 不抛出异常，避免影响应用启动
        }
    }
}
