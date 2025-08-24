package com.yonchain.ai.console.model.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 模型更新状态请求
 */
@Data
@Schema(description = "模型创建/更新请求")
public class ModelUpdateStatusRequest {

    /**
     * 模型名称
     */
    @NotBlank(message = "模型提供商不能为空")
    @Schema(description = "模型提供商名称", required = true)
    private String provider;

    /**
     * 模型类型
     */
    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用", required = true)
    private Boolean enabled;

}
