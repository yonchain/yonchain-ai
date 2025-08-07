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

package com.yonchain.ai.idm.entity;

import com.yonchain.ai.api.idm.RoleGroup;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色组实体
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class RoleGroupEntity implements RoleGroup {

    /**
     * 角色组ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 角色组名称
     */
    private String name;

    /**
     * 角色类型
     */
    private String category;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人ID
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
