package com.yonchain.ai.plugin.configuration;

import com.yonchain.ai.model.ModelClientFactory;
import com.yonchain.ai.model.ModelClientFactoryBuilder;
import com.yonchain.ai.model.ModelConfiguration;
import com.yonchain.ai.model.ModelRegistry;
import com.yonchain.ai.plugin.initialization.PluginSystemInitializer;
import com.yonchain.ai.plugin.PluginManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 插件系统自动配置
 *
 * @author yonchain
 */
@Configuration
public class PluginAutoConfiguration {

    /**
     * 配置插件系统初始化器
     *
     * @param pluginManager 插件管理器
     * @return 插件系统初始化器
     */
    @Bean
    @ConditionalOnProperty(name = "yonchain.plugin.auto-load-enabled", havingValue = "true", matchIfMissing = true)
    public PluginSystemInitializer pluginSystemInitializer(PluginManager pluginManager) {
        return new PluginSystemInitializer();
    }

    @Bean
    public ModelRegistry modelRegistry(ModelConfiguration configuration) {
        return configuration.getModelRegistry();
    }

    @Bean
    public ModelClientFactory modelClientFactory(ModelConfiguration configuration) {
        return new ModelClientFactoryBuilder().build(configuration);
    }

    @Bean
    public ModelConfiguration modelConfiguration() {
        return new ModelConfiguration();
    }
}
