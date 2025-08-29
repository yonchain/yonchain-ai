/*
 * Copyright (c) 2024 Dify4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.agent.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.api.agent.Agent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 应用实体类
 *
 * @author Cgy
 * @since 2024-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AgentEntity implements Agent {

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
    private String modelId;

    /**
     * 开场白
     */
    private String welcomeMessage;

    /**
     * 知识库ID列表（JSON格式，数据库存储）
     */
    private String knowledgeId;

    /**
     * 插件ID列表（JSON格式，数据库存储）
     */
    private String pluginId;

    /**
     * MCP配置（JSON格式，数据库存储）
     */
    private String mcp;

    /**
     * 工作流ID
     */
    private String workflowId;

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
    public List<String> getModelIds() {
        if (!StringUtils.hasText(modelId)) {
            return new ArrayList<>();
        }
        return Arrays.asList(modelId.split(","));
    }

    @Override
    public void setModelIds(List<String> modelIds) {
        if (CollectionUtils.isEmpty(modelIds)) {
            this.modelId = null;
        } else {
            this.modelId = String.join(",", modelIds);
        }
    }

    @Override
    public List<String> getKnowledgeIds() {
        if (!StringUtils.hasText(knowledgeId)) {
            return new ArrayList<>();
        }
        return Arrays.asList(knowledgeId.split(","));
    }

    @Override
    public void setKnowledgeIds(List<String> knowledgeIds) {
        if (CollectionUtils.isEmpty(knowledgeIds)) {
            this.knowledgeId = null;
        } else {
            this.knowledgeId = String.join(",", knowledgeIds);
        }
    }

    @Override
    public List<String> getPluginIds() {
        if (!StringUtils.hasText(pluginId)) {
            return new ArrayList<>();
        }
        return Arrays.asList(pluginId.split(","));
    }

    @Override
    public void setPluginIds(List<String> pluginIds) {
        if (CollectionUtils.isEmpty(pluginIds)) {
            this.pluginId = null;
        } else {
            this.pluginId = String.join(",", pluginIds);
        }
    }

    @Override
    public List<String> getMcps() {
        if (!StringUtils.hasText(mcp)) {
            return new ArrayList<>();
        }
        return Arrays.asList(mcp.split(","));
    }

    @Override
    public void setMcps(List<String> mcps) {
        if (CollectionUtils.isEmpty(mcps)) {
            this.mcp = null;
        } else {
            this.mcp = String.join(",", mcps);
        }
    }

    @Override
    public List<String> getWorkflowIds() {
        if (!StringUtils.hasText(workflowId)) {
            return new ArrayList<>();
        }
        return Arrays.asList(workflowId.split(","));
    }

    @Override
    public void setWorkflowIds(List<String> workflowIds) {
        if (CollectionUtils.isEmpty(workflowIds)) {
            this.workflowId = null;
        } else {
            this.workflowId = String.join(",", workflowIds);
        }
    }
}
