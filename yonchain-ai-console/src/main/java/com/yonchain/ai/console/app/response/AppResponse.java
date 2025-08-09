package com.yonchain.ai.console.app.response;

import com.yonchain.ai.console.sys.response.RoleResponse;
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
     * 应用模型配置ID
     */
    @Schema(description = "应用模型配置ID")
    private String appModelConfigId;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    private String status;

    /**
     * 是否启用站点
     */
    @Schema(description = "是否启用站点")
    private Boolean enableSite;

    /**
     * 是否启用API
     */
    @Schema(description = "是否启用API")
    private Boolean enableApi;

    /**
     * API每分钟请求限制
     */
    @Schema(description = "API每分钟请求限制")
    private Integer apiRpm;

    /**
     * API每小时请求限制
     */
    @Schema(description = "API每小时请求限制")
    private Integer apiRph;

    /**
     * 是否是演示应用
     */
    @Schema(description = "是否是演示应用")
    private Boolean isDemo;

    /**
     * 是否是公开应用
     */
    @Schema(description = "是否是公开应用")
    private Boolean isPublic;

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
     * 是否是通用应用
     */
    @Schema(description = "是否是通用应用")
    private Boolean isUniversal;

    /**
     * 工作流ID
     */
    @Schema(description = "工作流ID")
    private String workflowId;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 追踪配置
     */
    @Schema(description = "追踪配置")
    private String tracing;

    /**
     * 最大活跃请求数
     */
    @Schema(description = "最大活跃请求数")
    private Integer maxActiveRequests;

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
     * 是否使用图标作为回答图标
     */
    @Schema(description = "是否使用图标作为回答图标")
    private Boolean useIconAsAnswerIcon;

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
}
