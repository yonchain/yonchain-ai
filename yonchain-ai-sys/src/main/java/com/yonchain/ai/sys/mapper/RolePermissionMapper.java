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

package com.yonchain.ai.sys.mapper;

import com.yonchain.ai.sys.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色权限关联 Mapper 接口
 *
 * @author Qoder
 * @since 1.0.0
 */
public interface RolePermissionMapper {

    /**
     * 根据ID查询角色权限关联
     *
     * @param id 主键ID
     * @return 角色权限关联实体
     */
    RolePermissionEntity selectById(@Param("id") String id);

    /**
     * 查询角色权限关联列表
     *
     * @param params 查询参数
     * @return 角色权限关联列表
     */
    List<RolePermissionEntity> selectList(@Param("params") Map<String, Object> params);

    /**
     * 新增角色权限关联
     *
     * @param rolePermission 角色权限关联实体
     * @return 影响行数
     */
    int insert(RolePermissionEntity rolePermission);

    /**
     * 根据ID删除角色权限关联
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据角色ID删除关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") String roleId);


    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectPermissionsByRoleId(@Param("roleId") String roleId);

    /**
     * 批量插入角色权限关联
     *
     * @param roleId 角色ID
     * @param permissions 权限列表
     * @return 影响行数
     */
    int batchInsert(@Param("roleId") String roleId, @Param("permissions") List<String> permissions);

}