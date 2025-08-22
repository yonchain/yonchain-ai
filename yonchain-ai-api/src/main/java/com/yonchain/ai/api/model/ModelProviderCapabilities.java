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

package com.yonchain.ai.api.model;

/**
 * 提供商能力配置
 * 
 * @author Cgy
 * @since 1.0.0
 */
public class ModelProviderCapabilities {
    
    /**
     * 是否支持流式输出
     */
    private Boolean streaming;
    
    /**
     * 是否支持函数调用
     */
    private Boolean functionCalling;
    
    /**
     * 是否支持视觉理解
     */
    private Boolean vision;
    
    /**
     * 是否支持工具使用
     */
    private Boolean toolUse;
    
    /**
     * 最大上下文长度
     */
    private Integer maxContextLength;
    
    /**
     * 最大输出长度
     */
    private Integer maxOutputLength;
    
    /**
     * 支持的输入模态
     */
    private String[] inputModalities;
    
    /**
     * 支持的输出模态
     */
    private String[] outputModalities;

    public Boolean getStreaming() {
        return streaming;
    }

    public void setStreaming(Boolean streaming) {
        this.streaming = streaming;
    }

    public Boolean getFunctionCalling() {
        return functionCalling;
    }

    public void setFunctionCalling(Boolean functionCalling) {
        this.functionCalling = functionCalling;
    }

    public Boolean getVision() {
        return vision;
    }

    public void setVision(Boolean vision) {
        this.vision = vision;
    }

    public Boolean getToolUse() {
        return toolUse;
    }

    public void setToolUse(Boolean toolUse) {
        this.toolUse = toolUse;
    }

    public Integer getMaxContextLength() {
        return maxContextLength;
    }

    public void setMaxContextLength(Integer maxContextLength) {
        this.maxContextLength = maxContextLength;
    }

    public Integer getMaxOutputLength() {
        return maxOutputLength;
    }

    public void setMaxOutputLength(Integer maxOutputLength) {
        this.maxOutputLength = maxOutputLength;
    }

    public String[] getInputModalities() {
        return inputModalities;
    }

    public void setInputModalities(String[] inputModalities) {
        this.inputModalities = inputModalities;
    }

    public String[] getOutputModalities() {
        return outputModalities;
    }

    public void setOutputModalities(String[] outputModalities) {
        this.outputModalities = outputModalities;
    }
}