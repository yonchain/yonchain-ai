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

package com.yonchain.ai.console.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 模型提供商更新请求
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
@Schema(description = "模型提供商更新请求")
public class ModelProviderUpdateRequest {

    /**
     * 提供商ID
     */
    @NotBlank(message = "提供商ID不能为空")
    @Schema(description = "提供商ID", required = true, example = "1234567890abcdef")
    private String id;

    /**
     * 提供商名称
     */
    @Size(max = 50, message = "提供商名称长度不能超过50个字符")
    @Schema(description = "提供商名称", example = "OpenAI")
    private String name;

    /**
     * 提供商描述
     */
    @Size(max = 500, message = "提供商描述长度不能超过500个字符")
    @Schema(description = "提供商描述", example = "OpenAI API提供商")
    private String description;

    /**
     * 提供商图标
     */
    @Schema(description = "提供商图标URL", example = "https://example.com/openai-icon.png")
    private String icon;

    /**
     * API基础URL
     */
    @Schema(description = "API基础URL", example = "https://api.openai.com/v1")
    private String apiBaseUrl;

    /**
     * 认证类型
     */
    @Schema(description = "认证类型", example = "api_key", allowableValues = {"api_key", "oauth2"})
    private String authType;

    /**
     * 配置模式
     */
    @Schema(description = "配置模式JSON", example = "{\"type\":\"object\",\"properties\":{\"api_key\":{\"type\":\"string\"}}}")
    private String configSchema;

    /**
     * 是否有效
     */
    @Schema(description = "是否有效", example = "true")
    private Boolean isValid;
}
