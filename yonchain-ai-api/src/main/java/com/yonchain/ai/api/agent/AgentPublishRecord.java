/*
 * Copyright 2025-2028 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yonchain.ai.api.agent;

import java.time.LocalDateTime;

/**
 * 智能体发布记录接口
 *
 * @author Cgy
 * @since 2024-08-27
 */
public interface AgentPublishRecord {

    /**
     * 获取主键ID
     */
    String getId();

    /**
     * 设置主键ID
     */
    void setId(String id);

    /**
     * 获取智能体ID
     */
    String getAgentId();

    /**
     * 设置智能体ID
     */
    void setAgentId(String agentId);

    /**
     * 获取租户ID
     */
    String getTenantId();

    /**
     * 设置租户ID
     */
    void setTenantId(String tenantId);

    /**
     * 获取版本号
     */
    Integer getVersion();

    /**
     * 设置版本号
     */
    void setVersion(Integer version);

    /**
     * 获取智能体名称
     */
    String getName();

    /**
     * 设置智能体名称
     */
    void setName(String name);

    /**
     * 获取提示词
     */
    String getPrompt();

    /**
     * 设置提示词
     */
    void setPrompt(String prompt);

    /**
     * 获取模型ID
     */
    String getModelId();

    /**
     * 设置模型ID
     */
    void setModelId(String modelId);

    /**
     * 获取开场白
     */
    String getWelcomeMessage();

    /**
     * 设置开场白
     */
    void setWelcomeMessage(String welcomeMessage);

    /**
     * 获取知识库ID列表
     */
    String getKnowledgeBaseIds();

    /**
     * 设置知识库ID列表
     */
    void setKnowledgeBaseIds(String knowledgeIds);

    /**
     * 获取插件ID列表
     */
    String getPluginIds();

    /**
     * 设置插件ID列表
     */
    void setPluginIds(String pluginIds);

    /**
     * 获取MCP配置
     */
    String getMcpConfig();

    /**
     * 设置MCP配置
     */
    void setMcpConfig(String mcpConfig);

    /**
     * 获取工作流ID
     */
    String getWorkflowId();

    /**
     * 设置工作流ID
     */
    void setWorkflowId(String workflowId);

    /**
     * 获取描述
     */
    String getDescription();

    /**
     * 设置描述
     */
    void setDescription(String description);

    /**
     * 获取图标
     */
    String getIcon();

    /**
     * 设置图标
     */
    void setIcon(String icon);

    /**
     * 获取图标背景色
     */
    String getIconBackground();

    /**
     * 设置图标背景色
     */
    void setIconBackground(String iconBackground);

    /**
     * 获取发布者ID
     */
    String getPublishedBy();

    /**
     * 设置发布者ID
     */
    void setPublishedBy(String publishedBy);

    /**
     * 获取发布时间
     */
    LocalDateTime getPublishedAt();

    /**
     * 设置发布时间
     */
    void setPublishedAt(LocalDateTime publishedAt);

    /**
     * 获取状态
     */
    String getStatus();

    /**
     * 设置状态
     */
    void setStatus(String status);
}