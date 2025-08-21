package com.yonchain.ai.console.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 默认模型请求类
 *
 * @author Craft
 * @since 1.0.0
 */
@Schema(description = "默认模型请求")
public class TenantDefaultModelRequest {

    /**
     * 提供商名称
     */
    @Schema(description = "提供商名称", example = "openai")
    @NotBlank(message = "提供商名称不能为空")
    private String providerName;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称", example = "gpt-3.5-turbo")
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    /**
     * 模型类型
     */
    @Schema(description = "模型类型", example = "chat")
    @NotBlank(message = "模型类型不能为空")
    private String modelType;

    /**
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * 设置提供商名称
     *
     * @param providerName 提供商名称
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * 获取模型名称
     *
     * @return 模型名称
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 设置模型名称
     *
     * @param modelName 模型名称
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 获取模型类型
     *
     * @return 模型类型
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * 设置模型类型
     *
     * @param modelType 模型类型
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
