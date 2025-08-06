package com.yonchain.ai.security.oauth2.authorization;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * OAuth2授权类型常量类
 * 包含系统定义的各种授权类型常量
 *
 * @author Cgy
 */
public class OAuth2AuthorizationGrantTypes {

    /**
     * 邮箱授权类型
     */
    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

    /**
     * 钉钉授权码类型
     */
    public static final AuthorizationGrantType DINGTALK = new AuthorizationGrantType("dingtalk");

    /**
     * Dify授权码类型
     */
    public static final AuthorizationGrantType DIFY = new AuthorizationGrantType("dify");

}