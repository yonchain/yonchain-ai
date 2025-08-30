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

package com.yonchain.ai.sys.service;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainForbiddenException;
import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.security.Password;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.sys.entity.UserTenantEntity;
import com.yonchain.ai.sys.mapper.RoleMapper;
import com.yonchain.ai.sys.mapper.RolePermissionMapper;
import com.yonchain.ai.sys.mapper.UserTenantMapper;
import com.yonchain.ai.sys.mapper.UserMapper;
import com.yonchain.ai.util.IdUtil;
import com.yonchain.ai.util.PageUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author Cgy
 * @since 1.0.0
 */
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final SecurityService securityService;

    private final RoleMapper roleMapper;

    private final RolePermissionMapper rolePermissionMapper;

    private final UserTenantMapper userTenantMapper;

    public UserServiceImpl(UserMapper userMapper, SecurityService securityService,
                           RolePermissionMapper rolePermissionMapper,RoleMapper roleMapper,
                           UserTenantMapper userTenantMapper) {
        this.userMapper = userMapper;
        this.securityService = securityService;
        this.roleMapper = roleMapper;
        this.userTenantMapper = userTenantMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public User getUserById(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userMapper.selectByUsername(userName);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public Page<User> pageUsers(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        //分页
        PageHelper.startPage(pageNum, pageSize);

        List<User> users = userMapper.selectList(tenantId, queryParam);

        return PageUtil.convert(users);
    }

    @Override
    public List<User> getUsers(String tenantId, Map<String, Object> queryParam) {
        return userMapper.selectList(tenantId, queryParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        // 如果ID为空，则生成ID
        if (!StringUtils.hasText(user.getId())) {
            user.setId(IdUtil.generateId());
        }

        //如果密码盐为空，则生成密码盐，并对原始密码加密
        if (!StringUtils.hasText(user.getPasswordSalt())) {
            Password password = securityService.encodePassword(user.getPassword());
            user.setPasswordSalt(password.getSalt());
            user.setPassword(password.getPassword());
        }

        //如果时区为空，则设置时区为上海
        if (!StringUtils.hasText(user.getTimezone())) {
            user.setTimezone("Asia/Shanghai");
        }

        //如果主题为空，则设置主题为light
        if (!StringUtils.hasText(user.getInterfaceTheme())) {
            user.setInterfaceTheme("light");
        }

        //如果语言为空，则设置语言为简体中文
        if (!StringUtils.hasText(user.getInterfaceLanguage())) {
            user.setInterfaceLanguage("zh-Hans");
        }

        //如果状态为空，则设置状态为正常
        if (!StringUtils.hasText(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
        }

        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setLastActiveAt(new Date());
        user.setInitializedAt(LocalDateTime.now());

        //插入用户表
        userMapper.insert(user);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(String tenantId, User user, List<String> roleIds) {

        //插入用户表
        this.createUser(user);

       /* //获取系统默认角色和业务角色
        Map<String, Object> roleQueryParam = new HashMap<>();
        roleQueryParam.put("roleIds", roleIds);
        roleQueryParam.put("category", "0");
        List<Role> roles = roleMapper.selectList(tenantId, roleQueryParam);
        if (roles.isEmpty()) {
            throw new YonchainForbiddenException("角色必须包含默认角色");
        }
        if (roles.size() > 1) {
            throw new YonchainForbiddenException("角色不能包含多个默认角色");
        }
        Role systemRole = roles.get(0);*/


        //添加到用户与租户关联表
        UserTenantEntity userTenantEntity = new UserTenantEntity();
        userTenantEntity.setId(IdUtil.generateId());
        userTenantEntity.setTenantId(tenantId);
        userTenantEntity.setAccountId(user.getId());
        userTenantEntity.setCurrent(true);
     //   userTenantEntity.setRole(systemRole.getCode());
        userTenantEntity.setCreatedAt(new Date());
        userTenantEntity.setUpdatedAt(new Date());
        userTenantMapper.insert(userTenantEntity);

        //添加到用户与角色关联表
       /* roleIds = roleIds.stream()
                .filter(roleId -> !roleId.equals(systemRole.getId()))
                .collect(Collectors.toList());*/


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        userMapper.updateById(user);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(String tenantId, User user, List<String> roleIds) {
        this.updateUser(user);
        //TODO 添加到用户与租户关联表
    }


    @Override
    public void deleteUser(String tenantId, String userId) {
        userMapper.deleteById(userId);
        //TODO 删除到用户与租户关联表
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String userId, String oldPassword, String newPassword) {
        // 获取账户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new YonchainResourceNotFoundException("账户不存在");
        }

        // 验证旧密码
        if (!securityService.matchesPassword(oldPassword, user.getPasswordSalt(), user.getPassword())) {
            throw new YonchainIllegalStateException("旧密码不正确");
        }

        // 加密新密码
        Password password = securityService.encodePassword(newPassword);

        // 更新密码
        user.setPassword(password.getPassword());
        user.setPasswordSalt(password.getSalt());
        user.setLastActiveAt(new Date());

        // 更新账户
        userMapper.updateById(user);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLogin(String userId, String ip) {
        // 获取账户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new YonchainResourceNotFoundException("账户不存在");
        }

        // 更新最后登录信息
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setLastActiveAt(new Date());
        user.setUpdatedAt(new Date());

        // 更新账户
        userMapper.updateById(user);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastActive(String userId) {
        // 获取账户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new YonchainResourceNotFoundException("账户不存在");
        }
        // 更新最后活跃时间
        user.setLastActiveAt(new Date());
        user.setUpdatedAt(new Date());

        // 更新账户
        userMapper.updateById(user);

    }

    @Override
    public List<Role> getUserRoles(String tenantId, String userId) {
        return roleMapper.selectUserRoles(tenantId, userId);
    }

    @Override
    public List<String> getUserPermissions(String tenantId, String userId) {
        return userMapper.selectUserPermissions(tenantId, userId);
    }

}
