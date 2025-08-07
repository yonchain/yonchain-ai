package com.yonchain.ai.idm.mapper;

import com.yonchain.ai.api.idm.Tenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Tenant表的Mapper接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface TenantMapper {

    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户
     */
    Tenant selectById(@Param("id") String id);

    /**
     * 根据ID查询租户详情
     *
     * @param id 租户ID
     * @return 租户
     */
    Tenant selectDetailById(@Param("id") String id);

    /**
     * 查询用户所属租户列表
     *
     * @param queryParam 查询参数
     * @return 租户
     */
    List<Tenant> selectList(@Param("userId") String userId, @Param("params") Map<String, Object> queryParam);

    /**
     * 根据用户id查询租户列表
     *
     * @param userId 用户id
     * @return 租户列表
     */
    List<Tenant> selectListByUserId(@Param("userId") String userId);

    /**
     * 根据用户id获取当前租户
     *
     * @param userId 用户id
     * @return 当前租户
     */
    Tenant selectCurrentTenantByUserId(@Param("userId") String userId);

    /**
     * 插入一条租户记录
     *
     * @param tenant 租户
     * @return 影响的行数
     */
    int insert(Tenant tenant);

    /**
     * 根据ID更新租户信息
     *
     * @param tenant 租户
     * @return 影响的行数
     */
    int updateById(Tenant tenant);

    /**
     * 根据ID删除租户
     *
     * @param id 租户ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除租户
     *
     * @param ids 租户ID列表
     * @return 影响的行数
     */
    void deleteByIds(@Param("ids") List<String> ids);
}
