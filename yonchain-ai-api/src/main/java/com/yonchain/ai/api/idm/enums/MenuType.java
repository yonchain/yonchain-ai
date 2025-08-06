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
package com.yonchain.ai.api.idm.enums;

/**
 * 菜单类型
 *
 * @author chenyi
 */
public enum MenuType {

    /**
     * 菜单
     */
    MENU("0"),

    /**
     * 按钮
     */
    BUTTON("1");


    private final String value;

    /**
     * 构造函数
     *
     * @param value 菜单类型对应的字符串值
     */
    MenuType(String value) {
        this.value = value;
    }

    /**
     * 获取菜单类型对应的字符串值
     *
     * @return 菜单类型字符串值
     */
    public String getValue() {
        return value;
    }

    public static MenuType valueOfType(String type) {
        for (MenuType menuType : values()) {
            if (menuType.getValue().equals(type)) {
                return menuType;
            }
        }
        throw new IllegalArgumentException("Invalid menuType: " + type);
    }
}
