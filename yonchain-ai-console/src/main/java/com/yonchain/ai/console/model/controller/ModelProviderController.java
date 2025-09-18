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
 *//*


package com.yonchain.ai.console.model.controller;

import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.model.ModelProvider;
import com.yonchain.ai.api.model.ModelService;
import com.yonchain.ai.api.model.ProviderConfigResponse;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.model.request.ProviderConfigRequest;
import com.yonchain.ai.console.model.request.ModelProviderQueryRequest;
import com.yonchain.ai.console.model.response.ModelProviderResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

*/
/**
 * 模型提供商控制器
 *
 * @author Cgy
 * @since 1.0.0
 *//*

@Tag(name = "模型提供商管理", description = "模型提供商相关接口")
@RestController
@RequestMapping("/model-providers")
public class ModelProviderController extends BaseController {

    @Autowired
    private ModelService modelService;


    */
/**
     * 分页查询模型提供商
     *
     * @param request 查询请求
     * @return 分页响应
     *//*

    @Operation(summary = "分页查询模型提供商", description = "根据条件分页查询模型提供商列表")
    @GetMapping
    public ListResponse<ModelProviderResponse> getProviders(ModelProviderQueryRequest request) {

        //构建查询条件
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("name", request.getName());

        //分页查询
        List<ModelProvider> providers = modelService.getProviders(getCurrentTenantId(), queryParam);

        return responseFactory.createModelProviderListResponse(providers);
    }

    */
/**
     * 获取模型提供商配置
     *
     * @param providerCode 模型提供商编码
     * @return 配置信息
     *//*

    @Operation(summary = "获取模型提供商配置", description = "根据编码获取模型提供商配置信息")
    @GetMapping("/{providerCode}/config")
    public ProviderConfigResponse getProviderConfig(@PathVariable String providerCode) {

        return modelService.getProviderConfig(getCurrentTenantId(), providerCode);
    }

    */
/**
     * 保存模型提供商配置
     *
     * @param request 配置请求
     * @return 保存结果
     *//*

    @Operation(summary = "保存模型提供商配置", description = "保存模型提供商的配置信息")
    @PostMapping("/config")
    public ApiResponse<Void> saveProviderConfig(
            @Parameter(description = "配置请求", required = true)
            @RequestBody @Valid ProviderConfigRequest request) {

        // 构建配置Map传递给service层
        Map<String, Object> param = new HashMap<>();
        param.put("enabled", request.getEnabled());
        param.put("config", request.getConfig());


        modelService.saveProviderConfig(this.getCurrentTenantId(), request.getProvider(), param);

        return ApiResponse.success();
    }

}
*/
