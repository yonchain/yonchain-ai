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

import com.yonchain.ai.api.model.ModelProviderCapabilities;
import com.yonchain.ai.api.model.ModelProviderConfigItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型提供商响应
 * 与YAML提供商配置文件保持一致
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
@Schema(description = "模型提供商响应")
public class ModelProviderResponse {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1234567890abcdef")
    private String id;

    /**
     * 提供商唯一标识码
     */
    @Schema(description = "提供商唯一标识码", example = "openai")
    private String code;

    /**
     * 提供商显示名称
     */
    @Schema(description = "提供商显示名称", example = "OpenAI")
    private String name;

    /**
     * 提供商简要描述信息
     */
    @Schema(description = "提供商简要描述信息", example = "OpenAI提供的AI模型服务")
    private String description;

    /**
     * 提供商图标
     */
    @Schema(description = "提供商图标", example = "🤖")
    private String icon;

    /**
     * 排序权重，数值越小排序越靠前
     */
    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;

    /**
     * 该提供商支持的模型类型列表
     */
    @Schema(description = "支持的模型类型列表", example = "[\"text\", \"image\", \"embedding\", \"audio\"]")
    private List<String> supportedModelTypes;

    /**
     * 提供商配置参数的JSON Schema定义
     */
    @Schema(description = "配置参数Schema")
    private List<ModelProviderConfigItem> configSchema;

    /**
     * 提供商支持的能力配置
     */
    @Schema(description = "能力配置")
    private ModelProviderCapabilities capabilities;
}
