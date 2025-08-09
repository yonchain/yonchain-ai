/*
 * Copyright 2025-2028 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.api.sys.enums;

/**
 * 角色组类别
 *
 * @author Cgu
 */
public enum RoleGroupCategory {

    /**
     * 系统角色
     */
    SYSTEM("0"),

    /**
     * 业务角色
     */
    BUSINESS("1");

    private final String value;

    /**
     * 构造函数
     *
     * @param value 角色类型对应的字符串值
     */
    RoleGroupCategory(String value) {
        this.value = value;
    }

    /**
     * 获取角色类型对应的字符串值
     *
     * @return 角色类型字符串值
     */
    public String getValue() {
        return value;
    }

    public static RoleGroupCategory valueOfType(String type) {
        for (RoleGroupCategory roleType : values()) {
            if (roleType.getValue().equals(type)) {
                return roleType;
            }
        }
        throw new IllegalArgumentException("Invalid RoleType: " + type);
    }
}
