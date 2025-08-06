package com.yonchain.ai.console.idm.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.idm.DefaultMenu;
import com.yonchain.ai.api.idm.Menu;
import com.yonchain.ai.api.idm.MenuService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.idm.request.MenuQueryRequest;
import com.yonchain.ai.console.idm.request.MenuRequest;
import com.yonchain.ai.console.idm.response.MenuResponse;
import com.yonchain.ai.console.idm.response.MenuTreeResponse;
import com.yonchain.ai.util.IdUtil;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/menus")
@Tag(name = "菜单管理", description = "菜单管理相关接口")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单响应
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单详情")
    public MenuResponse getMenuById(@PathVariable String id) {
        Menu menu = menuService.getMenuById(id);
        return this.responseFactory.createMenuResponse(menu);
    }

    /**
     * 获取菜单列表
     *
     * @param request 查询请求
     * @return 菜单列表
     */
    @GetMapping("/page")
    @Operation(summary = "获取菜单列表", description = "获取所有菜单列表")
    public PageResponse<MenuResponse> pageMenus(MenuQueryRequest request) {
        //查询条件
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("name", request.getMenuName());

        Page<Menu> menus = menuService.pageMenus(queryParam, request.getPage(), request.getLimit());

        return this.responseFactory.createMenuPageResponse(menus);
    }

    /**
     * 获取菜单树形结构
     *
     * @return 菜单树形结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取菜单树形结构", description = "获取菜单的树形结构")
    public ListResponse<MenuTreeResponse> getTreeList(@RequestParam(name = "parentId", required = false) String parentId,
                                                      @RequestParam(name = "menuName", required = false) String menuName,
                                                      @RequestParam(name = "type", required = false) String type) {
        Map<String, Object> queryParam = new HashMap<>();
        if (StringUtils.hasText(parentId)) {
            queryParam.put("parentId", parentId);
        }
        if (StringUtils.hasText(menuName)) {
            queryParam.put("name", menuName);
        }
        if (StringUtils.hasText(type)) {
            queryParam.put("menuType", type);
        }
        List<Menu> menus = menuService.getMenus(queryParam);
        return responseFactory.createMenuTreeListResponse(menus);
    }


    /**
     * 创建菜单
     *
     * @param request 菜单请求
     * @return 菜单响应
     */
    @PostMapping
    @Operation(summary = "创建菜单", description = "创建一个新的菜单")
    public MenuResponse create(@Valid @RequestBody MenuRequest request) {
        Menu menu = new DefaultMenu();
        menu.setId(IdUtil.generateId());

        this.populateMenuFromRequest(menu, request);

        menuService.createMenu(menu);

        return this.responseFactory.createMenuResponse(menuService.getMenuById(menu.getId()));
    }

    /**
     * 更新菜单
     *
     * @param id      菜单ID
     * @param request 菜单请求
     * @return 菜单响应
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新菜单", description = "根据ID更新菜单信息")
    public MenuResponse update(@PathVariable String id, @Valid @RequestBody MenuRequest request) {
        Menu menu = this.getMenuFromRequest(id);

        this.populateMenuFromRequest(menu, request);

        menuService.updateMenu(menu);

        return this.responseFactory.createMenuResponse(menu);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    public ApiResponse<Void> delete(@PathVariable String id) {
        menuService.deleteMenuById(id);
        return ApiResponse.success();
    }

    /**
     * 获取菜单实体
     *
     * @param id
     * @return
     */
    private Menu getMenuFromRequest(String id) {
        Menu menu = menuService.getMenuById(id);
        if (menu == null) {
            throw new YonchainResourceNotFoundException("菜单不存在");
        }
        return menu;
    }

    /**
     * 将用户实体转换为请求对象
     *
     * @param menu    菜单实体
     * @param request 菜单请求
     */
    private void populateMenuFromRequest(Menu menu, MenuRequest request) {
        menu.setParentId(request.getParentId());
        menu.setWeight(request.getWeight());
        menu.setName(request.getName());
        menu.setPath(request.getPath());
        menu.setIsLink(request.getIsLink());
        menu.setIsIframe(request.getIsIframe());
        menu.setIsKeepAlive(request.getIsKeepAlive());
        menu.setIcon(request.getIcon());
        menu.setEnName(request.getEnName());
        menu.setIsAffix(request.getIsAffix());
        menu.setTitle(request.getName());//使用菜单名称
        menu.setIsHide(request.getIsHide());
        menu.setSortOrder(request.getSortOrder());
        menu.setMenuType(request.getMenuType());
        menu.setPermission(request.getPermission());
    }

}
