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
 * 智能体发布记录默认实现
 *
 * @author Cgy
 * @since 2024-08-27
 */
public class DefaultAgentPublishRecord implements AgentPublishRecord {

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
     * 版本号
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
     * 知识库ID列表
     */
    private String knowledgeBaseIds;

    /**
     * 插件ID列表
     */
    private String pluginIds;

    /**
     * MCP配置
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
     * 状态
     */
    private String status;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPrompt() {
        return prompt;
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String getModelId() {
        return modelId;
    }

    @Override
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    @Override
    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @Override
    public String getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    @Override
    public void setKnowledgeBaseIds(String knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }

    @Override
    public String getPluginIds() {
        return pluginIds;
    }

    @Override
    public void setPluginIds(String pluginIds) {
        this.pluginIds = pluginIds;
    }

    @Override
    public String getMcpConfig() {
        return mcpConfig;
    }

    @Override
    public void setMcpConfig(String mcpConfig) {
        this.mcpConfig = mcpConfig;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getIconBackground() {
        return iconBackground;
    }

    @Override
    public void setIconBackground(String iconBackground) {
        this.iconBackground = iconBackground;
    }

    @Override
    public String getPublishedBy() {
        return publishedBy;
    }

    @Override
    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    @Override
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    @Override
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}