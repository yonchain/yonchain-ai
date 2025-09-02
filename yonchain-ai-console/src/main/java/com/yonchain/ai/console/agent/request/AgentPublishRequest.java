package com.yonchain.ai.console.agent.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 智能体发布请求
 *
 * @author Cgy
 * @since 2024-08-24
 */
@Data
@Schema(description = "智能体发布请求")
public class AgentPublishRequest {

    @NotBlank(message = "提示词不能为空")
    @Schema(description = "提示词", required = true)
    private String prompt;

    @NotNull(message = "模型ID不能为空")
    @Schema(description = "模型ID", required = true)
    private List<String> modelIds;
    
    @Schema(description = "开场白")
    private String welcomeMessage;

    @Schema(description = "知识库ID列表")
    private List<String> knowledgeIds;

    @Schema(description = "插件ID列表")
    private List<String> pluginIds;

    @Schema(description = "MCP配置")
    private List<String> mcps;

    @Schema(description = "工作流ID")
    private List<String> workflowIds;
}