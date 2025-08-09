package com.yonchain.ai.console.sys.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OAuth2注册客户端查询请求
 *
 * @author Cgy
 * @since 1.0.0
 */
@Schema(description = "OAuth2注册客户端查询请求")
public class OAuth2RegisteredClientQueryRequest extends PageRequest {

    /**
     * 客户端ID
     */
    @Schema(description = "客户端ID")
    private String clientId;

    /**
     * 客户端名称
     */
    @Schema(description = "客户端名称")
    private String clientName;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}