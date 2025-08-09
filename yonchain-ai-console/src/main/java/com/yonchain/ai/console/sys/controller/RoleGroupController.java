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

package com.yonchain.ai.console.sys.controller;

import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.DefaultRoleGroup;
import com.yonchain.ai.api.sys.RoleGroup;
import com.yonchain.ai.api.sys.RoleService;
import com.yonchain.ai.api.sys.enums.RoleGroupCategory;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.sys.request.RoleGroupQueryRequest;
import com.yonchain.ai.console.sys.request.RoleGroupRequest;
import com.yonchain.ai.console.sys.response.RoleGroupResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 角色组控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@Tag(name = "角色组管理", description = "角色组相关接口")
@RestController
@RequestMapping("/role-groups")
public class RoleGroupController extends BaseController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色组
     *
     * @param request 查询条件
     * @return 分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询角色组")
    public ListResponse<RoleGroupResponse> list(RoleGroupQueryRequest request) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", request.getName());

        List<RoleGroup> groups = roleService.getRoleGroups(params);
        return responseFactory.buildRoleGroupListResponse(groups);
    }

    /**
     * 创建角色组
     *
     * @param request 创建请求
     * @return 创建的角色组信息
     */
    @PostMapping
    @Operation(summary = "创建角色组")
    public RoleGroupResponse createRoleGroup(@Valid @RequestBody RoleGroupRequest request) {
        RoleGroup roleGroup = new DefaultRoleGroup();
        roleGroup.setId(UUID.randomUUID().toString());
        roleGroup.setTenantId(this.getCurrentTenantId());
        roleGroup.setName(request.getName());
        roleGroup.setCategory(RoleGroupCategory.BUSINESS.getValue());
        roleGroup.setCreatedBy(getCurrentUserId());
        //创建角色组
        roleService.createRoleGroup(roleGroup);

        return responseFactory.buildRoleGroupResponse(roleGroup);
    }

    /**
     * 更新角色组
     *
     * @param id      角色组ID
     * @param request 更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新角色组")
    public ApiResponse<Void> updateRoleGroup(
            @Parameter(description = "角色组ID") @PathVariable String id,
            @Valid @RequestBody RoleGroupRequest request) {
        RoleGroup roleGroup = roleService.getRoleGroupById(id);
        if (roleGroup == null) {
            throw new YonchainResourceNotFoundException("角色组不存在");
        }
        roleGroup.setName(request.getName());
        roleGroup.setUpdatedBy(getCurrentUserId());
        roleService.updateRoleGroup(roleGroup);
        return ApiResponse.success();
    }

    /**
     * 删除角色组
     *
     * @param id 角色组ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色组")
    public ApiResponse<Void> deleteRoleGroupById(@Parameter(description = "角色组ID") @PathVariable String id) {
        RoleGroup roleGroup = roleService.getRoleGroupById(id);
        if (roleGroup == null) {
            throw new YonchainResourceNotFoundException("角色组不存在");
        }
        roleService.deleteRoleGroupById(id);
        return ApiResponse.success();
    }

}
