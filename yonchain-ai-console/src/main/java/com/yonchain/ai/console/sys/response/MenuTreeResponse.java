package com.yonchain.ai.console.sys.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单树形响应
 */
@Data
@Schema(description = "菜单树形响应")
public class MenuTreeResponse {

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
    private Integer sort;

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
     * 是否隐藏
     */
    @Schema(description = "是否隐藏")
    private Boolean isHide;


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
     * 子菜单列表
     */
    @Schema(description = "子菜单列表")
    private List<MenuTreeResponse> children;

}
