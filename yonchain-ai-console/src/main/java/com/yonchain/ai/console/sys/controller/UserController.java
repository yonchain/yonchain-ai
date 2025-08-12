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

package com.yonchain.ai.console.sys.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainForbiddenException;
import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.sys.enums.MenuType;
import com.yonchain.ai.api.sys.enums.RoleType;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.file.service.FileService;
import com.yonchain.ai.console.sys.request.UserCreateRequest;
import com.yonchain.ai.console.sys.request.UserPasswordRestRequest;
import com.yonchain.ai.console.sys.request.UserQueryRequest;
import com.yonchain.ai.console.sys.request.UserUpdateRequest;
import com.yonchain.ai.console.sys.response.MenuTreeResponse;
import com.yonchain.ai.console.sys.response.UserResponse;
import com.yonchain.ai.util.Assert;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private FileService fileService;

    /**
     * 根据用户id获取用户信息
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/{userId}")
    @Operation(summary = "根据用户id获取用户信息")
    public UserResponse getById(@Parameter(description = "用户id") @PathVariable String userId) {
        User user = this.getUserFromRequest(userId);
        return responseFactory.createUserResponse(user);
    }

    /**
     * 根据用户id获取用户信息详情
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/{userId}/details")
    @Operation(summary = "根据用户id获取用户信息")
    public UserResponse getDetailsById(@Parameter(description = "用户id") @PathVariable String userId) {
        User user = this.getUserFromRequest(userId);
        return this.buildDetailsResponse(user);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息")
    public Map<String, Object> getCurrentUserInfo() {
        // 获取当前用户信息
        String userId = getCurrentUserId();
        User user = this.getUserFromRequest(userId);

        // 获取用户信息及用户与菜单的按钮权限信息
        List<Menu> menus = userService.getUserMenus(this.getCurrentTenantId(), user.getId(), MenuType.BUTTON);

        Map<String, Object> map = new HashMap<>();
        map.put("user", this.buildDetailsResponse(user));
        map.put("permissions", menus.stream()
                .map(Menu::getPermission)
                .distinct()
                .toArray(String[]::new));
        return map;
    }

    /**
     * 分页查询
     *
     * @param request 查询请求
     * @return 保存后的用户信息
     */
    @GetMapping
    @Operation(summary = "分页查询", description = "分页查询用户信息")
    public PageResponse<UserResponse> pageUsers(UserQueryRequest request) {
        String tenantId = this.getCurrentTenantId();

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("name", request.getName());
        queryParam.put("email", request.getEmail());

        Page<User> users = userService.pageUsers(tenantId, queryParam, request.getPage(), request.getLimit());

        return buildPageResponse(users);
    }


    /**
     * 创建用户信息
     *
     * @param request 用户信息
     * @return 保存后的用户信息
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建用户基础信息")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        // 检查邮箱是否已存在
        if (userService.getUserByEmail(request.getEmail()) != null) {
            throw new YonchainForbiddenException("邮箱已存在");
        }

        //检查是否存在系统角色,而且只能存在一个系统角色(dify规范同个租户同个账号只有一个角色)
        List<String> roleIds = request.getRoleIds();
        this.checkSystemRole(this.getCurrentTenantId(), roleIds);

        User user = request.getUser();
        user.setId(UUID.randomUUID().toString());

        // 创建账户
        userService.createUser(this.getCurrentTenantId(), user, roleIds);

        user = userService.getUserById(user.getId());
        return responseFactory.createUserResponse(user);
    }


    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param request 用户信息
     * @return 保存后的用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "根据id更新用户信息")
    public ApiResponse<Void> updateUser(@Parameter(description = "用户id") @PathVariable String id,
                                        @Valid @RequestBody UserUpdateRequest request) {
        // 获取用户，检查用户是否已存在
        User user = this.getUserFromRequest(id);

        //填充用户信息
        populateUserFromRequest(user, request);

        // 更新用户
        if (request.isRoleSet()) {
            Assert.notEmpty(request.getRoleIds(), "角色不能为空");

            //检查是否存在系统角色,而且只能存在一个系统角色(dify规范同个租户同个账号只有一个角色)
            List<String> roleIds = request.getRoleIds();
            this.checkSystemRole(this.getCurrentTenantId(), roleIds);

            userService.updateUser(this.getCurrentTenantId(), user, request.getRoleIds());
        }
        //个人中心更新
        else {
            userService.updateUser(user);
        }

        return ApiResponse.success();
    }

    /**
     * 批量删除用户
     *
     * @param userId 用户id
     * @return 保存后的用户信息
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "批量删除用户", description = "根据id批量删除用户")
    public ApiResponse<Void> deleteUsers(@PathVariable String userId) {
        User user = this.getUserFromRequest(userId);

        CurrentUser currentUser = this.getCurrentUser();
        //不能删除自己
        if (currentUser.getUserId().equals(userId)) {
            throw new YonchainForbiddenException("不能删除自己");
        }

        //超级管理员不能删除
        if (userService.getUserRoles(this.getCurrentTenantId(), user.getId())
                .stream()
                .anyMatch(role -> RoleType.OWNER.getValue().equals(role.getCode()))) {
            throw new YonchainForbiddenException("禁止删除超级管理员");
        }

        //删除用户
        userService.deleteUser(this.getCurrentTenantId(), user.getId());
        return ApiResponse.success();
    }


    /**
     * 重置密码（当前用户）
     *
     * @param request 更新密码请求
     * @return 更新后的账户
     */
    @PutMapping("/current/password")
    @Operation(summary = "重置密码（当前用户）", description = "重置密码用户密码")
    public ApiResponse<Boolean> resetCurrentUserPassword(@Valid @RequestBody UserPasswordRestRequest request) {

        if (!request.getNewPassword().equals(request.getRepeatNewPassword())) {
            throw new YonchainIllegalStateException("两次新密码不一致");
        }

        userService.resetPassword(this.getCurrentUserId(), request.getPassword(), request.getNewPassword());

        return ApiResponse.success(true);
    }

    /**
     * 更新账户最后活跃时间
     *
     * @param id 账户ID
     * @return 更新后的账户
     */
    @PutMapping("/{id}/last-active")
    @Operation(summary = "更新账户最后活跃时间", description = "根据id更新账户最后活跃时间")
    public ApiResponse<Void> updateLastActive(@Parameter(description = "用户id") @PathVariable String id) {
        // 检查账户是否存在
        User user = userService.getUserById(id);
        if (user == null) {
            throw new YonchainResourceNotFoundException("用户不存在");
        }

        // 更新最后活跃时间
        userService.updateLastActive(id);

        return ApiResponse.success();
    }


    /**
     * 检查密码
     *
     * @param password 密码
     * @return 是否匹配
     */
    @PostMapping("/checkPassword")
    @Operation(summary = "检查密码", description = "检查密码是否匹配")
    public ApiResponse<?> checkPassword(String password) {
        User user = this.getUserFromRequest(this.getCurrentUserId());

        boolean matches = securityService.matchesPassword(password, user.getPasswordSalt(), user.getPassword());

        return matches ? ApiResponse.success(true) : ApiResponse.fail("密码不正确");
    }


    /**
     * 获取当前用户菜单
     *
     * @return 当前用户菜单列表
     */
    @GetMapping("/current/menus")
    @Operation(summary = "获取当前用户菜单", description = "获取当前登录用户的菜单列表")
    public List<Map<String, Object>> getCurrentUserMenus() {
        //查询用户菜单列表
        List<Menu> menus = userService.getUserMenus(getCurrentTenantId(), getCurrentUserId(), MenuType.MENU);

        List<Map<String, Object>> menuList = new ArrayList<>();
        menus.forEach(menu -> {
            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("parentId", menu.getParentId());
            queryParam.put("id", menu.getId());
            queryParam.put("name", menu.getName());
            queryParam.put("icon", menu.getIcon());
            queryParam.put("sort", menu.getSortOrder());
            queryParam.put("type", menu.getMenuType());
            queryParam.put("path", menu.getPath());
            queryParam.put("permission", menu.getPermission());
            queryParam.put("isLink", menu.getIsLink());
            queryParam.put("isHide", menu.getIsHide());
            queryParam.put("enName",menu.getEnName());
            menuList.add(queryParam);
        });

        // 构建树形菜单结构
        List<Map<String, Object>> treeMenus = new ArrayList<>();
        Map<String, Map<String, Object>> menuMap = new HashMap<>();

        // 将所有菜单存入map，以id为key
        for (Map<String, Object> menu : menuList) {
            menuMap.put(menu.get("id").toString(), menu);
        }

        // 遍历菜单，构建父子关系
        for (Map<String, Object> menu : menuList) {
            String parentId = menu.get("parentId").toString();
            if ("b61804f0-e99e-4c15-9f9c-0784b125888b".equals(parentId)) {
                treeMenus.add(menu);
            } else {
                Map<String, Object> parentMenu = menuMap.get(parentId);
                if (parentMenu != null) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parentMenu.computeIfAbsent("children", k -> new ArrayList<>());
                    children.add(menu);
                }
            }
        }

        // 按sort字段排序
        treeMenus.sort(Comparator.comparingInt(m -> Integer.parseInt(m.get("sort").toString())));
        for (Map<String, Object> menu : menuList) {
            if (menu.containsKey("children")) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) menu.get("children");
                children.sort(Comparator.comparingInt(m -> Integer.parseInt(m.get("sort").toString())));
            }
        }


        return treeMenus;
    }


    /**
     * 从请求中填充用户信息，采用选择性更新策略
     * 只更新请求中非空的字段，保持其他字段不变，避免不必要的数据库更新操作
     *
     * @param user    需要更新的用户实体对象
     * @param request 包含更新信息的请求对象
     */
    private void populateUserFromRequest(User user, UserUpdateRequest request) {
        // 更新用户名称
        user.setName(request.getName());

        // 更新用户邮箱
        // 邮箱作为用户的唯一联系方式，用于登录和接收系统通知
       /* if (StringUtils.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
        }*/

        // 更新用户头像
        // 头像URL地址，支持相对路径或完整的URL地址
        if (request.isAvatarSet()) {
            user.setAvatar(request.getAvatar());
        }

        // 更新界面语言设置
        // 用户界面的显示语言，如：zh-CN（中文简体）、en-US（英文）等
        if (request.isInterfaceLanguageSet()) {
            user.setInterfaceLanguage(request.getInterfaceLanguage());
        }

        // 更新界面主题设置
        // 用户界面的显示主题，如：light（明亮主题）、dark（暗黑主题）等
        if (request.isInterfaceThemeSet()) {
            user.setInterfaceTheme(request.getInterfaceTheme());
        }

        // 更新时区设置
        // 用户所在时区，如：Asia/Shanghai、America/New_York等
        if (request.isTimezoneSet()) {
            user.setTimezone(request.getTimezone());
        }
    }

    /**
     * 根据用户ID获取租户信息
     *
     * @param id 用户ID
     * @return 租户实体
     * @throws IllegalArgumentException 当用户不存在时抛出异常
     */
    private User getUserFromRequest(String id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new YonchainResourceNotFoundException("用户不存在");
        }
        return user;
    }

    /**
     * 构建分页响应
     *
     * @param users 用户列表
     * @return 分页响应
     */
    private PageResponse<UserResponse> buildPageResponse(Page<User> users) {
        String tenantId = this.getCurrentTenantId();
        PageResponse<UserResponse> response = responseFactory.createUserPageResponse(users);
        response.getData().forEach(user -> {
            //设置角色列表
            user.setRoleList(userService.getUserRoles(tenantId, user.getId()));
        });
        return response;
    }


    /**
     * 构建用户详情响应
     *
     * @param user 用户
     * @return 用户详情响应
     */
    private UserResponse buildDetailsResponse(User user) {
        UserResponse userResponse = responseFactory.createUserResponse(user);
        // 设置角色列表
        userResponse.setRoleList(userService.getUserRoles(this.getCurrentTenantId(), user.getId()));
        //设置头像url
        String avatarUrl = fileService.getSignedFileUrl(user.getAvatar());
        userResponse.setAvatarUrl(avatarUrl);
        return userResponse;
    }

    /**
     * 检查角色是否包含系统角色
     *
     * @param tenantId 租户ID
     * @param roleIds  角色ID列表
     */
    private void checkSystemRole(String tenantId, List<String> roleIds) {
        int systemRoleCount = roleService.getSystemRoleCount(tenantId, roleIds);
        if (systemRoleCount == 0) {
            throw new YonchainForbiddenException("角色必须包含默认角色");
        }
        if (systemRoleCount > 1) {
            throw new YonchainForbiddenException("角色不能包含多个默认角色");
        }
    }
}

