package com.yonchain.ai.sys.mapper;

import com.yonchain.ai.api.sys.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色Mapper接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface RoleMapper {

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色实体
     */
    Role selectById(@Param("id") String id);

    /**
     * 查询角色
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 角色列表
     */
    Role selectOne(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 查询角色列表
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 角色
     */
    List<Role> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 插入角色记录
     *
     * @param role 角色实体
     * @return 影响行数
     */
    int insert(Role role);

    /**
     * 根据ID更新角色信息
     *
     * @param role 角色实体
     * @return 影响行数
     */
    int updateById(Role role);

    /**
     * 根据ID删除角色
     *
     * @param id 角色ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据ID批量删除角色
     *
     * @param ids 角色ID列表
     * @return 影响行数
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 查询用户角色列表
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectUserRoles(@Param("tenantId") String tenantId,@Param("userId") String userId);
    
    /**
     * 批量插入角色记录
     *
     * @param roles 角色实体列表
     * @return 影响行数
     */
    int batchInsert(@Param("roles") List<Role> roles);
}
