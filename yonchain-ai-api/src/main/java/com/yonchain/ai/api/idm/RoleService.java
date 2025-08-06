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

package com.yonchain.ai.api.idm;

import com.yonchain.ai.api.common.Page;

import java.util.List;
import java.util.Map;

/**
 * 角色服务接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 根据ID获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色实体
     */
    Role getRoleById(String roleId);

    /**
     * 获取角色
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 角色
     */
    Role getRole(String tenantId, Map<String, Object> queryParam);

    /**
     * 分页获取角色列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 分页角色列表
     */
    Page<Role> pageRoles(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 创建角色
     *
     * @param role 角色实体
     */
    void createRole(Role role);

    /**
     * 更新角色信息
     *
     * @param role 角色实体
     * @return 是否更新成功
     */
    void updateRole(Role role);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 是否删除成功
     */
    void deleteRoleByIds(List<String> ids);

    /**
     * 获取角色列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 角色列表
     */
    List<Role> getRoleList(String tenantId, Map<String, Object> queryParam);

    /**
     * 获取系统角色数量
     *
     * @param tenantId 租户ID
     * @param roleIds  角色ID列表
     * @return 系统角色数量
     */
    int getSystemRoleCount(String tenantId, List<String> roleIds);

    /**
     * 获取系统角色
     *
     * @param tenantId 租户ID
     */
    List<Role> getSystemRoles(String tenantId);

    /**
     * 获取角色组
     *
     * @param roleGroupId 角色组ID
     * @return 角色组
     */
    RoleGroup getRoleGroupById(String roleGroupId);

    /**
     * 获取角色组列表
     *
     * @param params 查询参数
     * @return 角色组列表
     */
    List<RoleGroup> getRoleGroups(Map<String, Object> params);


    /**
     * 获取角色组
     *
     * @param roleGroup 角色组
     */
    void createRoleGroup(RoleGroup roleGroup);

    /**
     * 更新角色组
     *
     * @param roleGroup 角色组
     */
    void updateRoleGroup(RoleGroup roleGroup);

    /**
     * 删除角色组
     roleGroupId
     * @param roleGroupId 角色组ID
     */
    void deleteRoleGroupById(String roleGroupId);
}