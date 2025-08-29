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
import java.util.List;

/**
 * 应用实体类
 *
 * @author Cgy
 * @since 2024-01-20
 */
public class DefaultAgent implements Agent {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标背景
     */
    private String iconBackground;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 图标类型
     */
    private String iconType;

    /**
     * 创建者ID
     */
    private String createdBy;

    /**
     * 更新者ID
     */
    private String updatedBy;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 模型ID
     */
    private List<String> modelIds;

    /**
     * 开场白
     */
    private String welcomeMessage;

    /**
     * 知识库ID列表（JSON格式，数据库存储）
     */
    private List<String> knowledgeIds;

    /**
     * 插件ID列表（JSON格式，数据库存储）
     */
    private List<String> pluginIds;

    /**
     * MCP配置（JSON格式，数据库存储）
     */
    private List<String> mcps;

    /**
     * 工作流ID
     */
    private List<String> workflowIds;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 发布者ID
     */
    private String publishedBy;

    /**
     * 发布版本
     */
    private Integer publishVersion;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
    public String getIconType() {
        return iconType;
    }

    @Override
    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
    public List<String> getModelIds() {
        return modelIds;
    }

    public void setModelIds(List<String> modelIds) {
        this.modelIds = modelIds;
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
    public List<String> getKnowledgeIds() {
        return knowledgeIds;
    }

    @Override
    public void setKnowledgeIds(List<String> knowledgeIds) {
        this.knowledgeIds = knowledgeIds;
    }

    @Override
    public List<String> getPluginIds() {
        return pluginIds;
    }

    @Override
    public void setPluginIds(List<String> pluginIds) {
        this.pluginIds = pluginIds;
    }

    @Override
    public List<String> getMcps() {
        return mcps;
    }

    @Override
    public void setMcps(List<String> mcps) {
        this.mcps = mcps;
    }

    @Override
    public List<String> getWorkflowIds() {
        return workflowIds;
    }

    @Override
    public void setWorkflowIds(List<String> workflowIds) {
        this.workflowIds = workflowIds;
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
    public String getPublishedBy() {
        return publishedBy;
    }

    @Override
    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    @Override
    public Integer getPublishVersion() {
        return publishVersion;
    }

    @Override
    public void setPublishVersion(Integer publishVersion) {
        this.publishVersion = publishVersion;
    }
}
