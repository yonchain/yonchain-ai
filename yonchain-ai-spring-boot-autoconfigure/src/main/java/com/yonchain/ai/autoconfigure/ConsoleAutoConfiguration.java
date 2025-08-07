package com.dify4j.autoconfigure.console;


import com.dify4j.api.idm.TenantService;
import com.dify4j.api.idm.UserService;
import com.dify4j.api.security.SecurityService;
import com.dify4j.autoconfigure.idm.IdmAutoConfiguration;
import com.dify4j.autoconfigure.security.SecurityAutoConfiguration;
import com.dify4j.console.context.ConsoleContextFilter;
import com.dify4j.idm.service.RedisCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
