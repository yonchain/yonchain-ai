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

package com.yonchain.ai.api.model;


/**
 * 配置项数据结构
 * 
 * @author Cgy
 * @since 1.0.0
 */
public class ModelProviderConfigItem {
    
    /**
     * 配置项键名（如：apiKey, baseUrl, orgId）
     */
    private String key;
    
    /**
     * 配置项类型（string, number, boolean等）
     */
    private String type;
    
    /**
     * 显示标题
     */
    private String title;
    
    /**
     * 描述信息
     */
    private String description;
    
    /**
     * 是否必填
     */
    private Boolean required;
    
    /**
     * 默认值
     */
    private Object defaultValue;
    
    /**
     * 当前配置的值
     */
    private Object value;
    
    
    /**
     * 验证规则（可选）
     */
    private Object validation;
    
    /**
     * 显示顺序
     */
    private Integer order;
    
    /**
     * 是否敏感信息（如密码、密钥等）
     */
    private Boolean sensitive;
    
    /**
     * 配置项分组
     */
    private String group;
    
    /**
     * 占位符文本
     */
    private String placeholder;
    
    /**
     * 帮助链接
     */
    private String helpUrl;


    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Boolean getSensitive() {
        return sensitive;
    }

    public void setSensitive(Boolean sensitive) {
        this.sensitive = sensitive;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Object getValidation() {
        return validation;
    }

    public void setValidation(Object validation) {
        this.validation = validation;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}