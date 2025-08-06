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

package com.yonchain.ai.api.idm;


import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.idm.enums.MenuType;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(String userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    User getUserByUserName(String userName);

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 分页查询租户下的用户列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 分页用户列表
     */
    Page<User> pageUsers(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 获取租户下的用户列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 用户列表
     */
    List<User> getUsers(String tenantId, Map<String, Object> queryParam);

    /**
     * 创建用户
     *
     * @param user     用户对象
     */
    void createUser(User user);

    /**
     * 创建用户
     *
     * @param tenantId 租户Id
     * @param user     用户对象
     * @param roleIds  角色id列表
     */
    void createUser(String tenantId, User user, List<String> roleIds);

    /**
     * 更新用户
     *
     * @param user 用户对象
     */
    void updateUser(User user);

    /**
     * 更新用户
     *
     * @param tenantId 租户Id
     * @param user     用户对象
     * @param roleIds  角色id列表
     */
    void updateUser(String tenantId, User user, List<String> roleIds);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(String tenantId, String userId);

    /**
     * 重置密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void resetPassword(String userId, String oldPassword, String newPassword);

    /**
     * 更新用户最后登录信息
     *
     * @param userId 用户ID
     * @param ip     登录IP
     */
    void updateLastLogin(String userId, String ip);

    /**
     * 更新用户最后活跃时间
     *
     * @param userId 用户ID
     * @return 更新后的用户
     */
    void updateLastActive(String userId);

    /**
     * 获取用户角色列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 用户角色列表
     */
    List<Role> getUserRoles(String tenantId, String userId);

    /**
     * 获取用户菜单列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param menuType 菜单类型
     * @return 用户菜单列表
     */
    List<Menu> getUserMenus(String tenantId, String userId, MenuType menuType);
}