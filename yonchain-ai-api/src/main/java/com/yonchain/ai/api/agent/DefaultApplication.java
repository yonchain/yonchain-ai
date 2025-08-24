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

import java.time.LocalDateTime;

/**
 * 应用实体类
 *
 * @author Cgy
 * @since 2024-01-20
 */
public class DefaultApplication implements Application {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 应用提供商
     */
    private String provider;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用模式
     */
    private String mode;

    /**
     * 应用秘钥
     */
    private String apiKey;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标背景
     */
    private String iconBackground;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 图标类型
     */
    private String iconType;

    /**
     * 创建者ID
     */
    private String createdBy;

    /**
     * 更新者ID
     */
    private String updatedBy;

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
     * 获取应用名称
     *
     * @return 应用名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置应用名称
     *
     * @param name 应用名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取应用模式
     *
     * @return 应用模式
     */
    public String getMode() {
        return mode;
    }

    /**
     * 设置应用模式
     *
     * @param mode 应用模式
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * 获取应用提供商
     *
     * @return 应用提供商
     */
    public String getProvider() {
        return provider;
    }

    /**
     * 设置应用提供商
     *
     * @param provider 应用提供商
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * 获取应用秘钥
     *
     * @return 应用秘钥
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 设置应用秘钥
     *
     * @param apiKey 应用秘钥
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 获取基础URL
     *
     * @return 基础URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置基础URL
     *
     * @param baseUrl 基础URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 获取图标
     *
     * @return 图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图标
     *
     * @param icon 图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取图标背景
     *
     * @return 图标背景
     */
    public String getIconBackground() {
        return iconBackground;
    }

    /**
     * 设置图标背景
     *
     * @param iconBackground 图标背景
     */
    public void setIconBackground(String iconBackground) {
        this.iconBackground = iconBackground;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
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

    /**
     * 获取应用描述
     *
     * @return 应用描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置应用描述
     *
     * @param description 应用描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取图标类型
     *
     * @return 图标类型
     */
    public String getIconType() {
        return iconType;
    }

    /**
     * 设置图标类型
     *
     * @param iconType 图标类型
     */
    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    /**
     * 获取创建者ID
     *
     * @return 创建者ID
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置创建者ID
     *
     * @param createdBy 创建者ID
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取更新者ID
     *
     * @return 更新者ID
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 设置更新者ID
     *
     * @param updatedBy 更新者ID
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

}
