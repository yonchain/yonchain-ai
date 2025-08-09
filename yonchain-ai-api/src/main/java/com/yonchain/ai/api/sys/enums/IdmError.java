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

public enum IdmError {

    // 租户已存在
    TENANT_EXIST("TENANT_EXIST", "租户已存在"),
    // 租户不存在
    TENANT_NOT_EXIST("TENANT_NOT_EXIST", "租户不存在");


    private final String code;

    private final String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    IdmError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
