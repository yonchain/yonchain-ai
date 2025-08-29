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
import java.util.Map;

/**
 * 智能体 接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface Agent {

    /**
     * 获取主键ID
     */
    String getId();

    /**
     * 设置主键ID
     */
    void setId(String id);

    /**
     * 获取租户ID
     */
    String getTenantId();

    /**
     * 设置租户ID
     */
    void setTenantId(String tenantId);

    /**
     * 获取应用名称
     */
    String getName();

    /**
     * 设置应用名称
     */
    void setName(String name);

    /**
     * 获取图标
     */
    String getIcon();

    /**
     * 设置图标
     */
    void setIcon(String icon);

    /**
     * 获取图标背景
     */
    String getIconBackground();

    /**
     * 设置图标背景
     */
    void setIconBackground(String iconBackground);

    /**
     * 获取状态
     */
    String getStatus();

    /**
     * 设置状态
     */
    void setStatus(String status);

    /**
     * 获取创建时间
     */
    LocalDateTime getCreatedAt();

    /**
     * 设置创建时间
     */
    void setCreatedAt(LocalDateTime createdAt);

    /**
     * 获取更新时间
     */
    LocalDateTime getUpdatedAt();

    /**
     * 设置更新时间
     */
    void setUpdatedAt(LocalDateTime updatedAt);

    /**
     * 获取应用描述
     */
    String getDescription();

    /**
     * 设置应用描述
     */
    void setDescription(String description);

    /**
     * 获取图标类型
     */
    String getIconType();

    /**
     * 设置图标类型
     */
    void setIconType(String iconType);

    /**
     * 获取创建者ID
     */
    String getCreatedBy();

    /**
     * 设置创建者ID
     */
    void setCreatedBy(String createdBy);

    /**
     * 获取更新者ID
     */
    String getUpdatedBy();

    /**
     * 设置更新者ID
     */
    void setUpdatedBy(String updatedBy);

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
    List<String> getModelIds();

    /**
     * 设置模型ID
     */
    void setModelIds(List<String> modelIds);

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
    List<String> getKnowledgeIds();

    /**
     * 设置知识库ID列表
     */
    void setKnowledgeIds(List<String> knowledgeIds);

    /**
     * 获取插件ID列表
     */
    List<String> getPluginIds();

    /**
     * 设置插件ID列表
     */
    void setPluginIds(List<String> pluginIds);

    /**
     * 获取MCP列表
     */
    List<String> getMcps();

    /**
     * 设置MCP列表
     */
    void setMcps(List<String> mcps);

    /**
     * 获取工作流ID列表
     */
    List<String> getWorkflowIds();

    /**
     * 设置工作流ID列表
     */
    void setWorkflowIds(List<String> workflowIds);

    /**
     * 获取发布时间
     */
    LocalDateTime getPublishedAt();

    /**
     * 设置发布时间
     */
    void setPublishedAt(LocalDateTime publishedAt);

    /**
     * 获取发布者ID
     */
    String getPublishedBy();

    /**
     * 设置发布者ID
     */
    void setPublishedBy(String publishedBy);

    /**
     * 获取发布版本
     */
    Integer getPublishVersion();

    /**
     * 设置发布版本
     */
    void setPublishVersion(Integer publishVersion);
}
