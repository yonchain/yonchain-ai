package com.yonchain.ai.console.agent.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能体发布记录响应
 *
 * @author Cgy
 * @since 2024-08-27
 */
@Data
@Schema(description = "智能体发布记录响应")
public class AgentPublishRecordResponse {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "智能体ID")
    private String agentId;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "智能体名称")
    private String name;

    @Schema(description = "提示词")
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

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图标背景色")
    private String iconBackground;

    @Schema(description = "发布者ID")
    private String publishedBy;

    @Schema(description = "发布者名称")
    private String publishedByName;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    @Schema(description = "状态")
    private String status;
}