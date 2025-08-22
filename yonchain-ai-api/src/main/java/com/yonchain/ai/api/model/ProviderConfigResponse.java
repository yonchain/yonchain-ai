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

import java.util.List;

/**
 * 提供商配置响应
 * 
 * @author Cgy
 * @since 1.0.0
 */
public class ProviderConfigResponse {
    
    /**
     * 提供商编码
     */
    private String code;
    
    /**
     * 提供商名称
     */
    private String name;
    
    /**
     * 提供商描述
     */
    private String description;
    
    /**
     * 提供商图标
     */
    private String icon;
    
    /**
     * 支持的模型类型
     */
    private List<String> supportedModelTypes;
    
    /**
     * 配置项列表
     */
    private List<ModelProviderConfigItem> configItems;
    
    /**
     * 整体配置状态
     */
    private Boolean configured;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 配置完整性百分比（0-100）
     */
    private Integer completeness;
    
    /**
     * 最后更新时间
     */
    private String lastUpdated;

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getCompleteness() {
        return completeness;
    }

    public void setCompleteness(Integer completeness) {
        this.completeness = completeness;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
    }

    public List<ModelProviderConfigItem> getConfigItems() {
        return configItems;
    }

    public void setConfigItems(List<ModelProviderConfigItem> configItems) {
        this.configItems = configItems;
    }

    public List<String> getSupportedModelTypes() {
        return supportedModelTypes;
    }

    public void setSupportedModelTypes(List<String> supportedModelTypes) {
        this.supportedModelTypes = supportedModelTypes;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}