package com.yonchain.ai.security.oauth2.resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * 资源服务器配置类
 *
 * <p>
 * 主要校验Authorization请求Bearer开头的token
 * </p>
 *
 * @author Cgy
 */
@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerConfig {

   /* @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
                                                                 JwtAuthenticationConverter jwtAuthenticationConverter,
                                                                 AccessDeniedHandler accessDeniedHandler,
                                                                 AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize
                        //允许通行的路径
                        .requestMatchers( "/oauth/authorize","/oauth2-test/**","/login/**","/captcha","/swagger-ui/**","/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 管理员API
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
*//*            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 无状态会话
            )*//*
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护，因为我们使用的是无状态API
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                ); // 添加自定义异常处理

        return http.build();
    }



    // JWT 认证转换器 (用于提取权限)
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
*/
}
