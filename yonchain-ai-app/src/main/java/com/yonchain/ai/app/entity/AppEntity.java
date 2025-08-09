/*
 * Copyright (c) 2024 Dify4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.app.entity;

import com.yonchain.ai.api.app.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 应用实体类
 *
 * @author Cgy
 * @since 2024-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppEntity implements Application {

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
}
