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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * 应用模式
     */
    private String mode;

    /**
     * 应用提供商
     */
    private String provider;

    /**
     * 应用秘钥
     */
    private String apiKey;

    /**
     * 基础URL
     */
    private String baseUrl;

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
     * 配置信息
     */
    private Map<String, Object> config;
    
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
    private String knowledgeBaseId;
    
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
    
    /**
     * 知识库ID列表（内存对象，不存储到数据库）
     */
    private transient List<String> knowledgeBaseIdsList;
    
    /**
     * 插件ID列表（内存对象，不存储到数据库）
     */
    private transient List<String> pluginIdsList;
    
    /**
     * MCP配置（内存对象，不存储到数据库）
     */
    private transient Map<String, Object> mcpConfigMap;
    
    @Override
    public List<String> getKnowledgeBaseIds() {
        if (knowledgeBaseIdsList == null && knowledgeBaseId != null) {
            try {
                knowledgeBaseIdsList = new ObjectMapper().readValue(knowledgeBaseId,
                    new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析知识库ID列表失败", e);
            }
        }
        return knowledgeBaseIdsList;
    }
    
    @Override
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIdsList = knowledgeBaseIds;
        if (knowledgeBaseIds != null) {
            try {
                this.knowledgeBaseId = new ObjectMapper().writeValueAsString(knowledgeBaseIds);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化知识库ID列表失败", e);
            }
        } else {
            this.knowledgeBaseId = null;
        }
    }
    
    @Override
    public List<String> getPluginIds() {
        if (pluginIdsList == null && pluginId != null) {
            try {
                pluginIdsList = new ObjectMapper().readValue(pluginId,
                    new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析插件ID列表失败", e);
            }
        }
        return pluginIdsList;
    }
    
    @Override
    public void setPluginIds(List<String> pluginIds) {
        this.pluginIdsList = pluginIds;
        if (pluginIds != null) {
            try {
                this.pluginId = new ObjectMapper().writeValueAsString(pluginIds);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化插件ID列表失败", e);
            }
        } else {
            this.pluginId = null;
        }
    }
    
    @Override
    public Map<String, Object> getMcpConfig() {
        if (mcpConfigMap == null && mcp != null) {
            try {
                mcpConfigMap = new ObjectMapper().readValue(mcp,
                    new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析MCP配置失败", e);
            }
        }
        return mcpConfigMap;
    }
    
    @Override
    public void setMcpConfig(Map<String, Object> mcp) {
        this.mcpConfigMap = mcp;
        if (mcp != null) {
            try {
                this.mcp = new ObjectMapper().writeValueAsString(mcp);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化MCP配置失败", e);
            }
        } else {
            this.mcp = null;
        }
    }
}
