package com.yonchain.ai.api.idm;

/**
 * 用户详细信息接口，扩展基础用户信息
 * <p>
 * 包含租户信息和角色信息等扩展属性
 */
public interface UserDetail extends User {

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    void setTenantId(String tenantId);

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    String getTenantId();

    /**
     * 设置用户角色
     *
     * @param role 用户角色
     */
    void setRole(String role);

    /**
     * 获取用户角色
     *
     * @return 用户角色
     */
    String getRole();
}
