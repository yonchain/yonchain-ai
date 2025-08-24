package com.yonchain.ai.console.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模型响应对象
 */
@Data
@Schema(description = "模型响应对象")
public class ModelResponse {
    /**
     * 模型ID
     */
    @Schema(description = "模型唯一标识")
    private String id;

    /**
     * 模型编码
     */
    @Schema(description = "模型编码")
    private String modelCode;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 模型类型
     */
    @Schema(description = "模型类型")
    private String modelType;

    /**
     * 提供商名称
     */
    @Schema(description = "模型编码")
    private String provider;

    /**
     * 创建时间
     */
    @Schema(description = "模型创建时间")
    private LocalDateTime createdAt;

    /**
     * 是否有效
     */
    @Schema(description = "模型是否有效")
    private Boolean enabled;
}
