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
 * 模型信息接口，定义模型的基本属性和配置
 * 与YAML模型配置文件保持一致
 *
 * @author chengy
 * @since 1.0.0
 */
public interface ModelInfo {

    /**
     * 获取模型ID
     *
     * @return 模型ID
     */
    String getId();

    /**
     * 设置模型ID
     *
     * @param id 模型ID
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
     * 获取模型唯一标识符
     *
     * @return 模型代码
     */
    String getCode();

    /**
     * 设置模型唯一标识符
     *
     * @param code 模型代码
     */
    void setCode(String code);

    /**
     * 获取模型显示名称
     *
     * @return 模型名称
     */
    String getName();

    /**
     * 设置模型显示名称
     *
     * @param name 模型名称
     */
    void setName(String name);

    /**
     * 获取模型详细描述
     *
     * @return 模型描述
     */
    String getDescription();

    /**
     * 设置模型详细描述
     *
     * @param description 模型描述
     */
    void setDescription(String description);

    /**
     * 获取模型图标
     *
     * @return 模型图标
     */
    String getIcon();

    /**
     * 设置模型图标
     *
     * @param icon 模型图标
     */
    void setIcon(String icon);

    /**
     * 获取所属提供商代码
     *
     * @return 提供商代码
     */
    String getProvider();

    /**
     * 设置所属提供商代码
     *
     * @param provider 提供商代码
     */
    void setProvider(String provider);

    /**
     * 获取模型类型：CHAT/COMPLETION/EMBEDDING/IMAGE
     *
     * @return 模型类型
     */
    String getType();

    /**
     * 设置模型类型
     *
     * @param type 模型类型
     */
    void setType(String type);

    /**
     * 获取模型版本号
     *
     * @return 版本号
     */
    String getVersion();

    /**
     * 设置模型版本号
     *
     * @param version 版本号
     */
    void setVersion(String version);

    /**
     * 获取排序权重，数值越小排序越靠前
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
     * 获取模型支持的功能列表（英文名）
     * 如：chat, completion, function_calling, analysis等
     *
     * @return 功能列表
     */
    List<String> getCapabilities();

    /**
     * 设置模型支持的功能列表
     *
     * @param capabilities 功能列表
     */
    void setCapabilities(List<String> capabilities);

    /**
     * 获取模型配置参数定义
     *
     * @return 配置参数定义
     */
    Map<String, Object> getConfigSchema();

    /**
     * 设置模型配置参数定义
     *
     * @param configSchema 配置参数定义
     */
    void setConfigSchema(Map<String, Object> configSchema);
}
