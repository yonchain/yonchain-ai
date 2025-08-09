package com.yonchain.ai.api.sys;

import java.util.List;

/**
 * 当前用户接口
 *
 * @author Cgy
 */
public class CurrentUser {

    /**
     * 租户ID
     */
    private final String tenantId;

    /**
     * 用户唯一标识符
     */
    private final String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户电子邮箱
     */
    private final String email;


    /**
     * 用户角色id列表
     */
    private final List<String> roleIds;

    /**
     * 用户角色编码列表
     */
    private List<String> roleCodes;

    /**
     * 是否为超级管理员
     */
    private boolean isSuperAdmin;

    public CurrentUser(String userId, String name, String email,
                       String id, List<String> roleIds,
                       List<String> roleCodes, boolean isSuperAdmin) {
         this.userId = userId;
        this.userName = name;
        this.email = email;
        this.tenantId = id;
        this.roleIds = roleIds;
        this.roleCodes = roleCodes;
        this.isSuperAdmin = isSuperAdmin;
    }


    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 获取用户邮箱
     *
     * @return 用户邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 获取用户角色ID列表
     *
     * @return 用户角色ID列表
     */
    public List<String> getRoleIds() {
        return roleIds;
    }

    /**
     * 获取用户角色编码列表
     *
     * @return 用户角色编码列表
     */
    public List<String> getRoleCodes() {
        return roleCodes;
    }

    /**
     * 获取是否为超级管理员
     *
     * @return 是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

}
