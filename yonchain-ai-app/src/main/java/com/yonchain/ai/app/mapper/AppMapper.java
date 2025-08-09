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
package com.yonchain.ai.app.mapper;

import com.yonchain.ai.api.app.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 应用数据访问层接口
 *
 * @author Cgy
 * @since 2024-01-20
 */
@Mapper
public interface AppMapper {

    /**
     * 通过ID查询单个应用
     *
     * @param id 应用ID
     * @return 应用信息
     */
    Application selectById(@Param("id") String id);

    /**
     * 查询应用列表
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 应用列表
     */
    List<Application> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 新增应用
     *
     * @param app 应用信息
     * @return 影响行数
     */
    int insert(Application app);

    /**
     * 修改应用
     *
     * @param app 应用信息
     * @return 影响行数
     */
    int update(Application app);

    /**
     * 通过ID删除应用
     *
     * @param id 应用ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除应用
     *
     * @param ids 应用ID列表
     * @return 影响行数
     */
    int deleteByIds(@Param("ids") List<String> ids);
}
