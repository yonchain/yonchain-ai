package com.yonchain.ai.console.sys.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单响应
 */
@Data
@Schema(description = "菜单响应")
public class MenuResponse {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private String parentId;

    /**
     * 权重
     */
    @Schema(description = "权重")
    private Integer weight;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String name;

    /**
     * 菜单路径
     */
    @Schema(description = "菜单路径")
    private String path;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    private Integer sortOrder;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型")
    private String menuType;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String permission;

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
     * 是否为链接
     */
    @Schema(description = "是否为链接")
    private Boolean isLink;

    /**
     * 是否为iframe
     */
    @Schema(description = "是否为iframe")
    private Boolean isIframe;

    /**
     * 是否保持活跃状态
     */
    @Schema(description = "是否保持活跃状态")
    private Boolean isKeepAlive;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 英文名称
     */
    @Schema(description = "英文名称")
    private String enName;

    /**
     * 是否固定
     */
    @Schema(description = "是否固定")
    private Boolean isAffix;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 是否隐藏
     */
    @Schema(description = "是否隐藏")
    private Boolean isHide;
}
