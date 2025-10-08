package com.yonchain.ai.console.plugin.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 插件查询请求
 * 
 * @author yonchain
 */
@Data
@Schema(description = "插件查询请求")
public class PluginQueryRequest extends PageRequest {

    /**
     * 插件名称（模糊查询）
     */
    @Schema(description = "插件名称（模糊查询）", example = "openai")
    private String name;

    /**
     * 插件状态
     */
    @Schema(description = "插件状态", example = "enabled", allowableValues = {"enabled", "disabled", "installing", "uninstalling", "failed"})
    private String status;

    /**
     * 插件类型
     */
    @Schema(description = "插件类型", example = "ai_model", allowableValues = {"ai_model", "tool", "workflow"})
    private String type;
}
