package com.yonchain.ai.sys.mapper;

import com.yonchain.ai.api.sys.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 菜单Mapper接口
 */
public interface MenuMapper {

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单实体
     */
    Menu selectById(@Param("id") String id);

    /**
     * 查询菜单列表
     *
     * @param params 查询参数
     * @return 菜单列表
     */
    List<Menu> selectList(@Param("params") Map<String, Object> params);

    /**
     * 插入一条菜单记录
     *
     * @param menu 菜单实体
     * @return 影响的行数
     */
    int insert(Menu menu);

    /**
     * 根据ID更新菜单信息
     *
     * @param menu 菜单实体
     * @return 影响的行数
     */
    int updateById(Menu menu);

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     */
    void deleteByIds(@Param("ids") List<String> ids);

    /**
     * 查询用户关联的菜单列表
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param menuType 菜单类型
     * @return 菜单列表
     */
    List<Menu> selectUserMenus(@Param("tenantId") String tenantId, @Param("userId") String userId, @Param("menuType") String menuType);
}
