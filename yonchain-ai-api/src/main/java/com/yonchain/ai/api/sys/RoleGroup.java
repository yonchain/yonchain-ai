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

package com.yonchain.ai.api.sys;

import java.time.LocalDateTime;

/**
 * 角色组实体
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface RoleGroup {

    /**
     * 获取角色组ID
     */
    String getId();

    /**
     * 设置角色组ID
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
     * 获取角色组名称
     */
    String getName();

    /**
     * 设置角色组名称
     */
    void setName(String name);

    /**
     * 获取角色组类别(0-系统角色组，1-业务角色组)
     *
     * @return 角色组类别(0-系统角色组，1-业务角色组)
     */
    String getCategory();

    /**
     * 设置角色组类别(0-系统角色组，1-业务角色组)
     *
     * @param category 角色组类别(0-系统角色组，1-业务角色组)
     */
    void setCategory(String category);

    /**
     * 获取创建人ID
     */
    String getCreatedBy();

    /**
     * 设置创建人ID
     */
    void setCreatedBy(String createdBy);

    /**
     * 获取创建时间
     */
    LocalDateTime getCreatedAt();

    /**
     * 设置创建时间
     */
    void setCreatedAt(LocalDateTime createdAt);

    /**
     * 获取更新人ID
     */
    String getUpdatedBy();

    /**
     * 设置更新人ID
     */
    void setUpdatedBy(String updatedBy);

    /**
     * 获取更新时间
     */
    LocalDateTime getUpdatedAt();

    /**
     * 设置更新时间
     */
    void setUpdatedAt(LocalDateTime updatedAt);
}
