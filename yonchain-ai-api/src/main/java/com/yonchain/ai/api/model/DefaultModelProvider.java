/*
 * Copyright 2025-2028 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except* You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless require in compliance with the License.
 d by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.api.model;


import java.time.LocalDateTime;

/**
 * 模型提供商实体类
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
     * 提供商名称
     */
    private String providerName;

    /**
     * 提供商类型
     */
    private String providerType;

    /**
     * 加密配置
     */
    private String encryptedConfig;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsed;

    /**
     * 配额类型
     */
    private String quotaType;

    /**
     * 配额限制
     */
    private Long quotaLimit;

    /**
     * 已用配额
     */
    private Long quotaUsed;

    /**
     * 是否有效
     */
    private Boolean isValid;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 获取主键ID
     *
     * @return 主键ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * 设置提供商名称
     *
     * @param providerName 提供商名称
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * 获取提供商类型
     *
     * @return 提供商类型
     */
    public String getProviderType() {
        return providerType;
    }

    /**
     * 设置提供商类型
     *
     * @param providerType 提供商类型
     */
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    /**
     * 获取加密配置
     *
     * @return 加密配置
     */
    public String getEncryptedConfig() {
        return encryptedConfig;
    }

    /**
     * 设置加密配置
     *
     * @param encryptedConfig 加密配置
     */
    public void setEncryptedConfig(String encryptedConfig) {
        this.encryptedConfig = encryptedConfig;
    }

    /**
     * 获取最后使用时间
     *
     * @return 最后使用时间
     */
    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    /**
     * 设置最后使用时间
     *
     * @param lastUsed 最后使用时间
     */
    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    /**
     * 获取配额类型
     *
     * @return 配额类型
     */
    public String getQuotaType() {
        return quotaType;
    }

    /**
     * 设置配额类型
     *
     * @param quotaType 配额类型
     */
    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    /**
     * 获取配额限制
     *
     * @return 配额限制
     */
    public Long getQuotaLimit() {
        return quotaLimit;
    }

    /**
     * 设置配额限制
     *
     * @param quotaLimit 配额限制
     */
    public void setQuotaLimit(Long quotaLimit) {
        this.quotaLimit = quotaLimit;
    }

    /**
     * 获取已用配额
     *
     * @return 已用配额
     */
    public Long getQuotaUsed() {
        return quotaUsed;
    }

    /**
     * 设置已用配额
     *
     * @param quotaUsed 已用配额
     */
    public void setQuotaUsed(Long quotaUsed) {
        this.quotaUsed = quotaUsed;
    }

    /**
     * 获取是否有效
     *
     * @return 是否有效
     */
    public Boolean getIsValid() {
        return isValid;
    }

    /**
     * 设置是否有效
     *
     * @param isValid 是否有效
     */
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
