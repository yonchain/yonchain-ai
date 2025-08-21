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

import java.time.LocalDateTime;

/**
 * 模型实体类
 *
 * @author chengy
 * @since 1.0.0
 */
public interface ModelInfo {

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
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    String getProviderName();

    /**
     * 设置提供商名称
     *
     * @param providerName 提供商名称
     */
    void setProviderName(String providerName);

    /**
     * 获取模型名称
     *
     * @return 模型名称
     */
    String getModelName();

    /**
     * 设置模型名称
     *
     * @param modelName 模型名称
     */
    void setModelName(String modelName);

    /**
     * 获取模型类型
     *
     * @return 模型类型
     */
    String getModelType();

    /**
     * 设置模型类型
     *
     * @param modelType 模型类型
     */
    void setModelType(String modelType);

    /**
     * 获取加密配置
     *
     * @return 加密配置
     */
    String getEncryptedConfig();

    /**
     * 设置加密配置
     *
     * @param encryptedConfig 加密配置
     */
    void setEncryptedConfig(String encryptedConfig);

    /**
     * 获取是否有效
     *
     * @return 是否有效
     */
    Boolean getIsValid();

    /**
     * 设置是否有效
     *
     * @param isValid 是否有效
     */
    void setIsValid(Boolean isValid);

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    LocalDateTime getCreatedAt();

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    void setCreatedAt(LocalDateTime createdAt);

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    LocalDateTime getUpdatedAt();

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    void setUpdatedAt(LocalDateTime updatedAt);
}
