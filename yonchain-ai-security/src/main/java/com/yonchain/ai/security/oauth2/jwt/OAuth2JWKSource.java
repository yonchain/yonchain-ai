package com.yonchain.ai.security.oauth2.jwt;

import com.yonchain.ai.api.exception.YonchainException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * OAuth2 JWT密钥源
 * <p>
 *  提供JWKSource接口的实现，用于JWT编码和解码。
 * </p>
 *
 * @author Cgy
 */
public class OAuth2JWKSource implements JWKSource<SecurityContext> {

    /**
     * JWK源
     */
    private final ImmutableJWKSet<SecurityContext> immutableJWKSet;


    public OAuth2JWKSource() {
        this(generateRsa());
    }

    public OAuth2JWKSource(String privateKey, String publicKey, String kid) {
        this(generateRsa(privateKey, publicKey, kid));
    }

    public OAuth2JWKSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        immutableJWKSet = new ImmutableJWKSet<>(jwkSet);
    }


    @Override
    public List<JWK> get(JWKSelector jwkSelector, SecurityContext context) {
        return immutableJWKSet.get(jwkSelector, context);
    }


    /**
     * 从字符串创建RSA密钥对
     *
     * @param privateKeyStr Base64编码的私钥
     * @param publicKeyStr  Base64编码的公钥
     * @param kid           密钥ID
     * @return RSA密钥对
     * @throws Exception 如果创建密钥失败
     */
    private static RSAKey generateRsa(String privateKeyStr, String publicKeyStr, String kid) {
        try {
            // 1. 清理并解码私钥
            String cleanPrivateKey = privateKeyStr.replaceAll("\\s", "");
            byte[] privateKeyBytes = Base64.getDecoder().decode(cleanPrivateKey);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            // 2. 清理并解码公钥
            String cleanPublicKey = publicKeyStr.replaceAll("\\s", "");
            byte[] publicKeyBytes = Base64.getDecoder().decode(cleanPublicKey);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            // 3. 创建密钥对
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

            // 4. 构建RSA密钥
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(kid)
                    .build();
        } catch (Exception e) {
            throw new YonchainException("创建RSA密钥对错误", e);
        }
    }

    /**
     * 生成随机RSA密钥对
     *
     * @return RSA密钥对
     */
    private static RSAKey generateRsa() {
        try {
            // 1. 生成密钥对
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // 2. 获取公钥和私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            // 3. 构建RSA密钥
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }
}
