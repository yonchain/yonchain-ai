package com.yonchain.ai.console.sys.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * OAuth2注册客户端响应
 *
 * @author Cgy
 * @since 1.0.0
 */
@Schema(description = "OAuth2注册客户端响应")
public class OAuth2RegisteredClientResponse {

    /**
     * ID
     */
    @Schema(description = "ID")
    private String id;

    /**
     * 客户端ID
     */
    @Schema(description = "客户端ID")
    private String clientId;

    /**
     * 客户端密钥
     */
    @Schema(description = "客户端密钥")
    private String clientSecret;

    /**
     * 客户端名称
     */
    @Schema(description = "客户端名称")
    private String clientName;

    /**
     * 授权类型
     */
    @Schema(description = "授权类型，多个类型用逗号分隔")
    private String authorizationGrantTypes;

    /**
     * 重定向URI
     */
    @Schema(description = "重定向URI，多个URI用逗号分隔")
    private String redirectUris;

    /**
     * 作用域
     */
    @Schema(description = "作用域，多个作用域用逗号分隔")
    private String scopes;

    /**
     * 刷新token过期时间
     */
    @Schema(description = "访问token过期时间，单位秒")
    private Long accessTokenTimeToLive;

    /**
     * 刷新token过期时间
     */
    @Schema(description = "刷新token过期时间，单位秒")
    private Long refreshTokenTimeToLive;

    /**
     * 客户端ID发布时间
     */
    @Schema(description = "客户端ID发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date clientIdIssuedAt;
    
    /**
     * 客户端密钥过期时间
     */
    @Schema(description = "客户端密钥过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date clientSecretExpiresAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public void setAuthorizationGrantTypes(String authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
    }

    public String getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public Long getAccessTokenTimeToLive() {
        return accessTokenTimeToLive;
    }

    public void setAccessTokenTimeToLive(Long accessTokenTimeToLive) {
        this.accessTokenTimeToLive = accessTokenTimeToLive;
    }

    public Long getRefreshTokenTimeToLive() {
        return refreshTokenTimeToLive;
    }

    public void setRefreshTokenTimeToLive(Long refreshTokenTimeToLive) {
        this.refreshTokenTimeToLive = refreshTokenTimeToLive;
    }

    public Date getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public void setClientIdIssuedAt(Date clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }
    
    public Date getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public void setClientSecretExpiresAt(Date clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }
}
