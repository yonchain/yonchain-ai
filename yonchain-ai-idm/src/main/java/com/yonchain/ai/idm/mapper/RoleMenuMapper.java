package com.yonchain.ai.idm.mapper;

import com.yonchain.ai.idm.entity.RoleMenuEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色与菜单关联 Mapper接口
 */
public interface RoleMenuMapper {

    /**
     * 根据ID查询菜单角色关联
     *
     * @param id 主键ID
     * @return 菜单角色关联实体
     */
    RoleMenuEntity selectById(@Param("id") String id);

    /**
     * 查询菜单角色关联列表
     *
     * @param params 查询参数
     * @return 菜单角色关联列表
     */
    List<RoleMenuEntity> selectList(@Param("params") Map<String, Object> params);

    /**
     * 新增菜单角色关联
     *
     * @param menuRole 菜单角色关联实体
     * @return 影响行数
     */
    int insert(RoleMenuEntity menuRole);

    /**
     * 根据ID删除菜单角色关联
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据菜单ID删除关联
     *
     * @param menuId 菜单ID
     * @return 影响行数
     */
    int deleteByMenuId(@Param("menuId") String menuId);

    /**
     * 根据角色ID删除关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") String roleId);

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<String> selectMenuIdsByRoleId(@Param("roleId") String roleId);

    /**
     * 根据菜单ID查询角色ID列表
     *
     * @param menuId 菜单ID
     * @return 角色ID列表
     */
    List<String> selectRoleIdsByMenuId(@Param("menuId") String menuId);

    /**
     * 批量插入菜单角色关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 影响行数
     */
    int batchInsert(@Param("roleId") String roleId, @Param("menuIds") List<String> menuIds);
}