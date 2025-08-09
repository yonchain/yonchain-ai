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

package com.yonchain.ai.sys.service;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.sys.Role;
import com.yonchain.ai.api.sys.RoleGroup;
import com.yonchain.ai.api.sys.RoleService;
import com.yonchain.ai.sys.mapper.RoleGroupMapper;
import com.yonchain.ai.sys.mapper.RoleMapper;
import com.yonchain.ai.util.PageUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色服务实现类
 *
 * @author Cgy
 * @since 1.0.0
 */
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    private final RoleGroupMapper roleGroupMapper;

    public RoleServiceImpl(RoleMapper roleMapper,RoleGroupMapper roleGroupMapper) {
        this.roleMapper = roleMapper;
        this.roleGroupMapper = roleGroupMapper;
    }

    @Override
    public Role getRoleById(String id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role getRole(String tenantId, Map<String, Object> queryParam) {
        return this.roleMapper.selectOne(tenantId, queryParam);
    }

    @Override
    public Page<Role> pageRoles(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Role> roles = roleMapper.selectList(tenantId, queryParam);

        return PageUtil.convert(roles);
    }

    @Override
    public void createRole(Role role) {
        role.setStatus("normal");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        this.roleMapper.insert(role);
    }

    @Override
    public void updateRole(Role role) {
        role.setUpdatedAt(LocalDateTime.now());
        this.roleMapper.updateById(role);
    }

    @Override
    public void deleteRoleByIds(List<String> ids) {
        roleMapper.deleteByIds(ids);
    }

    @Override
    public List<Role> getRoleList(String tenantId, Map<String, Object> queryParam) {
        return roleMapper.selectList(tenantId, queryParam);
    }

    @Override
    public int getSystemRoleCount(String tenantId, List<String> roleIds) {
        // 查询符合条件的角色列表
        List<Role> systemRoles = this.getSystemRoles(tenantId);
        systemRoles = systemRoles.stream().filter(role -> roleIds.contains(role.getId())).toList();

        return systemRoles != null ? systemRoles.size() : 0;
    }

    @Override
    public List<Role> getSystemRoles(String tenantId) {
        // 使用批量查询优化性能
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("category", "0"); // 系统角色类别为"0"
        queryParam.put("code_ne", "owner"); // 排除"owner"超级管理员角色

        // 查询符合条件的角色列表
        return roleMapper.selectList(tenantId, queryParam);
    }

    @Override
    public RoleGroup getRoleGroupById(String roleGroupId) {
        return roleGroupMapper.selectById(roleGroupId);
    }

    @Override
    public List<RoleGroup> getRoleGroups(Map<String, Object> params) {
        return roleGroupMapper.selectList(params);
    }

    @Override
    public void createRoleGroup(RoleGroup roleGroup) {
        roleGroup.setCreatedAt(LocalDateTime.now());
        roleGroupMapper.insert(roleGroup);
    }

    @Override
    public void updateRoleGroup(RoleGroup roleGroup) {
        roleGroup.setUpdatedAt(LocalDateTime.now());
        roleGroupMapper.update(roleGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleGroupById(String roleGroupId) {
        roleGroupMapper.deleteById(roleGroupId);
    }

}
