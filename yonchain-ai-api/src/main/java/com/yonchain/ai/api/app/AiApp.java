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
 * 应用 接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface AiApp {

    /**
     * 获取主键ID
     */
    String getId();

    /**
     * 设置主键ID
     */
    void setId(String id);

    /**
     * 获取租户ID
     */
    String getTenantId();

    /**
     * 设置租户ID
     */
    void setTenantId(String tenantId);

    /**
     * 获取应用名称
     */
    String getName();

    /**
     * 设置应用名称
     */
    void setName(String name);

    /**
     * 获取应用模式
     */
    String getMode();

    /**
     * 设置应用模式
     */
    void setMode(String mode);

    /**
     * 获取图标
     */
    String getIcon();

    /**
     * 设置图标
     */
    void setIcon(String icon);

    /**
     * 获取图标背景
     */
    String getIconBackground();

    /**
     * 设置图标背景
     */
    void setIconBackground(String iconBackground);

    /**
     * 获取应用模型配置ID
     */
    String getAppModelConfigId();

    /**
     * 设置应用模型配置ID
     */
    void setAppModelConfigId(String appModelConfigId);

    /**
     * 获取状态
     */
    String getStatus();

    /**
     * 设置状态
     */
    void setStatus(String status);

    /**
     * 获取是否启用站点
     */
    Boolean getEnableSite();

    /**
     * 设置是否启用站点
     */
    void setEnableSite(Boolean enableSite);

    /**
     * 获取是否启用API
     */
    Boolean getEnableApi();

    /**
     * 设置是否启用API
     */
    void setEnableApi(Boolean enableApi);

    /**
     * 获取API每分钟请求限制
     */
    Integer getApiRpm();

    /**
     * 设置API每分钟请求限制
     */
    void setApiRpm(Integer apiRpm);

    /**
     * 获取API每小时请求限制
     */
    Integer getApiRph();

    /**
     * 设置API每小时请求限制
     */
    void setApiRph(Integer apiRph);

    /**
     * 获取是否为演示应用
     */
    Boolean getIsDemo();

    /**
     * 设置是否为演示应用
     */
    void setIsDemo(Boolean isDemo);

    /**
     * 获取是否公开
     */
    Boolean getIsPublic();

    /**
     * 设置是否公开
     */
    void setIsPublic(Boolean isPublic);

    /**
     * 获取创建时间
     */
    LocalDateTime getCreatedAt();

    /**
     * 设置创建时间
     */
    void setCreatedAt(LocalDateTime createdAt);

    /**
     * 获取更新时间
     */
    LocalDateTime getUpdatedAt();

    /**
     * 设置更新时间
     */
    void setUpdatedAt(LocalDateTime updatedAt);

    /**
     * 获取是否为通用应用
     */
    Boolean getIsUniversal();

    /**
     * 设置是否为通用应用
     */
    void setIsUniversal(Boolean isUniversal);

    /**
     * 获取工作流ID
     */
    String getWorkflowId();

    /**
     * 设置工作流ID
     */
    void setWorkflowId(String workflowId);

    /**
     * 获取应用描述
     */
    String getDescription();

    /**
     * 设置应用描述
     */
    void setDescription(String description);

    /**
     * 获取追踪信息
     */
    String getTracing();

    /**
     * 设置追踪信息
     */
    void setTracing(String tracing);

    /**
     * 获取最大活跃请求数
     */
    Integer getMaxActiveRequests();

    /**
     * 设置最大活跃请求数
     */
    void setMaxActiveRequests(Integer maxActiveRequests);

    /**
     * 获取图标类型
     */
    String getIconType();

    /**
     * 设置图标类型
     */
    void setIconType(String iconType);

    /**
     * 获取创建者ID
     */
    String getCreatedBy();

    /**
     * 设置创建者ID
     */
    void setCreatedBy(String createdBy);

    /**
     * 获取更新者ID
     */
    String getUpdatedBy();

    /**
     * 设置更新者ID
     */
    void setUpdatedBy(String updatedBy);

    /**
     * 获取是否使用图标作为回答图标
     */
    Boolean getUseIconAsAnswerIcon();

    /**
     * 设置是否使用图标作为回答图标
     */
    void setUseIconAsAnswerIcon(Boolean useIconAsAnswerIcon);
}
