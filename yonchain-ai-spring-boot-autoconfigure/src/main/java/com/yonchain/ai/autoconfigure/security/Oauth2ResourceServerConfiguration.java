package com.dify4j.autoconfigure.security;

import com.dify4j.security.SecurityConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 资源服务器配置类
 *
 * @author Cgy
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2ResourceServerProperties.class)
public class Oauth2ResourceServerConfiguration {

    /**
     * 配置资源服务器安全过滤器链
     * 
     * @param http HttpSecurity配置对象
     * @param jwtDecoder JWT解码器
     * @param jwtAuthenticationConverter JWT认证转换器
     * @param oauth2ResourceServerProperties 资源服务器配置属性
     * @param securityConfiguration 安全配置组件
     * @return 配置好的安全过滤器链
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            OAuth2ResourceServerProperties oauth2ResourceServerProperties,
            SecurityConfiguration securityConfiguration) throws Exception {
        
        // 配置请求授权
        configureRequestAuthorization(http, oauth2ResourceServerProperties);
        
        // 配置OAuth2资源服务器
        configureOAuth2ResourceServer(http, jwtDecoder, jwtAuthenticationConverter, securityConfiguration);
        
        // 配置安全策略
        configureSecurityPolicies(http, securityConfiguration);
        
        return http.build();
    }

    /**
     * 配置请求授权规则
     */
    private void configureRequestAuthorization(
            HttpSecurity http,
            OAuth2ResourceServerProperties properties) throws Exception {
        
        http.securityMatcher("/**")
            .authorizeHttpRequests(authorize -> authorize
                // 允许配置的路径匿名访问
                .requestMatchers(properties.getPermitPaths().stream().toArray(String[]::new))
                .permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            );
    }

    /**
     * 配置OAuth2资源服务器
     */
    private void configureOAuth2ResourceServer(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            SecurityConfiguration securityConfiguration) throws Exception {
        
        http.oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationConverter)
            )
            // 配置自定义异常处理
            .authenticationEntryPoint(securityConfiguration.getAuthenticationEntryPoint())
            .accessDeniedHandler(securityConfiguration.getAccessDeniedHandler())
        );
    }

    /**
     * 配置安全策略
     */
    private void configureSecurityPolicies(
            HttpSecurity http,
            SecurityConfiguration securityConfiguration) throws Exception {
        
        // 禁用CSRF保护（适用于无状态API）
        http.csrf(AbstractHttpConfigurer::disable);
        
        // 配置全局异常处理
        http.exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(securityConfiguration.getAuthenticationEntryPoint())
            .accessDeniedHandler(securityConfiguration.getAccessDeniedHandler())
        );
    }

    /**
     * 创建JWT认证转换器
     * 
     * 功能：
     * - 从JWT令牌中提取权限信息
     * - 添加ROLE_前缀
     * - 从"roles"声明中获取权限
     * 
     * @return 配置好的JWT认证转换器
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置权限前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        // 设置权限声明名称
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
}
