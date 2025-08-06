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
package com.yonchain.ai.api.app;

/**
 * 应用模式 枚举
 *
 * @author Cgy
 */
public enum AppMode {

    /**
     * 工作流模式，面向单轮自动化任务的编排工作流
     */
    WORKFLOW("workflow"),
    
    /**
     * 文本生成应用模式，用于文本生成任务的 AI 助手
     */
    COMPLETION("completion"),
    
    /**
     * 聊天助手模式，简单配置即可构建基于 LLM 的对话机器人
     */
    CHAT("chat"),
    
    /**
     * 聊天工作流模式，支持记忆的复杂多轮对话工作流
     */
    ADVANCED_CHAT("advanced-chat"),
    
    /**
     * 智能体模式，具备推理与自主工具调用的智能助手
     */
    AGENT_CHAT("agent-chat");

    private final String value;

    /**
     * 构造函数
     *
     * @param value 模式对应的字符串值
     */
    AppMode(String value) {
        this.value = value;
    }

    /**
     * 获取模式对应的字符串值
     *
     * @return 模式字符串值
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的AppMode枚举
     *
     * @param mode 模式字符串值
     * @return 对应的AppMode枚举
     * @throws IllegalArgumentException 如果传入的模式字符串无效
     */
    public static AppMode valueOfMode(String mode) {
        for (AppMode appMode : values()) {
            if (appMode.getValue().equals(mode)) {
                return appMode;
            }
        }
        throw new IllegalArgumentException("Invalid AppMode: " + mode);
    }
}