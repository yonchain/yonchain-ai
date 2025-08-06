package com.yonchain.ai.api.idm;

/**
 * IDM服务接口
 */
public interface IdmService {

    /**
     * 获取租户服务
     *
     * @return 租户服务
     */
    TenantService getTenantService();

    /**
     * 获取用户服务
     *
     * @return 用户服务
     */
    UserService getUserService();

    /**
     * 获取角色服务
     *
     * @return 角色服务
     */
    RoleService getRoleService();

}
