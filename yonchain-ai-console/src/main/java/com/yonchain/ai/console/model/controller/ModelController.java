package com.yonchain.ai.console.model.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.model.DefaultModel;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.model.request.ModelQueryRequest;
import com.yonchain.ai.console.model.request.ModelRequest;
import com.yonchain.ai.console.model.response.ModelResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public PageResponse<ModelResponse> pageModels(
            @Parameter(description = "查询条件", required = true)
            ModelQueryRequest request) {

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("tenantId", getCurrentTenantId());
        queryParam.put("providerName", request.getProviderName());
        queryParam.put("modelType", request.getModelType());

        Page<ModelInfo> models = modelService.pageModels(getCurrentTenantId(), queryParam, request.getPageNum(), request.getPageSize());

        return responseFactory.createModelPageResponse(models);
    }

    /**
     * 创建模型
     *
     * @param request 创建请求
     * @return 创建的模型
     */
    @Operation(summary = "创建模型", description = "创建一个新的模型")
    @PostMapping
    public ModelResponse createModel(@Valid @RequestBody ModelRequest request) {
        ModelInfo model = new DefaultModel();
        model.setId(UUID.randomUUID().toString());
        model.setTenantId(getCurrentTenantId());
        //model.setIsValid(true);

        //设置模型信息
        this.populateModelFromRequest(model, request);

        //从请求填充模型信息
        modelService.createModel(model);

        model = modelService.getModelById(model.getId());
        return responseFactory.createModelResponse(model);
    }

    /**
     * 更新模型
     *
     * @param id      模型ID
     * @param request 更新请求
     * @return 更新后的模型
     */
    @Operation(summary = "更新模型", description = "更新指定ID的模型信息")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateModel(
            @Parameter(description = "模型ID", required = true, in = ParameterIn.PATH) @PathVariable String id,
            @Valid @RequestBody ModelRequest request) {
        ModelInfo model = modelService.getModelById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        //从请求填充模型信息
        this.populateModelFromRequest(model, request);

        //更新模型
        modelService.updateModel(model);
        return ApiResponse.success();
    }

    /**
     * 删除模型
     *
     * @param id 模型ID
     * @return 操作结果
     */
    @Operation(summary = "删除模型", description = "删除指定ID的模型")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteModelById(@Parameter(description = "模型ID") @PathVariable String id) {
        modelService.deleteModel(id);
        return ApiResponse.success();
    }

    /**
     * 从请求中填充模型数据
     * 只有当请求中的字段不为空时才进行填充
     *
     * @param model   要填充的模型对象
     * @param request 请求对象
     */
    private void populateModelFromRequest(ModelInfo model, ModelRequest request) {
        // 设置模型名称（如果不为空）
        if (request.getModelName() != null) {
            model.setName(request.getModelName());
        }

        // 设置模型类型（如果不为空）
        if (request.getModelType() != null) {
            model.setType(request.getModelType());
        }

        // 设置提供商名称（如果不为空）
        if (request.getProviderName() != null) {
            model.setProvider(request.getProviderName());
        }

        // 设置加密配置（如果不为空）
        if (request.getEncryptedConfig() != null) {
           // model.setEncryptedConfig(request.getEncryptedConfig());
        }

        // 设置模型为有效状态
       // model.setIsValid(true);

        // 设置当前时间为更新时间
       // model.setUpdatedAt(LocalDateTime.now());

        // 如果是新建模型，设置创建时间
       /* if (model.getCreatedAt() == null) {
            model.setCreatedAt(LocalDateTime.now());
        }*/
    }

}
