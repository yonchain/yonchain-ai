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
import java.util.Map;

/**
 * 模型提供商默认实现类
 * 与YAML提供商配置文件保持一致
 *
 * @author Cgy
 * @since 1.0.0
 */
public class DefaultModelProvider implements ModelProvider {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 提供商唯一标识码
     */
    private String code;

    /**
     * 提供商显示名称
     */
    private String name;

    /**
     * 提供商简要描述信息
     */
    private String description;

    /**
     * 提供商图标
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 该提供商支持的模型类型列表
     */
    private List<String> supportedModelTypes;

    /**
     * 提供商配置参数的JSON Schema定义
     */
    private Map<String, Object> configSchema;

    /**
     * 提供商支持的能力配置
     */
    private Map<String, Object> capabilities;

    // Getters and Setters

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
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
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
    public Integer getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public List<String> getSupportedModelTypes() {
        return supportedModelTypes;
    }

    @Override
    public void setSupportedModelTypes(List<String> supportedModelTypes) {
        this.supportedModelTypes = supportedModelTypes;
    }

    @Override
    public Map<String, Object> getConfigSchema() {
        return configSchema;
    }

    @Override
    public void setConfigSchema(Map<String, Object> configSchema) {
        this.configSchema = configSchema;
    }

    @Override
    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    @Override
    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
}
