package com.yonchain.ai.autoconfigure.dify;


import com.yonchain.ai.api.dify.DifyAppService;
import com.yonchain.ai.console.dify.DifyResponseFactory;
import com.yonchain.ai.dify.DifyConfiguration;
import com.yonchain.ai.dify.service.DifyAppServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * dify 自动配置
 *
 * @author Cgy
 */
@AutoConfiguration
public class DifyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DifyResponseFactory difyResponseFactory() {
        return new DifyResponseFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DifyConfiguration difyConfiguration(DifyAppService difyAppService) {
        return DifyConfiguration.builder()
                .difyAppService(difyAppService)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public DifyAppService difyAppService() {
        return new DifyAppServiceImpl();
    }
}
