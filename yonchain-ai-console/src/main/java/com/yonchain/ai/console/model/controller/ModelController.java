package com.yonchain.ai.console.model.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.model.DefaultModel;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.model.request.ModelQueryRequest;
import com.yonchain.ai.console.model.request.ModelRequest;
import com.yonchain.ai.console.model.request.ModelConfigRequest;
import com.yonchain.ai.console.model.request.ModelUpdateStatusRequest;
import com.yonchain.ai.console.model.response.ModelResponse;
import com.yonchain.ai.console.model.response.ModelConfigResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

/**
 * 模型控制器
 *
 * @author chengy
 * @since 1.0.0
 */
@Tag(name = "模型管理", description = "模型相关接口")
@RestController
@RequestMapping("/models")
public class ModelController extends BaseController {

    @Autowired
    private ModelService modelService;

    /**
     * 获取模型详情
     *
     * @param id 模型ID
     * @return 模型响应
     */
    @Operation(summary = "获取模型详情", description = "根据ID获取模型详细信息")
    @GetMapping("/{id}")
    public ModelResponse getModelById(
            @Parameter(description = "模型ID", required = true, in = ParameterIn.PATH)
            @PathVariable String id) {
        ModelInfo model = modelService.getModelById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        return responseFactory.createModelResponse(model);
    }

    /**
     * 分页查询模型
     *
     * @param request 查询请求
     * @return 分页响应
     */
    @Operation(summary = "分页查询模型", description = "根据条件分页查询模型列表")
    @GetMapping
    public ListResponse<ModelResponse> getModels(
            @Parameter(description = "查询条件", required = true)
            ModelQueryRequest request) {

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("modelName", getCurrentTenantId());
        queryParam.put("provider", request.getProvider());
        queryParam.put("modelType", request.getModelType());

        List<ModelInfo> models = modelService.getModels(getCurrentTenantId(), queryParam);

        return responseFactory.createModelListResponse(models);
    }

    /*
     */
/**
 * /**
 * 获取模型配置
 *
 * @param modelCode 模型编码
 * @return 模型配置响应
 *//*

    @Operation(summary = "获取模型配置", description = "获取指定模型的租户级别配置信息")
    @GetMapping("/config")
    public ModelConfigResponse getModelConfig(
            @Parameter(description = "模型编码", required = true)
            @RequestParam("modelCode") String modelCode) {

        ModelInfo model = modelService.getModelConfig(this.getCurrentTenantId(), modelCode);

        if (model == null) {
            throw new YonchainResourceNotFoundException("模型不存在");
        }

        return responseFactory.createModelConfigResponse(model);
    }
*/

    /**
     * /**
     * 保存模型配置
     *
     * @param request 模型配置请求
     * @return 保存结果
     */
    @Operation(summary = "保存模型配置", description = "保存指定模型的租户级别配置信息")
    @PutMapping("/config")
    public ApiResponse<Void> saveModelConfig(
            @Parameter(description = "模型配置请求", required = true)
            @Valid @RequestBody ModelConfigRequest request) {
/*
        ModelInfo model = modelService.getModelByCode(request.getModelCode());
        if (model == null) {
            throw new YonchainResourceNotFoundException("模型不存在");
        }

        model.setEnabled(request.getEnabled());


        modelService.saveModelConfig(this.getCurrentTenantId(), model);*/

        return ApiResponse.success();
    }

    /**
     * 设置模型状态
     *
     * @param modelCode 模型编码
     * @param request   请求
     * @return 响应
     */
    @Operation(summary = "设置模型状态", description = "根据ID获取模型详细信息")
    @PutMapping("/{modelCode}/status")
    public ApiResponse updateStatus(
            @Parameter(description = "模型编码", required = true, in = ParameterIn.PATH)
            @PathVariable String modelCode,
            @RequestBody ModelUpdateStatusRequest request) {
        modelService.updateModelStatus(this.getCurrentTenantId(), request.getProvider(), modelCode, request.getEnabled());
        return ApiResponse.success();
    }


}
