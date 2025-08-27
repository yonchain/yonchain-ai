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
 * 智能体发布请求接口
 *
 * @author Cgy
 * @since 2024-08-27
 */
public interface AgentPublishRequest {

    /**
     * 获取提示词
     */
    String getPrompt();

    /**
     * 获取模型ID
     */
    String getModelId();

    /**
     * 获取开场白
     */
    String getWelcomeMessage();

    /**
     * 获取知识库ID列表
     */
    List<String> getKnowledgeBaseIds();

    /**
     * 获取插件ID列表
     */
    List<String> getPluginIds();

    /**
     * 获取MCP配置
     */
    Map<String, Object> getMcpConfig();

    /**
     * 获取工作流ID
     */
    String getWorkflowId();

    /**
     * 获取智能体名称
     */
    String getName();

    /**
     * 获取智能体描述
     */
    String getDescription();

    /**
     * 获取图标
     */
    String getIcon();

    /**
     * 获取图标背景色
     */
    String getIconBackground();

    /**
     * 获取角色ID列表
     */
    List<String> getRoleIds();
}