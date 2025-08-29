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

package com.yonchain.ai.console.sys.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色权限响应
 *
 * @author Qoder
 * @since 1.0.0
 */
@Data
@Schema(description = "角色权限响应")
public class RolePermissionResponse {

    /**
     * 菜单ID列表
     */
    @Schema(description = "菜单ID列表")
    private List<String> menu_ids;

    /**
     * 操作权限ID列表
     */
    @Schema(description = "操作权限ID列表")
    private List<String> action_ids;
}