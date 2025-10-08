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

public class Page<T> {

    private List<T> records;
    private long total;
    private long size;
    private long current;

    public Page() {
    }

    public Page(List<T> records, long total, int pageNum, int pageSize) {
        this.records = records;
        this.total = total;
        this.current = pageNum;
        this.size = pageSize;
    }


    public List<T> getRecords() {
        return records;
    }


    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }


    public void setTotal(long total) {
        this.total = total;
    }


    public void setSize(long size) {
        this.size = size;
    }


    public long getSize() {
        return size;
    }


    public void setCurrent(long current) {
        this.current = current;
    }


    public long getCurrent() {
        return current;
    }


}
