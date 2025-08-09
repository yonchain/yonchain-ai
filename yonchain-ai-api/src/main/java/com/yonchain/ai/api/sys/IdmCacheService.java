package com.yonchain.ai.api.sys;

import java.util.List;

/**
 * 身份管理缓存服务接口
 * 提供用户、租户和角色信息的缓存操作
 */
public interface IdmCacheService {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(String userId);

    /**
     * 缓存用户信息
     *
     * @param user 用户对象
     */
    void cacheUser(User user);

    /**
     * 根据用户ID获取租户信息
     *
     * @param userId 用户ID
     * @return 租户对象
     */
    Tenant getTenantByUserId(String userId);

    /**
     * 缓存租户信息
     *
     * @param userId 用户ID
     * @param tenant 租户对象
     */
    void cacheTenant(String userId, Tenant tenant);

    /**
     * 获取用户角色列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(String tenantId, String userId);

    /**
     * 缓存用户角色列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param roles    角色列表
     */
    void cacheUserRoles(String tenantId, String userId, List<Role> roles);

    /**
     * 清除用户缓存
     *
     * @param userId 用户ID
     */
    void clearUserCache(String userId);

    /**
     * 清除租户缓存
     *
     * @param userId 用户ID
     */
    void clearTenantCache(String userId);

    /**
     * 清除角色缓存
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    void clearRolesCache(String tenantId, String userId);
}
