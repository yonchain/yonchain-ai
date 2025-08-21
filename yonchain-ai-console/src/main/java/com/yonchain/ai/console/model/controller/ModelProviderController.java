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

package com.yonchain.ai.console.model.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.model.DefaultModelProvider;
import com.yonchain.ai.api.model.ModelProvider;
import com.yonchain.ai.api.model.ModelService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.model.request.ModelProviderQueryRequest;
import com.yonchain.ai.console.model.request.ModelProviderRequest;
import com.yonchain.ai.console.model.response.ModelProviderResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 模型提供商控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@Tag(name = "模型提供商管理", description = "模型提供商相关接口")
@RestController
@RequestMapping("/model-providers")
public class ModelProviderController extends BaseController {

    @Autowired
    private ModelService modelService;

    /**
     * 获取模型提供商详情
     *
     * @param id 模型提供商ID
     * @return 模型提供商响应
     */
    @Operation(summary = "获取模型提供商详情", description = "根据ID获取模型提供商详细信息")
    @GetMapping("/{id}")
    public ModelProviderResponse getModelProviderById(
            @Parameter(description = "模型提供商ID", required = true, in = ParameterIn.PATH)
            @PathVariable String id) {
        ModelProvider provider = modelService.getProviderById(id);
        if (provider == null) {
            throw new YonchainIllegalStateException("模型提供商不存在");
        }
        return responseFactory.createModelProviderResponse(provider);
    }

    /**
     * 分页查询模型提供商
     *
     * @param request 查询请求
     * @return 分页响应
     */
    @Operation(summary = "分页查询模型提供商", description = "根据条件分页查询模型提供商列表")
    @GetMapping
    public PageResponse<ModelProviderResponse> pageModelProviders(ModelProviderQueryRequest request) {

        //构建查询条件
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("name", request.getName());

        //分页查询
        Page<ModelProvider> providers = modelService.pageProviders(getCurrentTenantId(), queryParam, request.getPageNum(), request.getPageSize());

        return responseFactory.createModelProviderPageResponse(providers);
    }

    /**
     * 分页查询模型提供商
     *
     * @param request 查询请求
     * @return 分页响应
     */
    @Operation(summary = "分页查询模型提供商", description = "根据条件分页查询模型提供商列表")
    @GetMapping("/list")
    public ListResponse<ModelProviderResponse> listModelProviders(ModelProviderQueryRequest request) {

        //构建查询条件
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("name", request.getName());

        //查询列表
       // List<ModelProvider> providers = modelService.getProviderByTenantId(getCurrentTenantId());

        return responseFactory.createModelProviderListResponse(new ArrayList<>());
    }

    /**
     * 创建模型提供商
     *
     * @param request 创建请求
     * @return 创建的模型提供商
     */
    @Operation(summary = "创建模型提供商", description = "创建一个新的模型提供商")
    @PostMapping
    public ModelProviderResponse createModelProvider(@Valid @RequestBody ModelProviderRequest request) {

        // 创建新的模型提供商
        ModelProvider provider = new DefaultModelProvider();
        provider.setId(UUID.randomUUID().toString());
        provider.setTenantId(getCurrentTenantId());
        provider.setCreatedAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());

        this.populateProviderFromRequest(provider, request);

        modelService.createProvider(provider);

        return responseFactory.createModelProviderResponse(provider);
    }

    /**
     * 更新模型提供商
     *
     * @param request 更新请求
     * @return 操作结果
     */
    @Operation(summary = "更新模型提供商", description = "更新指定ID的模型提供商信息")
    @PutMapping("{id}")
    public ApiResponse<Void> updateModelProvider(@PathVariable String id, @Valid @RequestBody ModelProviderRequest request) {

        // 检查提供商是否存在
        ModelProvider provider = modelService.getProviderById(id);
        if (provider == null) {
            throw new YonchainResourceNotFoundException("提供商不存在");
        }

        this.populateProviderFromRequest(provider, request);

        provider.setUpdatedAt(LocalDateTime.now());


        modelService.updateProvider(provider);
        return ApiResponse.success();
    }

    /**
     * 删除模型提供商
     *
     * @param id 模型提供商ID
     * @return 操作结果
     */
    @Operation(summary = "删除模型提供商", description = "删除指定ID的模型提供商")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteModelProvider(
            @Parameter(description = "模型提供商ID", required = true, in = ParameterIn.PATH)
            @PathVariable String id) {
        modelService.deleteProvider(id);
        return ApiResponse.success();
    }


    private void populateProviderFromRequest(ModelProvider modelProvider, ModelProviderRequest request) {
        modelProvider.setProviderName(request.getProviderName());
      /*  modelProvider.setDescription(request.getDescription());
        modelProvider.setIcon(request.getIcon());
        modelProvider.setApiBaseUrl(request.getApiBaseUrl());*/
/*        provider.setAuthType(request.getAuthType());
        provider.setConfigSchema(request.getConfigSchema());
        provider.setIsSystem(request.getIsSystem());
        provider.setIsValid(request.getIsValid());*/

    }
}
