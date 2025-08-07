package com.yonchain.ai.security.oauth2.jwt;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.util.Map;

/**
 * yonchain JWT解码器实现
 * <p>
 * 提供JwtDecoder接口的实现，用于验证和解码JWT令牌。
 * 使用RSA算法验证JWT签名。
 * </p>
 *
 * @see JwtDecoder
 */
public final class OAuth2JwtDecoder implements JwtDecoder {

    private final NimbusJwtDecoder jwtDecoder;


    public OAuth2JwtDecoder(JWKSource<SecurityContext> jwkSource) {
        jwtDecoder = (NimbusJwtDecoder) OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
        // 添加自定义校验器
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                new JwtIssuerValidator(JwtConstant.JWT_ISSUER)
        );
        jwtDecoder.setJwtValidator(validator);
    }


    @Override
    public Jwt decode(String token) throws JwtException {
        return jwtDecoder.decode(token);
    }


    public void setJwtValidator(OAuth2TokenValidator<Jwt> jwtValidator) {
        jwtDecoder.setJwtValidator(jwtValidator);
    }


    public void setClaimSetConverter(Converter<Map<String, Object>, Map<String, Object>> claimSetConverter) {
        jwtDecoder.setClaimSetConverter(claimSetConverter);
    }
}
