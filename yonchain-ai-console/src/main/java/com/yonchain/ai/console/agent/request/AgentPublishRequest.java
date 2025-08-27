package com.yonchain.ai.console.agent.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    
    @Schema(description = "模型ID")
    private String modelId;
    
    @Schema(description = "开场白")
    private String welcomeMessage;

    @Schema(description = "知识库ID列表")
    private List<String> knowledgeBaseIds;

    @Schema(description = "插件ID列表")
    private List<String> pluginIds;

    @Schema(description = "MCP配置")
    private Map<String, Object> mcpConfig;

    @Schema(description = "工作流ID")
    private String workflowId;
    
    @Schema(description = "智能体名称", required = true)
    @NotBlank(message = "智能体名称不能为空")
    private String name;
    
    @Schema(description = "智能体描述")
    private String description;
    
    @Schema(description = "图标")
    private String icon;
    
    @Schema(description = "图标背景色")
    private String iconBackground;
    
    @Schema(description = "角色ID列表")
    private List<String> roleIds;
}