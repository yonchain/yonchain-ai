package com.yonchain.ai.api.idm;

import com.yonchain.ai.api.common.Page;

import java.util.List;
import java.util.Map;

/**
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 根据ID获取菜单
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    Menu getMenuById(String id);

    /**
     * 分页查询菜单
     *
     * @param queryParam 查询参数
     * @param pageNum    当前页
     * @param pageSize   每页大小
     * @return 分页结果
     */
    Page<Menu> pageMenus(Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 获取菜单列表
     *
     * @param queryParam 查询参数
     * @return 菜单树形结构
     */
    List<Menu> getMenus(Map<String, Object> queryParam);

    /**
     * 获取角色菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<String> getMenusByRoleId(String roleId);

    /**
     * 创建菜单
     *
     * @param menu 菜单信息
     * @return 创建的菜单ID
     */
    String createMenu(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     */
    void updateMenu(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void deleteMenuById(String id);

    /**
     * 为角色分配菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void assignRoleMenus(String roleId, List<String> menuIds);

}
