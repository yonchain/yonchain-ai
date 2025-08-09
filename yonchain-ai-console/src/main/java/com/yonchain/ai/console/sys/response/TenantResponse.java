package com.yonchain.ai.console.sys.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户响应
 */
@Data
@Schema(description = "租户响应")
public class TenantResponse {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String id;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称", example = "示例租户")
    private String name;

/*
    */
/**
     * 加密公钥
     *//*

    @Schema(description = "加密公钥")
    private String encryptPublicKey;
*/

    /**
     * 计划类型
     */
    @Schema(description = "计划类型", example = "basic")
    private String plan;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "normal")
    private String status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 自定义配置
     */
    @Schema(description = "自定义配置")
    private String customConfig;

    /**
     * 角色
     */
    @Schema(description = "角色")
    private String role;
}
