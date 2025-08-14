package com.yonchain.ai.console.tag.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签响应
 */
@Data
@Schema(description = "标签响应")
public class TagResponse {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 标签类型
     */
    @Schema(description = "标签类型")
    private String type;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String name;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private String createdBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

}
