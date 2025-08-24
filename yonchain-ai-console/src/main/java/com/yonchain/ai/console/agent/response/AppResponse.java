package com.yonchain.ai.console.agent.response;

import com.yonchain.ai.console.sys.response.RoleResponse;
import com.yonchain.ai.console.tag.response.TagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用信息响应对象
 */
@Data
@Schema(description = "应用响应")
public class AppResponse {

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private String id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模式
     */
    @Schema(description = "应用模式")
    private String mode;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 图标背景色
     */
    @Schema(description = "图标背景色")
    private String iconBackground;

    /**
     * 应用提供商
     */
    @Schema(description = "应用提供商")
    private String provider;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    private String status;

    /**
     * 应用秘钥
     * 必填字段，不能为空
     */
    @Schema(description = "应用秘钥", required = true)
    private String apiKey;

    /**
     * 基础URL
     */
    @Schema(description = "基础URL", required = true)
    private String baseUrl;

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
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 图标类型
     */
    @Schema(description = "图标类型")
    private String iconType;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private String createdBy;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private String updatedBy;

    /**
     * 角色列表
     */
    @Schema(description = "角色列表")
    private List<RoleResponse> roles;

    /**
     * 角色Id列表
     */
    @Schema(description = "角色Id列表")
    private List<String> roleIds;

    /**
     * 标签列表
     */
    @Schema(description = "标签列表")
    private List<TagResponse> tags;
}
