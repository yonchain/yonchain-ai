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

package com.yonchain.ai.business.mapper;

import com.yonchain.ai.business.entity.ModelProviderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模型提供商数据访问接口
 *
 * @author Cgy
 * @since 1.0.0
 */
@Mapper
public interface ModelProviderMapper {

    /**
     * 根据ID查询模型提供商
     *
     * @param id 模型提供商ID
     * @return 模型提供商实体
     */
    ModelProviderEntity selectById(@Param("id") String id);

    /**
     * 查询模型提供商列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 模型提供商列表
     */
    List<ModelProviderEntity> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> queryParam);

    /**
     * 计算符合条件的记录数
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 记录数
     */
    int count(@Param("tenantId") String tenantId, @Param("queryParam") Map<String, Object> queryParam);

    /**
     * 插入模型提供商
     *
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    int insert(ModelProviderEntity provider);

    /**
     * 更新模型提供商
     *
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    int update(ModelProviderEntity provider);

    /**
     * 删除模型提供商
     *
     * @param id 模型提供商ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除模型提供商
     *
     * @param ids 模型提供商ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<String> ids);

    /**
     * 根据提供商代码查询模型提供商
     *
     * @param tenantId 租户ID
     * @param providerCode 模型提供商代码
     * @return 模型提供商实体
     */
    ModelProviderEntity selectByProviderCode(@Param("tenantId") String tenantId, @Param("providerCode") String providerCode);

    /**
     * 根据启用状态查询模型提供商
     *
     * @param tenantId 租户ID
     * @param enabled 是否启用
     * @return 模型提供商列表
     */
    List<ModelProviderEntity> selectByEnabled(@Param("tenantId") String tenantId, @Param("enabled") Boolean enabled);

    // ========== 以下方法从ModelProviderConfigMapper迁移 ==========

    /**
     * 根据租户ID和提供商代码查询配置
     *
     * @param tenantId 租户ID
     * @param providerCode 提供商代码
     * @return 模型提供商实体
     */
    ModelProviderEntity selectByTenantAndCode(@Param("tenantId") String tenantId, @Param("providerCode") String providerCode);

    /**
     * 根据租户ID查询所有提供商配置
     *
     * @param tenantId 租户ID
     * @return 模型提供商列表
     */
    List<ModelProviderEntity> selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据租户ID删除所有配置
     *
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int deleteByTenantId(@Param("tenantId") String tenantId);

}
