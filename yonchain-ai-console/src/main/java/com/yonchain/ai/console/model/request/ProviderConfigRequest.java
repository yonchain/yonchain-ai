package com.yonchain.ai.console.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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

    /**
     * 提供商编码
     */
    @NotBlank(message = "提供商编码不能为空")
    @Size(max = 50, message = "提供商编码长度不能超过50个字符")
    @Schema(description = "提供商编码", required = true, example = "openai")
    private String provider;

    /**
     * 提供商编码
     */
    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用", required = true, example = "true")
    private Boolean enabled;

    /**
     * 提供商配置信息
     */
    @NotNull(message = "配置信息不能为空")
    @Schema(description = "提供商配置信息", required = true, example = "{\"apiKey\": \"sk-xxx\", \"timeout\": 30}")
    private Map<String, Object> config;
}