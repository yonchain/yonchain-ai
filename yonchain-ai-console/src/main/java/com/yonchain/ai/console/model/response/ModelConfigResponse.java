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

import java.util.List;

/**
 * 模型配置响应
 *
 * @author Cgy
 * @since 1.0.0
 */
@Schema(description = "模型配置响应")
public class ModelConfigResponse {

    @Schema(description = "模型代码", example = "gpt-4")
    private String modelCode;

    @Schema(description = "模型名称", example = "GPT-4")
    private String modelName;

    @Schema(description = "是否启用该模型", example = "true")
    private Boolean enabled;

    @Schema(description = "模型配置项列表")
    private List<ConfigItem> configItems;

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<ConfigItem> getConfigItems() {
        return configItems;
    }

    public void setConfigItems(List<ConfigItem> configItems) {
        this.configItems = configItems;
    }

    /**
     * 配置项内部类 - 用于前端展示配置信息
     */
    @Schema(description = "配置项")
    public static class ConfigItem {

        @Schema(description = "配置项键名", example = "temperature")
        private String key;

        @Schema(description = "配置项值", example = "0.7")
        private Object value;

        @Schema(description = "配置项类型", example = "number")
        private String type;

        @Schema(description = "显示标题", example = "温度")
        private String title;

        @Schema(description = "描述信息", example = "控制输出的随机性")
        private String description;

        @Schema(description = "是否必填", example = "false")
        private Boolean required;

        @Schema(description = "默认值", example = "0.7")
        private Object defaultValue;

        @Schema(description = "最小值", example = "0")
        private Object min;

        @Schema(description = "最大值", example = "2")
        private Object max;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getMin() {
            return min;
        }

        public void setMin(Object min) {
            this.min = min;
        }

        public Object getMax() {
            return max;
        }

        public void setMax(Object max) {
            this.max = max;
        }
    }
}
