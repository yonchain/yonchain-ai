package com.yonchain.ai.plugin.initialization;

import com.yonchain.ai.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 插件系统初始化器
 * 在应用启动时自动重新加载已启用的插件
 * 
 * @author yonchain
 */
@Component
@Order(100) // 在模型初始化器之后执行
public class PluginSystemInitializer implements ApplicationRunner {
    
    private static final Logger log = LoggerFactory.getLogger(PluginSystemInitializer.class);
    
    @Autowired
    private PluginManager pluginManager;
    
    @Value("${yonchain.plugin.auto-load-enabled:true}")
    private boolean autoLoadEnabled;
    
    @Value("${yonchain.plugin.auto-load-delay:5000}")
    private long autoLoadDelay;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!autoLoadEnabled) {
            log.info("Plugin auto-loading is disabled");
            return;
        }
        
        log.info("Starting plugin system initialization...");
        
        try {
            // 延迟一段时间以确保Spring容器完全启动
            if (autoLoadDelay > 0) {
                log.debug("Waiting {} ms before loading plugins...", autoLoadDelay);
                Thread.sleep(autoLoadDelay);
            }
            
            // 自动加载已启用的插件
            //TODO pluginManager.loadInstalledPlugins();
            
            log.info("Plugin system initialization completed successfully");
            
        } catch (Exception e) {
            log.error("Failed to initialize plugin system", e);
            // 不抛出异常，避免影响应用启动
        }
    }
}
