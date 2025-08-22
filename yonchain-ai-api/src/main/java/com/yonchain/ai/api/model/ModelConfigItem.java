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
 * 模型配置项数据结构
 * 
 * @author Cgy
 * @since 1.0.0
 */
public class ModelConfigItem {
    
    /**
     * 配置项名称
     */
    private String name;
    
    /**
     * 配置项类型（string, number, integer, boolean等）
     */
    private String type;
    
    /**
     * 默认值
     */
    private Object defaultValue;
    
    /**
     * 最小值（数值类型）
     */
    private Object min;
    
    /**
     * 最大值（数值类型）
     */
    private Object max;
    
    /**
     * 当前配置的值
     */
    private Object value;
    
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
     * 显示顺序
     */
    private Integer order;
    
    /**
     * 配置项分组
     */
    private String group;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}