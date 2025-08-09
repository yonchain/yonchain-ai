package com.yonchain.ai.sys.service;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainForbiddenException;
import com.yonchain.ai.api.sys.DefaultMenuTree;
import com.yonchain.ai.api.sys.Menu;
import com.yonchain.ai.api.sys.MenuService;
import com.yonchain.ai.api.sys.MenuTree;
import com.yonchain.ai.sys.mapper.MenuMapper;
import com.yonchain.ai.sys.mapper.RoleMenuMapper;
import com.yonchain.ai.util.PageUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 菜单服务实现类
 */
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    private final RoleMenuMapper roleMenuMapper;

    public MenuServiceImpl(MenuMapper menuMapper, RoleMenuMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public Menu getMenuById(String id) {
        return menuMapper.selectById(id);
    }

    @Override
    public Page<Menu> pageMenus(Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        // 分页查询
        List<Menu> entities = menuMapper.selectList(queryParam);

        return PageUtil.convert(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMenu(Menu entity) {

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // 生成ID
        if (!StringUtils.hasText(entity.getId())) {
            entity.setId(UUID.randomUUID().toString().replace("-", ""));
        }

        menuMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Menu entity) {

        entity.setUpdatedAt(LocalDateTime.now());

        menuMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuById(String id) {
        //查询是否有子菜单，如果有则不允许删除
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("parentId", id);
        queryParam.put("menuType", 0);
        List<Menu> menuList = menuMapper.selectList(queryParam);
        if (menuList != null && !menuList.isEmpty()) {
            throw new YonchainForbiddenException("存在子节点，不允许删除");
        }

        menuMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(String roleId, List<String> menuIds) {
        // 先删除角色原有的菜单关联
        roleMenuMapper.deleteByRoleId(roleId);

        // 如果菜单ID列表不为空，则添加新的关联
        if (menuIds != null && !menuIds.isEmpty()) {
            // 批量插入菜单角色关联
            roleMenuMapper.batchInsert(roleId, menuIds);
        }
    }

    @Override
    public List<Menu> getMenus(Map<String, Object> queryParam) {
        return menuMapper.selectList(queryParam);
    }

    @Override
    public List<String> getMenusByRoleId(String roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    private List<MenuTree> buildTreeList(List<Menu> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建菜单映射表和根菜单列表
        Map<String, MenuTree> menuMap = new HashMap<>();
        List<MenuTree> rootMenus = new ArrayList<>();

        // 第一遍遍历：创建所有菜单节点并放入映射表
        for (Menu menu : menuList) {
            DefaultMenuTree menuTree = new DefaultMenuTree();
            BeanUtils.copyProperties(menu, menuTree);
            menuMap.put(menu.getId(), menuTree);
        }

        // 第二遍遍历：构建树形结构
        for (MenuTree menuTree : menuMap.values()) {
            String parentId = menuTree.getParentId();
            if (parentId == null || "-1".equals(parentId)) {
                rootMenus.add(menuTree);
            } else {
                DefaultMenuTree parentMenu = (DefaultMenuTree) menuMap.get(parentId);
                if (parentMenu != null) {
                    parentMenu.addChild((DefaultMenuTree) menuTree);
                }
            }
        }

        // 递归排序整个树
        Comparator<Menu> menuComparator = Comparator
                .comparing(Menu::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Menu::getWeight, Comparator.nullsFirst(Comparator.naturalOrder()));

        rootMenus.sort(menuComparator);
        for (MenuTree menu : rootMenus) {
            sortMenuTree((DefaultMenuTree) menu, menuComparator);
        }

        return rootMenus;
    }

    /**
     * 递归排序菜单树
     */
    private void sortMenuTree(DefaultMenuTree menuTree, Comparator<Menu> comparator) {
        if (menuTree.getChildren() != null && !menuTree.getChildren().isEmpty()) {
            menuTree.getChildren().sort(comparator);
            for (DefaultMenuTree child : menuTree.getChildren()) {
                sortMenuTree(child, comparator);
            }
        }
    }

}
