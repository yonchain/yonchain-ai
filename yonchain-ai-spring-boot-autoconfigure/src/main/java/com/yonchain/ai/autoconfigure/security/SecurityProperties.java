package com.yonchain.ai.autoconfigure.security;

import com.yonchain.ai.security.crypto.PasswordEncoderType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dify4j安全配置属性类
 *
 * @author Cgy
 * @since 1.0.0
 */
@ConfigurationProperties("dify4j.security")
public class SecurityProperties {

    /**
     * 密码编码器类型
     * <p>
     * 指定用于密码加密的编码器类型。默认使用Dify4j的PBKDF2实现。
     * 可选值包括：
     * </p>
     *
     * @see com.yonchain.ai.security.crypto.PasswordEncoderType
     */
    private String passwordEncoderType = PasswordEncoderType.DIFY_PBKDF2;

    /**
     * 是否启用密码编码器前缀
     * <p>
     * 当设置为true时，存储的密码将包含编码器类型前缀，例如"{bcrypt}"。
     * 这允许在同一系统中使用多种密码编码方式，并在验证时自动选择正确的编码器。
     * </p>
     * <p>默认值为true。</p>
     */
    private boolean enablePasswordEncoderPrefix = true;

    /**
     * dify密钥
     */
    private String difySecretKey;

    /**
     * 获取密码编码器类型
     *
     * @return 当前配置的密码编码器类型
     */
    public String getPasswordEncoderType() {
        return passwordEncoderType;
    }

    /**
     * 设置密码编码器类型
     *
     * @param passwordEncoderType 要使用的密码编码器类型
     * @see com.yonchain.ai.security.crypto.PasswordEncoderType
     */
    public void setPasswordEncoderType(String passwordEncoderType) {
        this.passwordEncoderType = passwordEncoderType;
    }

    /**
     * 检查是否启用密码编码器前缀
     *
     * @return 如果启用了密码编码器前缀则返回true，否则返回false
     */
    public boolean isEnablePasswordEncoderPrefix() {
        return enablePasswordEncoderPrefix;
    }

    /**
     * 设置是否启用密码编码器前缀
     *
     * @param enablePasswordEncoderPrefix 是否启用密码编码器前缀
     */
    public void setEnablePasswordEncoderPrefix(boolean enablePasswordEncoderPrefix) {
        this.enablePasswordEncoderPrefix = enablePasswordEncoderPrefix;
    }

    /**
     * 设置dify密钥
     *
     * @param difySecretKey dify密钥
     */
    public void setDifySecretKey(String difySecretKey) {
        this.difySecretKey = difySecretKey;
    }

    /**
     * 获取dify密钥
     *
     * @return dify密钥
     */
    public String getDifySecretKey() {
        return difySecretKey;
    }
}
