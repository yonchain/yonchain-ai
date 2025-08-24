package com.yonchain.ai.autoconfigure.dify;


import com.yonchain.ai.api.dify.DifyService;
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
    public DifyConfiguration difyConfiguration(DifyService difyService) {
        return DifyConfiguration.builder()
                .difyAppService(difyService)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public DifyService difyAppService() {
        return new DifyAppServiceImpl();
    }
}
