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

import com.yonchain.ai.api.sys.RoleGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色组数据访问接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface RoleGroupMapper {

    /**
     * 根据ID查询角色组
     *
     * @param id 角色组ID
     * @return 角色组实体
     */
    RoleGroup selectById(@Param("id") String id);

    /**
     * 查询角色组列表
     *
     * @param params 查询参数
     * @return 角色组列表
     */
    List<RoleGroup> selectList(@Param("params") Map<String, Object> params);

    /**
     * 新增角色组
     *
     * @param roleGroup 角色组实体
     * @return 影响行数
     */
    int insert(RoleGroup roleGroup);

    /**
     * 更新角色组
     *
     * @param roleGroup 角色组实体
     * @return 影响行数
     */
    int update(RoleGroup roleGroup);

    /**
     * 删除角色组
     *
     * @param id 角色组ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);
}
