package com.dify4j.autoconfigure.console;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.api.idm.TenantService;
import com.yonchain.ai.api.idm.UserService;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.autoconfigure.idm.IdmAutoConfiguration;
import com.yonchain.ai.autoconfigure.security.SecurityAutoConfiguration;
import com.yonchain.ai.console.context.ConsoleContextFilter;
import com.yonchain.ai.idm.service.RedisCacheService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 控制台 自动装配配置
 *
 * @author Cgy
 */
@AutoConfiguration(after = {SecurityAutoConfiguration.class, IdmAutoConfiguration.class})
public class ConsoleAutoConfiguration {

    @Bean
    public FilterRegistrationBean<ConsoleContextFilter> userContextFilter(SecurityService securityService,
                                                                          RedisCacheService cacheService,
                                                                          ObjectMapper objectMapper,
                                                                          UserService userService,
                                                                          TenantService tenantService) {
        FilterRegistrationBean<ConsoleContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ConsoleContextFilter(securityService, cacheService,objectMapper,userService,tenantService));
        registration.addUrlPatterns("/*"); // 拦截所有请求
        registration.setOrder(1); // 设置优先级
        return registration;
    }
}
