/*
 * Copyright (c) 2024 Dify4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.dify.mapper;

import com.yonchain.ai.api.sys.Role;
import com.yonchain.ai.app.entity.AppRoleEntiy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用角色关联数据访问层接口
 *
 * @author Cgy
 * @since 2024-01-20
 */
@Mapper
public interface DifyAppRoleMapper {

    /**
     * 通过ID查询单个应用角色关联
     *
     * @param id 关联ID
     * @return 应用角色关联信息
     */
    AppRoleEntiy selectById(@Param("id") String id);

    /**
     * 查询应用角色关联列表
     *
     * @param appId 应用ID
     * @return 应用角色关联列表
     */
    List<AppRoleEntiy> selectByAppId(@Param("appId") String appId);

    /**
     * 查询角色关联的应用列表
     *
     * @param roleId 角色ID
     * @return 应用角色关联列表
     */
    List<AppRoleEntiy> selectByRoleId(@Param("roleId") String roleId);

    /**
     * 新增应用角色关联
     *
     * @param appRole 应用角色关联信息
     * @return 影响行数
     */
    int insert(AppRoleEntiy appRole);

    /**
     * 修改应用角色关联
     *
     * @param appRole 应用角色关联信息
     * @return 影响行数
     */
    int update(AppRoleEntiy appRole);

    /**
     * 通过ID删除应用角色关联
     *
     * @param id 关联ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 通过应用ID删除所有关联角色
     *
     * @param appId 应用ID
     * @return 影响行数
     */
    int deleteByAppId(@Param("appId") String appId);

    /**
     * 通过角色ID删除所有关联应用
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") String roleId);

    /**
     * 批量新增应用角色关联
     *
     * @param appId   应用ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int batchInsert(@Param("appId") String appId, @Param("roleIds") List<String> roleIds);

    /**
     * 通过应用ID查询角色列表
     *
     * @param appId 应用ID
     * @return 角色列表
     */
    List<Role> selectRoleByAppId(@Param("appId") String appId);
}
