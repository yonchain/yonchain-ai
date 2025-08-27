package com.yonchain.ai.console.agent.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能体发布记录查询请求
 *
 * @author Cgy
 * @since 2024-08-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "智能体发布记录查询请求")
public class AgentPublishRecordQueryRequest extends PageRequest {

    @Schema(description = "智能体名称")
    private String name;

    @Schema(description = "发布状态")
    private String status;
}