/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yonchain.ai.web.response;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 数据列表响应对象
 *
 * @author Cgy
 * @since 1.0.0
 */
public class ListResponse<T> extends HashMap<String, List<T>> {

    /**
     * 数据列表属性名称
     */
    private final String dataName;

    /**
     * 默认构造，使用"data"作为属性名
     */
    public ListResponse() {
        this("data");
    }

    /**
     * 指定属性名构造
     *
     * @param dataName 数据属性名
     */
    public ListResponse(String dataName) {
        this.dataName = dataName;
        setData(Collections.emptyList());
    }


    public List<T> getData() {
        return super.get(dataName);
    }

    public void setData(List<T> data) {
        super.put(dataName, CollectionUtils.isEmpty(data) ? Collections.emptyList() : data);
    }
}
