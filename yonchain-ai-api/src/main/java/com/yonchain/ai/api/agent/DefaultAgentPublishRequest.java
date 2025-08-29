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

import java.util.List;
import java.util.Map;

/**
 * 智能体发布请求默认实现
 *
 * @author Cgy
 * @since 2024-08-27
 */
public class DefaultAgentPublishRequest implements AgentPublishRequest {

    private String prompt;
    private String modelId;
    private String welcomeMessage;
    private List<String> knowledgeIds;
    private List<String> pluginIds;
    private Map<String, Object> mcpConfig;
    private String workflowId;
    private String name;
    private String description;
    private String icon;
    private String iconBackground;
    private List<String> roleIds;

    @Override
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @Override
    public List<String> getKnowledgeBaseIds() {
        return knowledgeIds;
    }

    public void setKnowledgeBaseIds(List<String> knowledgeIds) {
        this.knowledgeIds = knowledgeIds;
    }

    @Override
    public List<String> getPluginIds() {
        return pluginIds;
    }

    public void setPluginIds(List<String> pluginIds) {
        this.pluginIds = pluginIds;
    }

    @Override
    public Map<String, Object> getMcpConfig() {
        return mcpConfig;
    }

    public void setMcpConfig(Map<String, Object> mcpConfig) {
        this.mcpConfig = mcpConfig;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getIconBackground() {
        return iconBackground;
    }

    public void setIconBackground(String iconBackground) {
        this.iconBackground = iconBackground;
    }

    @Override
    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}