package com.yonchain.ai.console.agent.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 智能体发布请求
 *
 * @author Cgy
 * @since 2024-08-24
 */
@Data
@Slf4j
@Schema(description = "智能体发布请求")
public class AgentPublishRequest {

    @NotBlank(message = "提示词不能为空")
    @Schema(description = "提示词", required = true)
    private String prompt;

    @NotEmpty(message = "模型ID不能为空")
    @Schema(description = "模型ID", required = true)
    @JsonProperty("modelIds")
    private List<String> modelIds;
    
    // 添加setter方法用于调试
    public void setModelIds(List<String> modelIds) {
        log.info("设置modelIds: {}", modelIds);
        this.modelIds = modelIds;
    }
    
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