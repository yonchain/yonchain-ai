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

package com.yonchain.ai.api.agent;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.sys.Role;

import java.util.List;
import java.util.Map;


/**
 * 应用服务接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface AgentService {

    /**
     * 通过ID查询应用
     *
     * @param id 应用ID
     * @return 应用信息
     */
    Agent getAppById(String id);

    /**
     * 分页查询应用列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 分页应用列表
     */
    Page<Agent> getAppsByPage(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 新增应用
     *
     * @param app 应用信息
     */
    void createApp(Agent app);

    /**
     * 新增应用
     *
     * @param app     应用信息
     * @param roleIds 角色ID列表
     */
    void createApp(Agent app, List<String> roleIds);

    /**
     * 修改应用
     *
     * @param app 应用信息
     */
    void updateApp(Agent app);

    /**
     * 修改应用
     *
     * @param app     应用信息
     * @param roleIds 角色ID列表
     */
    void updateApp(Agent app, List<String> roleIds);

    /**
     * 通过ID删除应用
     *
     * @param id 应用ID
     */
    void deleteAppById(String id);

    /**
     * 获取应用关联的角色
     *
     * @param appId 应用ID
     * @return 角色列表
     */
    List<Role> getAppRoles(String appId);

    /**
     * 批量保存应用角色关联
     *
     * @param appId   应用ID
     * @param roleIds 角色ID列表
     */
    void saveAppRoles(String appId, List<String> roleIds);
    
    /**
     * 发布智能体
     *
     * @param agent    发布请求
     * @param publishedBy 发布者ID
     * @return 智能体应用
     */
    Agent publishAgent(Agent agent, String publishedBy);
    
    /**
     * 获取智能体最新发布记录
     *
     * @param agentId 智能体ID
     * @return 发布记录
     */
    AgentPublishRecord getLatestAgentPublishRecord(String agentId);
    
    /**
     * 获取智能体所有发布记录
     *
     * @param agentId 智能体ID
     * @return 发布记录列表
     */
    List<AgentPublishRecord> getAgentPublishRecords(String agentId);
    
    /**
     * 分页查询发布记录
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 分页发布记录列表
     */
    Page<AgentPublishRecord> getAgentPublishRecordsByPage(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);


    AgentPublishRecord getAgentPublishRecordById(String id);
}
