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
package com.yonchain.ai.dify.service;

import com.github.pagehelper.PageHelper;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.dify.DefaultDifyApp;
import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.dify.DifyService;
import com.yonchain.ai.api.sys.Role;
import com.yonchain.ai.api.sys.RoleService;
import com.yonchain.ai.dify.api.DifyAppApi;
import com.yonchain.ai.dify.mapper.DifyAppMapper;
import com.yonchain.ai.dify.mapper.DifyAppRoleMapper;
import com.yonchain.ai.util.Assert;
import com.yonchain.ai.util.MapUtil;
import com.yonchain.ai.util.PageUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用服务实现类
 *
 * @author Cgy
 * @since 2024-01-20
 */
@Service
public class DifyAppServiceImpl implements DifyService {

    @Autowired
    private DifyAppMapper appMapper;

    @Autowired
    private DifyAppRoleMapper appRoleMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public DifyApp getAppById(String id) {
        return appMapper.selectById(id);
    }

    @Override
    public Page<DifyApp> getAppsByPage(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<DifyApp> apps = appMapper.selectList(tenantId, queryParam);

        return PageUtil.convert(apps);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApp(DifyApp app) {

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
    public void createApp(DifyApp app, List<String> roleIds) {
        //创建应用
        this.createApp(app);

        //保存应用关联的角色
        if (!CollectionUtils.isEmpty(roleIds)) {
            this.saveAppRoles(app.getId(), roleIds);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApp(DifyApp app) {
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.update(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApp(DifyApp app, List<String> roleIds) {
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
            DifyApp app = getAppById(appId);
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
    public DifyApp getAppByApiKey(String apiKey, String baseUrl) {
        DifyAppApi difyAppApi = DifyAppApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
        ResponseEntity<DifyAppApi.AppInfoResponse> responseEntity = difyAppApi.getAppInfo();
        DifyAppApi.AppInfoResponse response = responseEntity.getBody();
        DifyApp difyApp = new DefaultDifyApp();
        difyApp.setName(response.name());
        difyApp.setDescription(response.description());
        difyApp.setMode(response.mode());
        return difyApp;
    }

    @Override
    public Map<String, Object> getAppParameters(String apiKey, String baseUrl) {
        DifyAppApi difyAppApi = DifyAppApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
        ResponseEntity<DifyAppApi.AppParametersResponse> responseEntity = difyAppApi.getAppParameters();
        DifyAppApi.AppParametersResponse response = responseEntity.getBody();
        return MapUtil.convertToMap(response);
    }
}
