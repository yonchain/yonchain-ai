package com.yonchain.ai.agent.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能体发布记录实体
 *
 * @author Cgy
 * @since 2024-08-27
 */
@Data
public class AgentPublishRecord {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 智能体ID
     */
    private String agentId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 发布版本号
     */
    private Integer version;

    /**
     * 智能体名称
     */
    private String name;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 开场白
     */
    private String welcomeMessage;

    /**
     * 知识库ID列表，JSON格式存储
     */
    private String knowledgeIds;

    /**
     * 插件ID列表，JSON格式存储
     */
    private String pluginIds;

    /**
     * MCP配置，JSON格式存储
     */
    private String mcpConfig;

    /**
     * 工作流ID
     */
    private String workflowId;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标背景色
     */
    private String iconBackground;

    /**
     * 发布者ID
     */
    private String publishedBy;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 发布状态：success-成功，failed-失败
     */
    private String status;
}