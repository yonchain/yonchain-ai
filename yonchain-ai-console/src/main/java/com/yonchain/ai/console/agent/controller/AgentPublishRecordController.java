package com.yonchain.ai.console.agent.controller;

import com.yonchain.ai.api.agent.AgentPublishRecord;
import com.yonchain.ai.api.agent.AgentService;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.agent.response.AgentPublishRecordResponse;
import com.yonchain.ai.web.request.PageRequest;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 智能体发布记录控制器
 *
 * @author Cgy
 * @since 2024-08-27
 */
@RestController
@RequestMapping("/agents/{agentId}/publish-records")
@Tag(name = "智能体发布记录", description = "智能体发布记录相关接口")
public class AgentPublishRecordController extends BaseController {

    /**
     * 智能体发布记录查询请求
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "智能体发布记录查询请求")
    public static class AgentPublishRecordQueryRequest extends PageRequest {
        @Schema(description = "智能体名称")
        private String name;

        @Schema(description = "发布状态")
        private String status;
    }

    @Autowired
    private AgentService agentService;

    /**
     * 获取智能体最新发布记录
     *
     * @param agentId 智能体ID
     * @return 发布记录响应
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新发布记录", description = "获取智能体最新的发布记录")
    public AgentPublishRecordResponse getLatestPublishRecord(@Parameter(description = "智能体ID") @PathVariable String agentId) {
        AgentPublishRecord record = agentService.getLatestAgentPublishRecord(agentId);
        if (record == null) {
            throw new YonchainResourceNotFoundException("PUBLISH_RECORD_NOT_FOUND", "发布记录未找到");
        }
        return this.responseFactory.createAgentPublishRecordResponse(record);
    }

    /**
     * 获取智能体所有发布记录
     *
     * @param agentId 智能体ID
     * @return 发布记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有发布记录", description = "获取智能体所有的发布记录")
    public List<AgentPublishRecordResponse> getAllPublishRecords(@Parameter(description = "智能体ID") @PathVariable String agentId) {
        List<AgentPublishRecord> records = agentService.getAgentPublishRecords(agentId);
        return records.stream()
                .map(pagePublishRecord -> responseFactory.createAgentPublishRecordResponse(pagePublishRecord))
                .collect(Collectors.toList());
    }

    /**
     * 分页查询发布记录
     *
     * @param agentId 智能体ID
     * @param request 查询请求
     * @return 分页发布记录列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询发布记录", description = "分页查询智能体发布记录")
    public PageResponse<AgentPublishRecordResponse> pagePublishRecords(
            @Parameter(description = "智能体ID") @PathVariable String agentId,
            AgentPublishRecordQueryRequest request) {

        // 构建查询参数
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("agentId", agentId);

        if (StringUtils.hasText(request.getName())) {
            queryParam.put("name", request.getName());
        }

        if (StringUtils.hasText(request.getStatus())) {
            queryParam.put("status", request.getStatus());
        }

        // 分页查询
        Page<AgentPublishRecord> records = agentService.getAgentPublishRecordsByPage(
                this.getCurrentTenantId(), queryParam, request.getPageNum(), request.getPageSize());

        // 转换为响应对象
        return this.responseFactory.createAgentPublishRecordPageResponse(records);

    }

    /**
     * 获取指定版本的发布记录
     *
     * @param id 记录ID
     * @return 发布记录响应
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取指定发布记录", description = "根据ID获取智能体发布记录")
    public AgentPublishRecordResponse getPublishRecordById(
            @Parameter(description = "记录ID") @PathVariable String id) {

        // 这里需要实现根据ID获取发布记录的方法
        // 暂时使用获取所有记录然后过滤的方式
        AgentPublishRecord record = agentService.getAgentPublishRecordById(id);

        return this.responseFactory.createAgentPublishRecordResponse(record);
    }

}
