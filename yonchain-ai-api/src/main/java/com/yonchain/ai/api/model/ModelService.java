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
     * 获取模型列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 模型列表
     */
    List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam);

    /**
     * 获取提供商列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 提供商列表
     */
    List<ModelProviderInfo> getProviders(String tenantId, Map<String, Object> queryParam);

    /**
     * 获取租户的模型提供商配置
     *
     * @param tenantId     租户ID
     * @param providerCode 提供商代码
     * @return 租户提供商配置
     */
    ProviderConfigResponse getProviderConfig(String tenantId, String providerCode);

    /**
     * 获取租户的模型配置
     * @param provider
     * @param modelCode
     * @return
     */
    ModelInfo getModel(String provider, String modelCode);

    /**
     * 保存租户的模型提供商配置
     *
     * @param tenantId     租户ID
     * @param providerCode 提供商代码
     * @param config       配置信息
     */
    void saveProviderConfig(String tenantId, String providerCode, Map<String, Object> config);

/*
    */
/**
     * 获取租户的模型配置
     *
     * @param tenantId  租户ID
     * @param modelCode 模型代码
     * @return 租户模型配置
     *//*

    ModelInfo getModelConfig(String tenantId, String modelCode);
*/

    /**
     * 设置模型状态
     *
     * @param provider  提供商
     * @param modelCode 模型编码
     * @param enabled   是否启用
     */
    void updateModelStatus(String tenantId, String provider, String modelCode, boolean enabled);

    /**
     * 保存租户的模型配置
     *
     * @param tenantId
     * @param modelInfo
     */
    void saveModelConfig(String tenantId, ModelInfo modelInfo);

    /**
     * 保存插件的提供商信息到数据库（用于可视化界面展示和配置）
     *
     * @param pluginId 插件ID
     * @param modelProvider 模型提供商
     */
    void saveProviderForUI(String pluginId, ModelProviderInfo modelProvider);

    /**
     * 保存插件的模型信息到数据库（用于可视化界面展示和配置）
     *
     * @param pluginId 插件ID
     * @param modelMetadataList 模型元数据列表
     * @param providerCode 提供商代码
     */
    void saveModelsForUI(String pluginId, List<Object> modelMetadataList, String providerCode);

    /**
     * 删除插件相关的所有数据
     *
     * @param pluginId 插件ID
     */
    void removePluginData(String pluginId);

}
