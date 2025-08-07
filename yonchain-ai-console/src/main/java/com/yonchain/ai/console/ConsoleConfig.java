package com.yonchain.ai.console;


import com.yonchain.ai.api.yonchainService;
import com.yonchain.ai.api.yonchainServiceImpl;
import com.yonchain.ai.api.idm.TenantService;
import com.yonchain.ai.api.idm.UserService;
import com.yonchain.ai.api.knowledge.KnowledgeService;
import com.yonchain.ai.api.security.SecurityEventPublisher;
import com.yonchain.ai.console.sys.event.UserLoginLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 控制台配置
 *
 * @author Cgy
 * @since 1.0.0
 */
@Configuration
public class ConsoleConfig {


    @Bean
    public ResponseFactory responseFactory() {
        return new ResponseFactory();
    }


    @Bean
    public yonchainService yonchainService(UserService userService, TenantService tenantService, KnowledgeService knowledgeService) {
        return new YonchainServiceImpl(userService, tenantService, knowledgeService);
    }

    @Bean
    public UserLoginLogger userLoginLogger() {
        UserLoginLogger userLoginLogger = new UserLoginLogger();
        SecurityEventPublisher.addListener(userLoginLogger);
        return userLoginLogger;
    }
}
