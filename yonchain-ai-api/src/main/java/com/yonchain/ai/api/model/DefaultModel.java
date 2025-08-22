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
 * 模型信息默认实现类
 * 与YAML模型配置文件保持一致
 *
 * @author chengy
 * @since 1.0.0
 */
public class DefaultModel implements ModelInfo {
    /**
     * 模型ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 模型唯一标识符
     */
    private String code;

    /**
     * 模型显示名称
     */
    private String name;

    /**
     * 模型详细描述
     */
    private String description;

    /**
     * 模型图标
     */
    private String icon;

    /**
     * 所属提供商代码
     */
    private String provider;

    /**
     * 模型类型
     */
    private String type;

    /**
     * 模型版本号
     */
    private String version;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 模型支持的功能列表
     */
    private List<String> capabilities;

    /**
     * 模型配置参数定义
     */
    private List<ModelConfigItem> configSchema;

    /**
     * 是否已启用标识（租户级别）
     */
    private Boolean enabled;

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
    public String getProvider() {
        return provider;
    }

    @Override
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
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
    public List<String> getCapabilities() {
        return capabilities;
    }

    @Override
    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public List<ModelConfigItem> getConfigSchema() {
        return configSchema;
    }

    @Override
    public void setConfigSchema(List<ModelConfigItem> configSchema) {
        this.configSchema = configSchema;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
