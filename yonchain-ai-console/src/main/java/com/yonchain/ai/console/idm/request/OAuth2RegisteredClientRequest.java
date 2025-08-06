package com.yonchain.ai.console.idm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * OAuth2注册客户端请求
 *
 * @author Cgy
 * @since 1.0.0
 */
@Schema(description = "OAuth2注册客户端请求")
public class OAuth2RegisteredClientRequest {

    /**
     * 客户端ID
     */
    @Schema(description = "客户端ID")
    @NotBlank(message = "客户端ID不能为空")
    @Size(max = 100, message = "客户端ID长度不能超过100")
    private String clientId;

    /**
     * 客户端密钥
     */
    @Schema(description = "客户端密钥")
    @Size(max = 200, message = "客户端密钥长度不能超过200")
    private String clientSecret;

    /**
     * 客户端名称
     */
    @Schema(description = "客户端名称")
    @NotBlank(message = "客户端名称不能为空")
    @Size(max = 200, message = "客户端名称长度不能超过200")
    private String clientName;


    /**
     * 授权类型
     */
    @Schema(description = "授权类型，多个类型用逗号分隔")
    @NotBlank(message = "授权类型不能为空")
    @Size(max = 1000, message = "授权类型长度不能超过1000")
    private String authorizationGrantTypes;

    /**
     * 重定向URI
     */
    @Schema(description = "重定向URI，多个URI用逗号分隔")
    @Size(max = 1000, message = "重定向URI长度不能超过1000")
    private String redirectUris;

    /**
     * 作用域
     */
    @Schema(description = "作用域，多个作用域用逗号分隔")
    @NotBlank(message = "作用域不能为空")
    @Size(max = 1000, message = "作用域长度不能超过1000")
    private String scopes;

    /**
     * 刷新token过期时间
     */
    @Schema(description = "访问token过期时间，单位秒")
    @NotNull(message = "访问token过期时间不能为空")
    private Long accessTokenTimeToLive;

    /**
     * 刷新token过期时间
     */
    @Schema(description = "刷新token过期时间，单位秒")
    @NotNull(message = "刷新token过期时间不能为空")
    private Long refreshTokenTimeToLive;


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
}
