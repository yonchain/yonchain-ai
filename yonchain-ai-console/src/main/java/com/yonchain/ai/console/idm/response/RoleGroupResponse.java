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

package com.yonchain.ai.console.idm.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色组响应
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
@Schema(description = "角色组响应")
public class RoleGroupResponse {

    /**
     * 角色组ID
     */
    @Schema(description = "角色组ID")
    private String id;

    /**
     * 角色组名称
     */
    @Schema(description = "角色组名称")
    private String name;

    /**
     * 角色组类别(0-系统角色组，1-业务角色组)
     */
    @Schema(description = "角色组类别(0-系统角色组，1-业务角色组)")
    private String category;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private String createdBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private String updatedBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

}
