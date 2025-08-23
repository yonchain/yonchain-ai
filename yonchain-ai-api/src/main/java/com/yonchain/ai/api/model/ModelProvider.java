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
 * 模型提供商接口
 * 与YAML提供商配置文件保持一致
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface ModelProvider {

    /**
     * 获取主键ID
     *
     * @return 主键ID
     */
    String getId();

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    void setId(String id);

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    String getTenantId();

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    void setTenantId(String tenantId);

    /**
     * 获取提供商唯一标识码
     *
     * @return 提供商代码
     */
    String getCode();

    /**
     * 设置提供商唯一标识码
     *
     * @param code 提供商代码
     */
    void setCode(String code);

    /**
     * 获取提供商显示名称
     *
     * @return 提供商名称
     */
    String getName();

    /**
     * 设置提供商显示名称
     *
     * @param name 提供商名称
     */
    void setName(String name);

    /**
     * 获取提供商简要描述信息
     *
     * @return 提供商描述
     */
    String getDescription();

    /**
     * 设置提供商简要描述信息
     *
     * @param description 提供商描述
     */
    void setDescription(String description);

    /**
     * 获取提供商图标
     *
     * @return 提供商图标
     */
    String getIcon();

    /**
     * 设置提供商图标
     *
     * @param icon 提供商图标
     */
    void setIcon(String icon);

    /**
     * 获取排序权重
     *
     * @return 排序权重
     */
    Integer getSortOrder();

    /**
     * 设置排序权重
     *
     * @param sortOrder 排序权重
     */
    void setSortOrder(Integer sortOrder);

    /**
     * 获取该提供商支持的模型类型列表
     *
     * @return 支持的模型类型列表
     */
    List<String> getSupportedModelTypes();

    /**
     * 设置该提供商支持的模型类型列表
     *
     * @param supportedModelTypes 支持的模型类型列表
     */
    void setSupportedModelTypes(List<String> supportedModelTypes);

    /**
     * 获取提供商配置参数的JSON Schema定义
     *
     * @return 配置参数Schema
     */
    List<ModelConfigItem> getConfigSchemas();

    /**
     * 设置提供商配置参数的JSON Schema定义
     *
     * @param configSchemas 配置参数Schema
     */
    void setConfigSchemas(List<ModelConfigItem> configSchemas);

    /**
     * 获取提供商支持的能力配置
     *
     * @return 能力配置
     */
    ModelProviderCapabilities getCapabilities();

    /**
     * 设置提供商支持的能力配置
     *
     * @param capabilities 能力配置
     */
    void setCapabilities(ModelProviderCapabilities capabilities);

    /**
     * 获取租户是否已启用该提供商
     *
     * @return 是否已启用
     */
    Boolean getEnabled();

    /**
     * 设置租户是否已启用该提供商
     *
     * @param enabled 是否已启用
     */
    void setEnabled(Boolean enabled);


}
