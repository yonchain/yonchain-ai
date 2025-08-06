package com.yonchain.ai.console.utils;

import com.yonchain.ai.api.idm.CurrentUser;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultCurrentUser implements CurrentUser {

    /**
     * 用户唯一标识符
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户电子邮箱
     */
    private String email;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户角色id列表
     */
    private List<String> roleIds;

    /**
     * 用户角色编码列表
     */
    private List<String> roleCodes;

    /**
     * 是否为超级管理员
     */
    private boolean isSuperAdmin;

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * 设置用户名称
     *
     * @param userName 用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * 设置用户邮箱
     *
     * @param email 用户邮箱
     */
    public void setEmail(String email) {
        this.email = email;
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
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
     * 设置用户角色ID列表
     *
     * @param roleIds 用户角色ID列表
     */
    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 获取用户角色ID列表
     *
     * @return 用户角色ID列表
     */
    public List<String> getRoleIds() {
        if (CollectionUtils.isEmpty(roleIds)) {
            roleIds = new ArrayList<>();
        }
        return roleIds;
    }

    /**
     * 设置用户角色编码列表
     *
     * @param roleCodes 用户角色编码列表
     */
    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    /**
     * 获取用户角色编码列表
     *
     * @return 用户角色编码列表
     */
    public List<String> getRoleCodes() {
        if(CollectionUtils.isEmpty(roleCodes)){
            roleCodes = new ArrayList<>();
        }
        return roleCodes;
    }

    /**
     * 设置是否为超级管理员
     *
     * @param isSuperAdmin 是否为超级管理员
     */
    public void setSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
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
