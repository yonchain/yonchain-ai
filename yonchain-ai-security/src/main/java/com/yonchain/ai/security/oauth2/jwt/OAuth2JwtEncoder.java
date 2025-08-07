package com.yonchain.ai.security.oauth2.jwt;


import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.oauth2.jwt.*;

import java.util.Map;

/**
 * yonchain JWT编码器实现
 * <p>
 * 提供JwtEncoder接口的实现，用于生成JWT令牌。
 * 使用RSA算法对JWT进行签名。
 * </p>
 *
 * @see JwtEncoder
 */
public final class OAuth2JwtEncoder implements JwtEncoder {

    private final JwtEncoder jwtEncoder;

    /**
     * 使用指定的JWK源和RS256算法创建JWT编码器
     *
     * @param jwkSource JWK源
     */
    public OAuth2JwtEncoder(JWKSource<SecurityContext> jwkSource) {
        jwtEncoder = new NimbusJwtEncoder(jwkSource);
    }


    @Override
    public Jwt encode(JwtEncoderParameters parameters) {
        //此次后续有扩展

        // 创建JWT载荷
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.claim("Author", "yonchainAI");

        // 添加标准声明
        JwtClaimsSet claims = parameters.getClaims();
        Map<String, Object> allClaims = claims.getClaims();
        for (Map.Entry<String, Object> claim : allClaims.entrySet()) {
            String name = claim.getKey();
            Object value = claim.getValue();
            claimsBuilder.claim(name, value);
        }

        JwsHeader jwsHeader = parameters.getJwsHeader();
        JWTClaimsSet claimsSet = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims));
    }
}
