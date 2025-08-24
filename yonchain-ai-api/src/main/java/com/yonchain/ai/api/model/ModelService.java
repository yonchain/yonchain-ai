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

package com.yonchain.ai.api.model;

import com.yonchain.ai.api.common.Page;

import java.util.List;
import java.util.Map;

/**
 * 模型服务接口
 *
 * @author chengy
 * @since 1.0.0
 */
public interface ModelService {

    /**
     * 根据ID获取模型
     *
     * @param id 模型ID
     * @return 模型DTO
     */
    ModelInfo getModelById(String id);

    /**
     * 根据模型编码获取模型
     *
     * @param modelCode 模型编码
     * @return 模型
     */
    ModelInfo getModel(String provider, String modelCode);

    /**
     * 获取模型列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 模型列表
     */
    List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam);

    /**
     * 分页获取模型列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 模型分页列表
     */
    Page<ModelInfo> pageModels(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 获取提供商列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 提供商列表
     */
    List<ModelProvider> getProviders(String tenantId, Map<String, Object> queryParam);

    /**
     * 创建模型
     *
     * @param model 模型信息
     * @return 创建的模型
     */
    String createModel(ModelInfo model);

    /**
     * 更新模型
     *
     * @param model 模型信息
     * @return 更新后的模型
     */
    void updateModel(ModelInfo model);

    /**
     * 删除模型
     *
     * @param id 模型ID
     * @return 是否删除成功
     */
    void deleteModel(String id);


    /**
     * 根据ID获取模型提供商
     *
     * @param providerId 模型提供商ID
     * @return 模型提供商实体
     */
    ModelProvider getProviderById(String providerId);

    /**
     * 分页查询模型提供商
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 分页结果
     */
    Page<ModelProvider> pageProviders(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 获取模型提供商列表
     *
     * @param tenantId 租户ID
     * @return 模型提供商列表
     */
    List<ModelProvider> getProvidersByTenantId(String tenantId);

    /**
     * 创建模型提供商
     *
     * @param modelProvider 模型提供商
     * @return 创建的模型提供商
     */
    void createProvider(ModelProvider modelProvider);

    /**
     * 更新模型提供商
     *
     * @param modelProvider 模型提供商
     */
    void updateProvider(ModelProvider modelProvider);

    /**
     * 删除模型提供商
     *
     * @param providerId 模型提供商ID
     */
    void deleteProvider(String providerId);

    // ==================== 租户配置管理方法 ====================

    /**
     * 获取租户的模型提供商配置
     *
     * @param tenantId     租户ID
     * @param providerCode 提供商代码
     * @return 租户提供商配置
     */
    ProviderConfigResponse getProviderConfig(String tenantId, String providerCode);

    /**
     * 保存租户的模型提供商配置
     *
     * @param tenantId     租户ID
     * @param providerCode 提供商代码
     * @param config       配置信息
     */
    void saveProviderConfig(String tenantId, String providerCode, Map<String, Object> config);

    /**
     * 获取租户的模型配置
     *
     * @param tenantId  租户ID
     * @param modelCode 模型代码
     * @return 租户模型配置
     */
    ModelInfo getModelConfig(String tenantId, String modelCode);

    /**
     * 保存租户的模型配置
     *
     * @param tenantId  租户ID
     * @param modelCode 模型代码
     * @param config    配置信息
     */
    void saveModelConfig(String tenantId, ModelInfo modelInfo);

    /**
     * 设置模型状态
     *
     * @param provider  提供商
     * @param modelCode 模型编码
     * @param enabled   是否启用
     */
    void updateModelStatus(String tenantId, String provider, String modelCode, boolean enabled);
}
