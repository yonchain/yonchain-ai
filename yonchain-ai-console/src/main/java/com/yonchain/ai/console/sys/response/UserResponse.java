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

import com.yonchain.ai.api.sys.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户响应
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
@Schema(description = "用户响应")
public class UserResponse {

    /**
     * 用户id
     */
    @Schema(description = "用户ID")
    private String id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String name;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 用户头像id
     */
    @Schema(description = "用户头像id")
    private String avatar;

    /**
     * 用户状态(0-禁用,1-正常)
     */
    @Schema(description = "用户状态(0-禁用,1-正常)")
    private String status;

    /**
     * 用户创建时间
     */
    @Schema(description = "用户创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    /**
     * 用户更新时间
     */
    @Schema(description = "用户更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastActiveAt;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<Role> roleList;

    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String avatarUrl;



}
