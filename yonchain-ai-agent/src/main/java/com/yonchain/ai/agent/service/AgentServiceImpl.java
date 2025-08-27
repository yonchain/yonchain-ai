/*
 * Copyright (c) 2024 Dify4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.agent.mapper.AgentPublishRecordMapper;
import com.yonchain.ai.api.agent.*;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.agent.mapper.AgentMapper;
import com.yonchain.ai.agent.mapper.AgentRoleMapper;
import com.yonchain.ai.util.Assert;
import com.yonchain.ai.util.PageUtil;
import com.github.pagehelper.PageHelper;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用服务实现类
 *
 * @author Cgy
 * @since 2024-01-20
 */
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentMapper appMapper;

    @Autowired
    private AgentRoleMapper appRoleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AgentPublishRecordMapper agentPublishRecordMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Agent getAppById(String id) {
        return appMapper.selectById(id);
    }

    @Override
    public Page<Agent> getAppsByPage(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Agent> apps = appMapper.selectList(tenantId, queryParam);

        return PageUtil.convert(apps);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApp(Agent app) {

        //如果为空则设置默认值
        if (StringUtils.isBlank(app.getIconType())) {
            app.setIconType("emoji");
        }
        if (app.getDescription() == null) {
            //dify数据库设置不能为空，只能设置空字符串
            app.setDescription("");
        }

        LocalDateTime now = LocalDateTime.now();
        app.setCreatedAt(now);
        app.setUpdatedAt(now);

        //保存应用
        appMapper.insert(app);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApp(Agent app, List<String> roleIds) {
        //创建应用
        this.createApp(app);

        //保存应用关联的角色
        if (!CollectionUtils.isEmpty(roleIds)) {
            this.saveAppRoles(app.getId(), roleIds);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApp(Agent app) {
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.update(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApp(Agent app, List<String> roleIds) {
        //更新应用信息
        this.updateApp(app);

        //保存角色
        if (!CollectionUtils.isEmpty(roleIds)) {
            this.saveAppRoles(app.getId(), roleIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAppById(String id) {
        //删除应用角色
        appRoleMapper.deleteByAppId(id);
        //删除应用
        appMapper.deleteById(id);
    }

    @Override
    public List<Role> getAppRoles(String appId) {
        List<Role> roles = appRoleMapper.selectRoleByAppId(appId);
        //如果应用没有关联角色，则返回默认角色(Dify默认所有默认角色都可以访问应用)
        if (CollectionUtils.isEmpty(roles)) {
            Agent app = getAppById(appId);
            Assert.notNull(app, "应用不存在");
            roles = roleService.getSystemRoles(app.getTenantId());
        }
        return roles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAppRoles(String appId, List<String> roleIds) {
        Assert.notEmpty(roleIds, "角色不能为空");
        Assert.hasText(appId, "应用不能为空");
        //先根据应用id删除所有角色
        appRoleMapper.deleteByAppId(appId);

        //再根据应用id和角色id批量插入
        appRoleMapper.batchInsert(appId, roleIds);
    }






    @Override
    @Transactional(rollbackFor = Exception.class)
    public Agent publishAgent(String id, Agent agent, String publishedBy) {
        Agent app;

        // 检查是否是更新现有智能体
        if (id != null) {
            app = getAppById(id);
            if (app == null) {
                throw new RuntimeException("应用未找到: " + id);
            }
        } else {
            // 创建新智能体
            app = new DefaultAgent();
            app.setId(UUID.randomUUID().toString());
            app.setTenantId(getTenantIdByUserId(publishedBy)); // 需要实现此方法获取租户ID
            app.setCreatedBy(publishedBy);
            app.setMode("agent"); // 设置为智能体模式
        }

        // 获取最新版本号
        Integer latestVersion = agentPublishRecordMapper.selectLatestVersionByAgentId(id);
        int newVersion = (latestVersion == null) ? 1 : latestVersion + 1;

        // 更新基本信息
        app.setName(agent.getName());
        app.setDescription(agent.getDescription() != null ? agent.getDescription() : agent.getPrompt());
        app.setIcon(agent.getIcon());
        app.setIconBackground(agent.getIconBackground());
        app.setUpdatedBy(publishedBy);
        app.setStatus("published"); // 设置状态为已发布

        // 设置智能体特有属性
        app.setPrompt(agent.getPrompt());
        app.setModelId(agent.getModelId());
        app.setWelcomeMessage(agent.getWelcomeMessage());
        app.setPublishedBy(publishedBy);
        app.setPublishedAt(LocalDateTime.now());
        app.setPublishVersion(newVersion);

        // 直接设置列表和Map对象
        if (agent.getKnowledgeBaseIds() != null && !agent.getKnowledgeBaseIds().isEmpty()) {
            app.setKnowledgeBaseIds(agent.getKnowledgeBaseIds());
        }

        if (agent.getPluginIds() != null && !agent.getPluginIds().isEmpty()) {
            app.setPluginIds(agent.getPluginIds());
        }

        if (agent.getMcpConfig() != null && !agent.getMcpConfig().isEmpty()) {
            app.setMcpConfig(agent.getMcpConfig());
        }

        app.setWorkflowId(agent.getWorkflowId());

        // 更新或创建智能体
        if (id != null) {
            updateApp(app);
        } else {
            createApp(app);
        }

        // 创建发布记录
        createAgentPublishRecordInternal(app.getId(), agent, publishedBy);

        return getAppById(app.getId());
    }

    /**
     * 内部方法：创建智能体发布记录
     */
    @Transactional(rollbackFor = Exception.class)
    protected String createAgentPublishRecordInternal(String agentId, Agent agent, String publishedBy) {
        // 获取最新版本号
        Integer latestVersion = agentPublishRecordMapper.selectLatestVersionByAgentId(agentId);
        int newVersion = (latestVersion == null) ? 1 : latestVersion + 1;

        // 创建发布记录
        AgentPublishRecord record = new DefaultAgentPublishRecord();
        record.setId(UUID.randomUUID().toString());
        record.setAgentId(agentId);
        record.setTenantId(getTenantIdByAgentId(agentId));
        record.setVersion(newVersion);
        record.setName(agent.getName());
        record.setPrompt(agent.getPrompt());
        record.setModelId(agent.getModelId());
        record.setWelcomeMessage(agent.getWelcomeMessage());

        // 需要将List和Map转换为JSON字符串，因为AgentPublishRecord仍然使用字符串格式
        try {
            if (agent.getKnowledgeBaseIds() != null && !agent.getKnowledgeBaseIds().isEmpty()) {
                record.setKnowledgeBaseIds(objectMapper.writeValueAsString(agent.getKnowledgeBaseIds()));
            }

            if (agent.getPluginIds() != null && !agent.getPluginIds().isEmpty()) {
                record.setPluginIds(objectMapper.writeValueAsString(agent.getPluginIds()));
            }

            if (agent.getMcpConfig() != null && !agent.getMcpConfig().isEmpty()) {
                record.setMcpConfig(objectMapper.writeValueAsString(agent.getMcpConfig()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON处理错误: " + e.getMessage(), e);
        }

        record.setWorkflowId(agent.getWorkflowId());
        record.setDescription(agent.getDescription());
        record.setIcon(agent.getIcon());
        record.setIconBackground(agent.getIconBackground());
        record.setPublishedBy(publishedBy);
        record.setPublishedAt(LocalDateTime.now());
        record.setStatus("success");

        // 保存记录
        agentPublishRecordMapper.insert(record);

        return record.getId();
    }

    /**
     * 根据用户ID获取租户ID
     */
    private String getTenantIdByUserId(String userId) {
        // 实现此方法，从用户表中获取租户ID
        // 这里简单返回一个默认值，实际项目中需要实现
        return "default_tenant_id";
    }

    @Override
    public AgentPublishRecord getLatestAgentPublishRecord(String agentId) {
        AgentPublishRecord record = agentPublishRecordMapper.selectLatestByAgentId(agentId);
        return convertToApiAgentPublishRecord(record);
    }

    @Override
    public List<AgentPublishRecord> getAgentPublishRecords(String agentId) {
        List<AgentPublishRecord> records = agentPublishRecordMapper.selectByAgentId(agentId);
        return records.stream()
                .map(this::convertToApiAgentPublishRecord)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AgentPublishRecord> getAgentPublishRecordsByPage(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        // 分页查询
        List<AgentPublishRecord> records = agentPublishRecordMapper.selectPageByTenantIdAndParams(tenantId, queryParam);

        return PageUtil.convert(records);
    }

    @Override
    public AgentPublishRecord getAgentPublishRecordById(String id) {
        return agentPublishRecordMapper.selectById(id);
    }

    /**
     * 将实体对象转换为API接口对象
     */
    private AgentPublishRecord convertToApiAgentPublishRecord(AgentPublishRecord record) {
        if (record == null) {
            return null;
        }

        DefaultAgentPublishRecord apiRecord = new DefaultAgentPublishRecord();

        // 基本属性复制
        apiRecord.setId(record.getId());
        apiRecord.setAgentId(record.getAgentId());
        apiRecord.setTenantId(record.getTenantId());
        apiRecord.setVersion(record.getVersion());
        apiRecord.setName(record.getName());
        apiRecord.setPrompt(record.getPrompt());
        apiRecord.setModelId(record.getModelId());
        apiRecord.setWelcomeMessage(record.getWelcomeMessage());
        apiRecord.setKnowledgeBaseIds(record.getKnowledgeBaseIds());
        apiRecord.setPluginIds(record.getPluginIds());
        apiRecord.setMcpConfig(record.getMcpConfig());
        apiRecord.setWorkflowId(record.getWorkflowId());
        apiRecord.setDescription(record.getDescription());
        apiRecord.setIcon(record.getIcon());
        apiRecord.setIconBackground(record.getIconBackground());
        apiRecord.setPublishedBy(record.getPublishedBy());
        apiRecord.setPublishedAt(record.getPublishedAt());
        apiRecord.setStatus(record.getStatus());

        return apiRecord;
    }

    /**
     * 根据智能体ID获取租户ID
     */
    private String getTenantIdByAgentId(String agentId) {
        // 实现此方法，从应用表中获取租户ID
        Agent app = getAppById(agentId);
        return app != null ? app.getTenantId() : null;
    }
}
