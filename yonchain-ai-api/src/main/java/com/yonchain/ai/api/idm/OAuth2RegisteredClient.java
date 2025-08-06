package com.yonchain.ai.api.idm;

import java.util.Date;

/**
 * OAuth2注册客户端接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface OAuth2RegisteredClient {

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    void setId(String id);

    /**
     * 设置客户端ID
     *
     * @param clientId 客户端ID
     */
    void setClientId(String clientId);

    /**
     * 设置客户端ID发布时间
     *
     * @param clientIdIssuedAt 客户端ID发布时间
     */
    void setClientIdIssuedAt(Date clientIdIssuedAt);

    /**
     * 设置客户端密钥
     *
     * @param clientSecret 客户端密钥
     */
    void setClientSecret(String clientSecret);

    /**
     * 设置客户端密钥过期时间
     *
     * @param clientSecretExpiresAt 客户端密钥过期时间
     */
    void setClientSecretExpiresAt(Date clientSecretExpiresAt);

    /**
     * 设置客户端名称
     *
     * @param clientName 客户端名称
     */
    void setClientName(String clientName);

    /**
     * 设置客户端认证方法
     *
     * @param clientAuthenticationMethods 客户端认证方法
     */
    void setClientAuthenticationMethods(String clientAuthenticationMethods);

    /**
     * 设置授权类型
     *
     * @param authorizationGrantTypes 授权类型
     */
    void setAuthorizationGrantTypes(String authorizationGrantTypes);

    /**
     * 设置重定向URI
     *
     * @param redirectUris 重定向URI
     */
    void setRedirectUris(String redirectUris);

    /**
     * 设置登出后重定向URI
     *
     * @param postLogoutRedirectUris 登出后重定向URI
     */
    void setPostLogoutRedirectUris(String postLogoutRedirectUris);

    /**
     * 设置作用域
     *
     * @param scopes 作用域
     */
    void setScopes(String scopes);

    /**
     * 设置客户端设置
     *
     * @param clientSettings 客户端设置
     */
    void setClientSettings(String clientSettings);

    /**
     * 设置令牌设置
     *
     * @param tokenSettings 令牌设置
     */
    void setTokenSettings(String tokenSettings);

    /**
     * 获取主键ID
     *
     * @return 主键ID
     */
    String getId();

    /**
     * 获取客户端ID
     *
     * @return 客户端ID
     */
    String getClientId();

    /**
     * 获取客户端ID发布时间
     *
     * @return 客户端ID发布时间
     */
    Date getClientIdIssuedAt();

    /**
     * 获取客户端密钥
     *
     * @return 客户端密钥
     */
    String getClientSecret();

    /**
     * 获取客户端密钥过期时间
     *
     * @return 客户端密钥过期时间
     */
    Date getClientSecretExpiresAt();

    /**
     * 获取客户端名称
     *
     * @return 客户端名称
     */
    String getClientName();

    /**
     * 获取客户端认证方法
     *
     * @return 客户端认证方法
     */
    String getClientAuthenticationMethods();

    /**
     * 获取授权类型
     *
     * @return 授权类型
     */
    String getAuthorizationGrantTypes();

    /**
     * 获取重定向URI
     *
     * @return 重定向URI
     */
    String getRedirectUris();

    /**
     * 获取登出后重定向URI
     *
     * @return 登出后重定向URI
     */
    String getPostLogoutRedirectUris();

    /**
     * 获取作用域
     *
     * @return 作用域
     */
    String getScopes();

    /**
     * 获取客户端设置
     *
     * @return 客户端设置
     */
    String getClientSettings();

    /**
     * 获取令牌设置
     *
     * @return 令牌设置
     */
    String getTokenSettings();
}
