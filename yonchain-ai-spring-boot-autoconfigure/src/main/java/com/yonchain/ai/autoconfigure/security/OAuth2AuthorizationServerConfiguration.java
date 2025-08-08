package com.yonchain.ai.autoconfigure.security;

import com.yonchain.ai.security.SecurityConfiguration;
import com.yonchain.ai.security.oauth2.authorization.dify.DifyCodeAuthenticationConverter;
import com.yonchain.ai.security.oauth2.authorization.dify.DifyCodeAuthenticationProvider;
import com.yonchain.ai.security.oauth2.exception.YonchainOAuth2ClientAuthenticationFailureHandler;
import com.yonchain.ai.security.oauth2.exception.YonchainOAuth2ErrorAuthenticationFailureHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ErrorAuthenticationFailureHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * OAuth2 授权服务器配置类
 *
 * @author Cgy
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2AuthorizationServerProperties.class)
public class OAuth2AuthorizationServerConfiguration {

    /**
     * 配置OAuth2授权服务器安全过滤器链
     *
     * @param http                  HttpSecurity配置对象
     * @param securityConfiguration 安全配置组件
     * @param properties            OAuth2授权服务器配置属性
     * @return 配置好的安全过滤器链
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            SecurityConfiguration securityConfiguration,
            OAuth2AuthorizationServerProperties properties) throws Exception {

        // 初始化OAuth2授权服务器配置器
        OAuth2AuthorizationServerConfigurer configurer = new OAuth2AuthorizationServerConfigurer();

        // 配置基础安全设置
        configureBaseSecurity(http, configurer, securityConfiguration);

        // 配置令牌端点认证方式
        configureTokenEndpoint(http, configurer, securityConfiguration);

        // 配置验证码功能（如果启用）
        configureCaptcha(http, securityConfiguration, properties);

        // 配置全局异常处理
        configureExceptionHandling(http, securityConfiguration);

        return http.build();
    }

    /**
     * 配置基础安全设置
     */
    private void configureBaseSecurity(HttpSecurity http,
                                       OAuth2AuthorizationServerConfigurer configurer,
                                       SecurityConfiguration securityConfiguration) throws Exception {

        http
                // 只处理/oauth2/路径下的请求
                .securityMatcher("/oauth2/**")
                // 禁用CSRF保护（OAuth2端点需要）
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**"))
                // 应用OAuth2授权服务器配置
                .with(configurer, cfg -> cfg
                        .registeredClientRepository(securityConfiguration.getRegisteredClientRepository())
                        .authorizationService(securityConfiguration.getAuthorizationService())
                        .tokenGenerator(securityConfiguration.getTokenGenerator())
                        // 配置客户端认证错误处理
                        .clientAuthentication(clientAuth ->
                                clientAuth.errorResponseHandler(new YonchainOAuth2ClientAuthenticationFailureHandler())
                        )
                        // 配置授权端点错误处理
                        .authorizationEndpoint(endpoint ->
                                endpoint.errorResponseHandler(new OAuth2ErrorAuthenticationFailureHandler())
                        )
                );
    }

    /**
     * 配置令牌端点认证方式
     */
    private void configureTokenEndpoint(HttpSecurity http,
                                        OAuth2AuthorizationServerConfigurer configurer,
                                        SecurityConfiguration securityConfiguration) throws Exception {

        configurer.tokenEndpoint(endpoint -> {
            // 添加用户名密码认证方式
            endpoint.accessTokenRequestConverter(securityConfiguration.getUsernamePasswordAuthenticationConverter());
            endpoint.authenticationProvider(securityConfiguration.getUsernamePasswordAuthenticationProvider());

            // 添加钉钉认证方式
            endpoint.accessTokenRequestConverter(securityConfiguration.getDingtalkAuthenticationConverter());
            endpoint.authenticationProvider(securityConfiguration.getDingtalkAuthenticationProvider());

            // 添加Dify认证方式
            endpoint.authenticationProvider(securityConfiguration.getDifyAuthenticationProvider());
            endpoint.accessTokenRequestConverter(securityConfiguration.difyAuthenticationConverter());

            // 添加自定义错误处理器
            endpoint.errorResponseHandler(new YonchainOAuth2ErrorAuthenticationFailureHandler());

            // 添加自定义code认证方式
            endpoint.accessTokenRequestConverter(new DifyCodeAuthenticationConverter());
            endpoint.authenticationProvider(new DifyCodeAuthenticationProvider(
                    securityConfiguration.getAuthorizationService(),
                    securityConfiguration.getUserDetailsService(),
                    securityConfiguration.getDifySecretKey()
            ));
        });
    }

    /**
     * 配置验证码功能
     */
    private void configureCaptcha(HttpSecurity http,
                                  SecurityConfiguration securityConfiguration,
                                  OAuth2AuthorizationServerProperties properties) throws Exception {

        if (properties.isCaptchaEnabled()) {
            // 在Basic认证前添加验证码过滤器
            http.addFilterBefore(
                    securityConfiguration.getCaptchaAuthenticationFilter(),
                    BasicAuthenticationFilter.class
            );
        }
    }

    /**
     * 配置全局异常处理
     */
    private void configureExceptionHandling(HttpSecurity http,
                                            SecurityConfiguration securityConfiguration) throws Exception {

        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(securityConfiguration.getAuthenticationEntryPoint())
                .accessDeniedHandler(securityConfiguration.getAccessDeniedHandler())
        );
    }

    /**
     * 提供OAuth2授权服务Bean
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(SecurityConfiguration securityConfiguration) {
        return securityConfiguration.getAuthorizationService();
    }

    /**
     * 提供注册客户端仓库Bean
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(SecurityConfiguration securityConfiguration) {
        return securityConfiguration.getRegisteredClientRepository();
    }
}
