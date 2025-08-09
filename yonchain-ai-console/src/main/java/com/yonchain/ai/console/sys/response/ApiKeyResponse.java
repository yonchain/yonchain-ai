package com.yonchain.ai.console.sys.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API密钥响应
 */
@Data
@Schema(description = "API密钥响应")
public class ApiKeyResponse {

    /**
     * ID
     */
    @Schema(description = "ID")
    private String id;

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private String appId;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;

    /**
     * 密钥令牌
     */
    @Schema(description = "密钥令牌")
    private String token;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 最后使用时间
     */
    @Schema(description = "最后使用时间")
    private LocalDateTime lastUsedAt;

}
