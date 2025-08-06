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
     * 应用名称
     */
    private String name;

    /**
     * 应用模式
     */
    private String mode;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标背景
     */
    private String iconBackground;

    /**
     * 应用模型配置ID
     */
    private String appModelConfigId;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否启用站点
     */
    private Boolean enableSite;

    /**
     * 是否启用API
     */
    private Boolean enableApi;

    /**
     * API每分钟请求限制
     */
    private Integer apiRpm;

    /**
     * API每小时请求限制
     */
    private Integer apiRph;

    /**
     * 是否为演示应用
     */
    private Boolean isDemo;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否为通用应用
     */
    private Boolean isUniversal;

    /**
     * 工作流ID
     */
    private String workflowId;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 追踪信息
     */
    private String tracing;

    /**
     * 最大活跃请求数
     */
    private Integer maxActiveRequests;

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
     * 是否使用图标作为回答图标
     */
    private Boolean useIconAsAnswerIcon;

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
     * 获取应用模型配置ID
     *
     * @return 应用模型配置ID
     */
    public String getAppModelConfigId() {
        return appModelConfigId;
    }

    /**
     * 设置应用模型配置ID
     *
     * @param appModelConfigId 应用模型配置ID
     */
    public void setAppModelConfigId(String appModelConfigId) {
        this.appModelConfigId = appModelConfigId;
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
     * 获取是否启用站点
     *
     * @return 是否启用站点
     */
    public Boolean getEnableSite() {
        return enableSite;
    }

    /**
     * 设置是否启用站点
     *
     * @param enableSite 是否启用站点
     */
    public void setEnableSite(Boolean enableSite) {
        this.enableSite = enableSite;
    }

    /**
     * 获取是否启用API
     *
     * @return 是否启用API
     */
    public Boolean getEnableApi() {
        return enableApi;
    }

    /**
     * 设置是否启用API
     *
     * @param enableApi 是否启用API
     */
    public void setEnableApi(Boolean enableApi) {
        this.enableApi = enableApi;
    }

    /**
     * 获取API每分钟请求限制
     *
     * @return API每分钟请求限制
     */
    public Integer getApiRpm() {
        return apiRpm;
    }

    /**
     * 设置API每分钟请求限制
     *
     * @param apiRpm API每分钟请求限制
     */
    public void setApiRpm(Integer apiRpm) {
        this.apiRpm = apiRpm;
    }

    /**
     * 获取API每小时请求限制
     *
     * @return API每小时请求限制
     */
    public Integer getApiRph() {
        return apiRph;
    }

    /**
     * 设置API每小时请求限制
     *
     * @param apiRph API每小时请求限制
     */
    public void setApiRph(Integer apiRph) {
        this.apiRph = apiRph;
    }

    /**
     * 获取是否为演示应用
     *
     * @return 是否为演示应用
     */
    public Boolean getIsDemo() {
        return isDemo;
    }

    /**
     * 设置是否为演示应用
     *
     * @param isDemo 是否为演示应用
     */
    public void setIsDemo(Boolean isDemo) {
        this.isDemo = isDemo;
    }

    /**
     * 获取是否公开
     *
     * @return 是否公开
     */
    public Boolean getIsPublic() {
        return isPublic;
    }

    /**
     * 设置是否公开
     *
     * @param isPublic 是否公开
     */
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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
     * 获取是否为通用应用
     *
     * @return 是否为通用应用
     */
    public Boolean getIsUniversal() {
        return isUniversal;
    }

    /**
     * 设置是否为通用应用
     *
     * @param isUniversal 是否为通用应用
     */
    public void setIsUniversal(Boolean isUniversal) {
        this.isUniversal = isUniversal;
    }

    /**
     * 获取工作流ID
     *
     * @return 工作流ID
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 设置工作流ID
     *
     * @param workflowId 工作流ID
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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
     * 获取追踪信息
     *
     * @return 追踪信息
     */
    public String getTracing() {
        return tracing;
    }

    /**
     * 设置追踪信息
     *
     * @param tracing 追踪信息
     */
    public void setTracing(String tracing) {
        this.tracing = tracing;
    }

    /**
     * 获取最大活跃请求数
     *
     * @return 最大活跃请求数
     */
    public Integer getMaxActiveRequests() {
        return maxActiveRequests;
    }

    /**
     * 设置最大活跃请求数
     *
     * @param maxActiveRequests 最大活跃请求数
     */
    public void setMaxActiveRequests(Integer maxActiveRequests) {
        this.maxActiveRequests = maxActiveRequests;
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

    /**
     * 获取是否使用图标作为回答图标
     *
     * @return 是否使用图标作为回答图标
     */
    public Boolean getUseIconAsAnswerIcon() {
        return useIconAsAnswerIcon;
    }

    /**
     * 设置是否使用图标作为回答图标
     *
     * @param useIconAsAnswerIcon 是否使用图标作为回答图标
     */
    public void setUseIconAsAnswerIcon(Boolean useIconAsAnswerIcon) {
        this.useIconAsAnswerIcon = useIconAsAnswerIcon;
    }
}
