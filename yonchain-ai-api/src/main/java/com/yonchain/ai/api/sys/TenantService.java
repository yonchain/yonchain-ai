package com.yonchain.ai.api.sys;

import com.yonchain.ai.api.common.Page;

import java.util.List;
import java.util.Map;

/**
 * 租户服务接口
 *
 * @author Cgy
 */
public interface TenantService {

    /**
     * 根据ID获取租户
     *
     * @param id 租户ID
     * @return 租户
     */
    Tenant getTenantById(String id);

    /**
     * 根据用户id获取租户详情
     *
     * <p>
     * 包括角色
     * </P>
     *
     * @param id 租户ID
     * @return 租户
     */
    Tenant getTenantDetailById(String id);

    /**
     * 根据用户id获取当前租户
     *
     * @param userId 用户id
     * @return 当前租户
     */
    Tenant getCurrentTenantByUserId(String userId);

    /**
     * 根据用户id获取租户列表
     *
     * @param userId 用户i
     * @return 租户列表
     */
    List<Tenant> getTenantsByUserId(String userId);

    /**
     * 分页获取用户所有租户
     *
     * @param userId   用户id
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页租户列表
     */
    Page<Tenant> pageTenants(String userId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 创建租户
     *
     * @param tenant 租户
     */
    void createTenant(Tenant tenant);

    /**
     * 创建租户
     *
     * @param tenant 租户
     * @param userId 超级管理员id
     */
    void createTenant(Tenant tenant, String userId);

    /**
     * 更新租户信息
     *
     * @param tenant 租户
     */
    void updateTenant(Tenant tenant);

    /**
     * 根据ID删除租户
     *
     * @param id 租户ID
     */
    void deleteTenantById(String id);

    /**
     * 切换用户的租户
     *
     * @param tenantId 租户ID
     */
    void switchTenant(String tenantId, String userId);
}
