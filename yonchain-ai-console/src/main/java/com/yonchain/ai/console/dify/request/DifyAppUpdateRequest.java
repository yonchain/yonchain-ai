package com.yonchain.ai.console.dify.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 应用创建/更新请求参数
 */
@Data
@Schema(description = "应用请求")
public class DifyAppUpdateRequest {

    /**
     * 应用名称
     * 必填字段，不能为空
     */
    @NotBlank(message = "应用名称不能为空")
    @Schema(description = "应用名称", required = true)
    private String name;

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
     * 应用图标URL
     * 可选字段
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 图标背景色
     * 十六进制颜色代码，如#FFFFFF
     */
    @Schema(description = "图标背景色")
    private String iconBackground;

    /**
     * 图标类型
     * 可选值：emoji, image, svg等
     */
    @Schema(description = "图标类型")
    private String iconType;

    /**
     * 应用描述
     * 最大长度255字符
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 是否使用 WebApp 图标替换
     */
    @Schema(description = "是否使用 WebApp 图标替换")
    private Boolean useIconAsAnswerIcon;

    /**
     * 应用所属角色组ID列表
     * 可选字段
     */
    @Schema(description = "角色ID列表")
    private List<String> roleIds;

}
