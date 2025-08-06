package com.yonchain.ai.security.oauth2.authorization.dify;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashSet;

/**
 * DifyJWT令牌生成器
 * <p>
 * 该类负责生成OAuth2协议的JWT访问令牌和刷新令牌
 * <p>
 * 主要功能：
 * 1. 使用HS256算法生成JWT访问令牌
 * 2. 生成安全的随机刷新令牌
 * 3. 支持自定义令牌过期时间
 * <p>
 * 使用说明：
 * 1. 构造时需要传入密钥字符串
 * 2. 调用generateAccessToken生成访问令牌
 * 3. 调用generateRefreshToken生成刷新令牌
 * <p>
 * 注意事项：
 * 1. 密钥长度建议至少256位
 * 2. 生产环境应从安全配置中获取密钥
 * 3. 默认令牌有效期为24小时
 */
public class DifyJwtTokenGenerator {

    private final JwtEncoder jwtEncoder;
    private final String secretKey;
    private final String edition;
    private final int accessTokenExpireMinutes;

    public DifyJwtTokenGenerator(String secretKey) {
        this.secretKey = secretKey;
        this.edition = "SELF_HOSTED";
        this.accessTokenExpireMinutes = 60 * 24; // 24小时
        this.jwtEncoder = this.jwtEncoder(null);//createDefaultJwtEncoder();
    }

    public OAuth2AccessToken generateAccessToken(String userId) {
        try {
            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plus(accessTokenExpireMinutes, ChronoUnit.MINUTES);

            // 构建claims
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject("Console API Passport")
                    .issuedAt(issuedAt)
                    .expiresAt(expiresAt)
                    .issuer(edition)
                    .claim("user_id", userId)
                    .build();

            // 构建JWT头部，指定算法和类型
            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256)
                    .type("JWT")  // 明确设置typ为JWT
                    .build();

            // 生成JWT
            Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims));

            return new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    jwt.getTokenValue(),
                    issuedAt,
                    expiresAt,
                    new HashSet<>()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate access token", e);
        }
    }

    public OAuth2RefreshToken generateRefreshToken(String userId) {
        // Dify4jUser user = (Dify4jUser) userDetails;
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenExpireMinutes, ChronoUnit.MINUTES);

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64]; // 512 bits
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        return new OAuth2RefreshToken(
                token,
                issuedAt,
                expiresAt
        );
    }

    public JwtEncoder createDefaultJwtEncoder() {
        // HS256密钥(生产环境应从安全配置中获取)
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");

        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKeySpec));
    }

    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        SecretKeySpec secretKey2 = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey2));
    }

}
