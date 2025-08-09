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

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainForbiddenException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.sys.RoleCategory;
import com.yonchain.ai.api.sys.enums.RoleType;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.sys.request.RoleRequest;
import com.yonchain.ai.console.sys.response.RoleResponse;
import com.yonchain.ai.web.request.PageRequest;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 角色控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@Tag(name = "角色管理", description = "角色相关接口")
@RestController
@RequestMapping("/roles")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    /**
     * 根据角色id获取角色信息
     *
     * @param roleId 角色id
     * @return
     */
    @GetMapping("/{roleId}")
    public ApiResponse<RoleResponse> getById(@PathVariable String roleId) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new YonchainResourceNotFoundException("角色不存在");
        }
        return ApiResponse.success(responseFactory.createRoleResponse(role));
    }

    /**
     * 分页查询角色
     *
     * @param name 角色名称
     * @return 角色分页列表
     */
    @GetMapping
    public PageResponse<RoleResponse> pageRoles(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "group_id", required = false) String groupId,
                                                PageRequest pageRequest) {
        //构建查询参数
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("name", name);
        queryParam.put("groupId", groupId);

        //分页查询
        Page<Role> page = roleService.pageRoles(this.getCurrentTenantId(), queryParam, pageRequest.getPage(), pageRequest.getLimit());

        PageResponse<RoleResponse> response = responseFactory.createRolePageResponse(page);
        response.getData().forEach(roleResponse -> {
            //获取角色组名称
            RoleGroup group = roleService.getRoleGroupById(roleResponse.getGroupId());
            if (group != null) {
                roleResponse.setGroupName(group.getName());
            }
        });
        return response;
    }

    /**
     * 创建角色信息
     *
     * @param roleRequest 角色信息
     * @return 保存后的角色信息
     */
    @PostMapping
    public ApiResponse<String> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        Role role = new DefaultRole();
        role.setId(UUID.randomUUID().toString());
        role.setTenantId(this.getCurrentTenantId());
        role.setCreatedBy(this.getCurrentUserId());
        role.setCategory(RoleCategory.BUSINESS);

        //从请求中获取菜单填充
        this.populateRoleFromRequest(role, roleRequest);

        // 创建角色
        roleService.createRole(role);

        return ApiResponse.success(role.getId());
    }


    /**
     * 更新角色信息
     *
     * @param roleId      角色信息
     * @param roleRequest 角色信息
     * @return 保存后的角色信息
     */
    @PutMapping("/{roleId}")
    public ApiResponse<Void> updateRole(@PathVariable String roleId, @Valid @RequestBody RoleRequest roleRequest) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setUpdatedBy(this.getCurrentUserId());

        //从请求中获取菜单填充
        this.populateRoleFromRequest(role, roleRequest);

        roleService.updateRole(role);
        return ApiResponse.success();
    }


    /**
     * 批量删除角色
     *
     * @param ids 角色id列表
     * @return 保存后的角色信息
     */
    @DeleteMapping
    public ApiResponse<Void> deleteRole(@RequestBody List<String> ids) {
        //系统角色不能删除
        if (roleService.getSystemRoleCount(this.getCurrentTenantId(), ids) > 0) {
            throw new YonchainForbiddenException("系统角色不能删除");
        }

        roleService.deleteRoleByIds(ids);
        return ApiResponse.success();
    }

    /**
     * 为角色分配菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 操作结果
     */
    @PostMapping("/{roleId}/menus")
    public ApiResponse<Void> assignRoleMenus(@PathVariable String roleId, @RequestBody List<String> menuIds) {
        menuService.assignRoleMenus(roleId, menuIds);
        return ApiResponse.success();
    }

    /**
     * 获取角色菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{roleId}/menus")
    public ApiResponse<List<String>> getRoleMenus(@PathVariable String roleId) {
        return ApiResponse.success(menuService.getMenusByRoleId(roleId));
    }

    /**
     * 查询租户下角色列表
     *
     * @return 角色分页列表
     */
    @GetMapping("/list")
    public ListResponse<RoleResponse> getRoles() {
        //查询列表
        List<Role> roles = roleService.getRoleList(this.getCurrentTenantId(), new HashMap<>());

        //构建响应对象
        ListResponse<RoleResponse> response = responseFactory.createRoleListResponse(roles);

        response.setData(response.getData().stream()
                //将超级管理员角色排除
                .filter(role -> !role.getCode().equals(RoleType.OWNER.getValue()))
                .peek(roleResponse -> {
                    //获取角色组名称
                    RoleGroup group = roleService.getRoleGroupById(roleResponse.getGroupId());
                    if (group != null) {
                        roleResponse.setGroupName(group.getName());
                    }
                })
                .toList());
        return response;
    }


    /**
     * 根据参数判断记录是否存在
     *
     * @return 角色组列表
     */
    @GetMapping("/exist")
    public ApiResponse<Boolean> isExist(@RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "code", required = false) String code) {
        Map<String, Object> queryParam = new HashMap<>();
        if (StringUtils.hasText(name)) {
            //后缀_eq代表等于
            queryParam.put("name_eq", name);
        }
        if (StringUtils.hasText(code)) {
            //后缀_eq代表等于
            queryParam.put("code_eq", code);
        }

        List<Role> roles = roleService.getRoleList(this.getCurrentTenantId(), queryParam);

        return ApiResponse.success(!roles.isEmpty());
    }

    /**
     * 从请求对象中获取数据填充角色对象
     *
     * @param role    要填充的角色对象
     * @param request 包含角色数据的请求对象
     */
    private void populateRoleFromRequest(Role role, RoleRequest request) {
        // 填充角色名称
        if (request.isNameSet()) {
            role.setName(request.getName());
        }

        // 填充角色编码
        if (request.isCodeSet()) {
            role.setCode(request.getCode());
        }

        if (request.isGroupIdSet()) {
            role.setGroupId(request.getGroupId());
        }

        // 填充角色描述
        if (request.isDescriptionSet()) {
            role.setDescription(request.getDescription());
        }
    }
}

