package com.yonchain.ai.security.crypto;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * yonchain 委托密码编码器实现
 *
 * <p>
 * 提供了多种密码编码方式的委托支持，默认包含 Spring Security 支持的所有编码器，
 * 并额外添加了 Dify 专用的 PBKDF2 编码器。
 *
 * </p>
 *
 * @author Cgy
 */
public class YonchainDelegatingPasswordEncoder implements PasswordEncoder {

    private static final String DEFAULT_ID = PasswordEncoderType.DIFY_PBKDF2;

    private final DelegatingPasswordEncoder delegatingPasswordEncoder;

    public YonchainDelegatingPasswordEncoder() {
        delegatingPasswordEncoder = new DelegatingPasswordEncoder(DEFAULT_ID, this.createDefaultPasswordEncoder());
    }

    public YonchainDelegatingPasswordEncoder(Map<String, PasswordEncoder> idToPasswordEncoder) {
        this(DEFAULT_ID, idToPasswordEncoder);
    }

    public YonchainDelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder) {
        Map<String, PasswordEncoder> encoders = new HashMap<>(this.createDefaultPasswordEncoder());
        if (idToPasswordEncoder != null) {
            encoders.putAll(idToPasswordEncoder);
        }
        delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegatingPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegatingPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 创建默认的密码编码器
     *
     * @return 默认的密码编码器
     */
    @SuppressWarnings("deprecation")
    private Map<String, PasswordEncoder> createDefaultPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
        encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
        encoders.put("pbkdf2@SpringSecurity_v5_8", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
        encoders.put("scrypt@SpringSecurity_v5_8", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
        encoders.put("SHA-256", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_2());
        encoders.put("argon2@SpringSecurity_v5_8", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        // dify密码编码器
        encoders.put(PasswordEncoderType.DIFY_PBKDF2, DifyPbkdf2PasswordEncoder.defaultEncoder());
        return encoders;
    }
}
