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

package com.yonchain.ai.console.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模型提供商响应
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
@Schema(description = "模型提供商响应")
public class ModelProviderResponse {

    /**
     * 提供商ID
     */
    @Schema(description = "提供商ID", example = "1234567890abcdef")
    private String id;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID", example = "tenant123")
    private String tenantId;

    /**
     * 提供商名称
     */
    @Schema(description = "提供商名称", example = "OpenAI")
    private String providerName;

    /**
     * 提供商类型
     */
    @Schema(description = "提供商类型", example = "llm")
    private String providerType;

    /**
     * 加密配置
     */
    @Schema(description = "加密配置JSON", example = "{\"apiKey\":\"encrypted_value\"}")
    private String encryptedConfig;

    /**
     * 是否有效
     */
    @Schema(description = "是否有效", example = "true")
    private Boolean isValid;

    /**
     * 最后使用时间
     */
    @Schema(description = "最后使用时间", example = "2023-01-01T12:00:00")
    private LocalDateTime lastUsed;

    /**
     * 配额类型
     */
    @Schema(description = "配额类型", example = "monthly", allowableValues = {"monthly", "daily", "none"})
    private String quotaType;

    /**
     * 配额限制
     */
    @Schema(description = "配额限制", example = "1000")
    private Long quotaLimit;

    /**
     * 已用配额
     */
    @Schema(description = "已用配额", example = "250")
    private Long quotaUsed;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2023-01-01T12:00:00")
    private LocalDateTime updatedAt;
}
