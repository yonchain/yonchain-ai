package com.yonchain.ai.console.sys.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单请求
 */
@Data
@Schema(description = "菜单请求")
public class MenuRequest {

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
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 255, message = "菜单名称长度不能超过255个字符")
    @Schema(description = "菜单名称")
    private String name;

    /**
     * 菜单路径
     */
    @Size(max = 255, message = "菜单路径长度不能超过255个字符")
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
