package com.yonchain.ai.api.idm;

import java.util.List;

/**
 * 当前用户接口
 *
 * @author Cgy
 */
public interface CurrentUser {

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    String getUserId();

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    String getTenantId();

    /**
     * 获取账户名称
     *
     * @return 账户名称
     */
    String getUserName();

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    String getEmail();

    /**
     * 获取角色ID列表
     *
     * @return 角色ID列表
     */
    List<String> getRoleIds();

    /**
     * 获取角色编码列表
     *
     * @return 角色编码列表
     */
    List<String> getRoleCodes();

    /**
     * 获取是否为超级管理员
     *
     * @return 是否为超级管理员
     */
    boolean isSuperAdmin();

}
