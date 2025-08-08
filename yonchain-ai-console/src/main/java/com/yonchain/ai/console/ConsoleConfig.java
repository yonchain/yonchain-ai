package com.yonchain.ai.console;


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
    public UserLoginLogger userLoginLogger() {
        UserLoginLogger userLoginLogger = new UserLoginLogger();
        SecurityEventPublisher.addListener(userLoginLogger);
        return userLoginLogger;
    }
}
