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

package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.ModelProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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
    ModelProvider selectById(@Param("id") String id);

    /**
     * 查询模型提供商列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 模型提供商列表
     */
    List<ModelProvider> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> queryParam);

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
    int insert(ModelProvider provider);

    /**
     * 更新模型提供商
     *
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    int update(ModelProvider provider);

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
     * 根据提供商名称查询模型提供商
     *
     * @param tenantId 租户ID
     * @param providerName 模型提供商名称
     * @return 模型提供商实体
     */
    ModelProvider selectByProviderName(@Param("tenantId") String tenantId, @Param("providerName") String providerName);

    /**
     * 根据提供商类型查询模型提供商
     *
     * @param tenantId 租户ID
     * @param providerType 模型提供商类型
     * @return 模型提供商列表
     */
    List<ModelProvider> selectByProviderType(@Param("tenantId") String tenantId, @Param("providerType") String providerType);

    /**
     * 更新配额使用情况
     *
     * @param id 模型提供商ID
     * @param quotaUsed 已用配额
     * @param lastUsed 最后使用时间
     * @return 影响行数
     */
    int updateQuotaUsage(@Param("id") String id, @Param("quotaUsed") Long quotaUsed, @Param("lastUsed") LocalDateTime lastUsed);

}
