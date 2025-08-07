package com.dify4j.autoconfigure.security;

import com.dify4j.security.oauth2.jwt.OAuth2JWKSource;
import com.dify4j.security.oauth2.jwt.OAuth2JwtDecoder;
import com.dify4j.security.oauth2.jwt.OAuth2JwtEncoder;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.*;


/**
 * JWT 自动装配配置
 *
 * @author Cgy
 */
@AutoConfiguration
@ConditionalOnClass(JWKSource.class)
@EnableConfigurationProperties(OAuth2JwtProperties.class)
public class OAuth2JwtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JWKSource<SecurityContext> jwkSource(OAuth2JwtProperties jwtProperties) {
        return new OAuth2JWKSource(jwtProperties.getPrivateKey(),
                jwtProperties.getPublicKey(),
                jwtProperties.getKid());
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return new OAuth2JwtDecoder(jwkSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new OAuth2JwtEncoder(jwkSource);
    }
}
