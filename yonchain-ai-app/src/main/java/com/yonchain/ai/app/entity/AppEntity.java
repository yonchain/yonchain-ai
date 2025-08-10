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
     * 应用提供商
     */
    private String provider;

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

}
