package com.yonchain.ai.console.model.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 模型创建/更新请求
 */
@Data
@Schema(description = "模型创建/更新请求")
public class ModelRequest {
    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空")
    @Parameter(description = "模型名称", required = true)
    @Schema(description = "模型名称", required = true)
    private String modelName;

    /**
     * 模型类型
     */
    @NotBlank(message = "模型类型不能为空")
    @Parameter(description = "模型类型", required = true)
    @Schema(description = "模型类型", required = true)
    private String modelType;

    /**
     * 提供商名称
     */
    @NotBlank(message = "提供商名称不能为空")
    @Parameter(description = "提供商名称", required = true)
    @Schema(description = "提供商名称", required = true)
    private String providerName;

    /**
     * 加密配置
     */
    @NotBlank(message = "模型配置不能为空")
    @Parameter(description = "加密后的模型配置", required = true)
    @Schema(description = "加密后的模型配置", required = true)
    private String encryptedConfig;
}
