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
package com.yonchain.ai.api.common;

import java.util.List;

/**
 * 分页实现类
 *
 * @author Cgy
 * @since 2024-08-27
 */
public class PageImpl<T> extends Page<T> {

    /**
     * 构造函数
     *
     * @param records 记录列表
     * @param current 当前页码
     * @param size    每页大小
     * @param total   总记录数
     */
    public PageImpl(List<T> records, long current, long size, long total) {
        setRecords(records);
        setCurrent(current);
        setSize(size);
        setTotal(total);
    }
}