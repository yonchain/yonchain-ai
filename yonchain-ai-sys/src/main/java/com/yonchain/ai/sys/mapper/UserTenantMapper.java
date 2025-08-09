package com.yonchain.ai.sys.mapper;

import com.yonchain.ai.sys.entity.UserTenantEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户账户关联关系Mapper接口
 * <p>
 * 处理租户和账户之间的关联关系数据访问
 * </p>
 *
 * @author Cgy
 * @since 2024-01-20
 */
public interface UserTenantMapper {

    /**
     * 根据ID查询租户账户关联记录
     *
     * @param id 关联ID
     * @return 租户账户关联实体
     */
    UserTenantEntity selectById(@Param("id") String id);

    /**
     * 根据租户ID查询关联记录列表
     *
     * @param tenantId 租户ID
     * @return 关联记录列表
     */
    List<UserTenantEntity> selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据账户ID查询关联记录列表
     *
     * @param accountId 账户ID
     * @return 关联记录列表
     */
    List<UserTenantEntity> selectByAccountId(@Param("accountId") String accountId);

    /**
     * 插入租户账户关联记录
     *
     * @param join 租户账户关联实体
     * @return 影响的行数
     */
    int insert(UserTenantEntity join);

    /**
     * 更新租户账户关联记录
     *
     * @param join 租户账户关联实体
     * @return 影响的行数
     */
    int update(UserTenantEntity join);

    /**
     * 根据ID删除租户账户关联记录
     *
     * @param id 关联ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID删除关联记录
     *
     * @param tenantId 租户ID
     * @return 影响的行数
     */
    int deleteByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据账户ID删除关联记录
     *
     * @param accountId 账户ID
     * @return 影响的行数
     */
    int deleteByAccountId(@Param("accountId") String accountId);

    /**
     * 更新当前状态
     *
     * @param tenantId 租户ID
     * @param accountId 账户ID
     */
    void updateCurrentStatus(@Param("tenantId") String tenantId, @Param("accountId") String accountId);

    /**
     * 更新用户其他租户的当前状态为false
     *
     * @param tenantId 当前租户ID(排除)
     * @param accountId 账户ID
     */
    void updateOtherTenantsCurrentStatus(@Param("tenantId") String tenantId, @Param("accountId") String accountId);

}
