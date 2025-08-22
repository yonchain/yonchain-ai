package com.yonchain.ai.console.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 模型提供商配置请求实体
 *
 * @author yonchain
 * @since 2024-01-01
 */
@Data
@Schema(description = "模型提供商配置请求")
public class ProviderConfigRequest {

    @Schema(description = "提供商ID")
    private Long providerId;

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "提供商类型", required = true)
    @NotBlank(message = "提供商类型不能为空")
    private String providerType;

    @Schema(description = "API密钥", required = true)
    @NotBlank(message = "API密钥不能为空")
    private String apiKey;

    @Schema(description = "基础URL")
    private String baseUrl;

    @Schema(description = "是否启用", required = true)
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;

    @Schema(description = "配置参数")
    private Map<String, Object> configParams;

    @Schema(description = "描述")
    private String description;
}