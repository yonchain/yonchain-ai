package com.yonchain.ai.sys.entity;

import com.yonchain.ai.api.sys.OAuth2RegisteredClient;
import lombok.Data;

import java.util.Date;

/**
 * OAuth2注册客户端实体
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class OAuth2RegisteredClientEntity implements OAuth2RegisteredClient {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端ID发布时间
     */
    private Date clientIdIssuedAt;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 客户端密钥过期时间
     */
    private Date clientSecretExpiresAt;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端认证方法
     */
    private String clientAuthenticationMethods;

    /**
     * 授权类型
     */
    private String authorizationGrantTypes;

    /**
     * 重定向URI
     */
    private String redirectUris;

    /**
     * 登出后重定向URI
     */
    private String postLogoutRedirectUris;

    /**
     * 作用域
     */
    private String scopes;

    /**
     * 客户端设置
     */
    private String clientSettings;

    /**
     * 令牌设置
     */
    private String tokenSettings;
}
