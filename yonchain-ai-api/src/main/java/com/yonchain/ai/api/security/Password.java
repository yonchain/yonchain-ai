package com.yonchain.ai.api.security;

/**
 * dify密码
 */
public class Password {

    /**
     * 密码
     */
    private final String password;

    /**
     * 盐值
     */
    private final String salt;


    public Password(String password, String salt) {
        this.password = password;
        this.salt = salt;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 获取盐值
     *
     * @return 盐值
     */
    public String getSalt() {
        return salt;
    }
}
