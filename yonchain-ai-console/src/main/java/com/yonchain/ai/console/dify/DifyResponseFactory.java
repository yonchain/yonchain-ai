package com.yonchain.ai.console.dify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.api.app.Application;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.console.app.response.AppResponse;
import com.yonchain.ai.console.dify.response.DifyAppResponse;
import com.yonchain.ai.console.file.entity.FileEntity;
import com.yonchain.ai.console.file.response.FileResponse;
import com.yonchain.ai.console.sys.response.*;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;

/**
 * 响应工厂类，用于创建和构建API响应对象
 * 提供标准化的响应格式和错误处理机制
 *
 * @author Cgy
 * @since 1.0.0
 */
public class DifyResponseFactory {

    /**
     * 创建用户响应对象
     * <p>
     * 将User实体对象转换为标准化的API响应格式
     * 包含用户的基本信息和状态
     * </p>
     *
     * @param user 用户实体对象，包含用户基本信息，不能为null
     * @return 标准化后的用户响应对象，包含用户ID、名称、邮箱等信息
     * @see User
     * @see UserResponse
     */
    public UserResponse createUserResponse(User user) {
        UserResponse response = new UserResponse();
        // 设置用户ID
        response.setId(user.getId());
        // 设置用户名称
        response.setName(user.getName());
        // 设置用户邮箱
        response.setEmail(user.getEmail());
        // 设置用户头像URL
        response.setAvatar(user.getAvatar());
        // 设置用户状态
        response.setStatus(user.getStatus());
        // 设置创建时间
        response.setCreatedAt(user.getCreatedAt());
        //设置最后活跃时间
        response.setLastActiveAt(user.getLastActiveAt());
        return response;
    }

    /**
     * 创建用户分页响应对象
     * 根据传入的用户分页数据构建标准化的分页响应
     *
     * @param users 用户分页数据对象，包含分页信息和用户数据列表
     * @return 标准化后的用户分页响应对象
     */
    public PageResponse<UserResponse> createUserPageResponse(Page<User> users) {
        PageResponse<UserResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPage(users.getCurrent());
        // 设置每页记录数
        response.setLimit(users.getSize());
        // 设置总记录数
        response.setTotal(users.getTotal());
        // 设置用户数据列表
        response.setData(users.getRecords().stream()
                .map(this::createUserResponse)
                .toList());
        return response;
    }


    /**
     * 创建角色响应对象
     * <p>
     * 将Role实体对象转换为标准化的API响应格式
     * 包含角色的基本信息和权限配置
     * </p>
     *
     * @param role 角色实体对象，包含角色基本信息，不能为null
     * @return 标准化后的角色响应对象，包含角色ID、名称、编码等信息
     * @see Role
     * @see RoleResponse
     */
    public RoleResponse createRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        // 设置角色ID
        roleResponse.setId(role.getId());
        // 设置角色名称
        roleResponse.setName(role.getName());
        // 设置角色编码
        roleResponse.setCode(role.getCode());
        // 设置角色描述
        roleResponse.setDescription(role.getDescription());
        // 设置角色状态
        roleResponse.setStatus(role.getStatus());
        // 设置创建时间
        roleResponse.setCreatedAt(role.getCreatedAt());
        // 设置创建人
        roleResponse.setCreatedBy(role.getCreatedBy());
        // 设置更新时间
        roleResponse.setUpdatedAt(role.getUpdatedAt());
        // 设置更新人
        roleResponse.setUpdatedBy(role.getUpdatedBy());
        // 设置角色所属组ID
        roleResponse.setGroupId(role.getGroupId());
        //设置角色类别
        roleResponse.setCategory(role.getCategory());
        return roleResponse;
    }

    /**
     * 创建角色分页响应对象
     * <p>
     * 将角色分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的角色数据列表
     * </p>
     *
     * @param page 角色分页数据对象，包含分页信息和角色数据列表，不能为null
     * @return 标准化后的角色分页响应对象，包含分页元数据和角色列表
     * @see Role
     * @see RoleResponse
     * @see PageResponse
     */
    public PageResponse<RoleResponse> createRolePageResponse(Page<Role> page) {
        PageResponse<RoleResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPage(page.getCurrent());
        // 设置每页记录数
        response.setLimit(page.getSize());
        // 设置总记录数
        response.setTotal(page.getTotal());
        // 设置角色数据列表
        response.setData(page.getRecords().stream()
                .map(this::createRoleResponse)
                .toList());
        return response;
    }

    /**
     * 创建角色列表响应对象
     * <p>
     * 将角色列表转换为标准化的API列表响应格式
     * 包含角色数据列表但不包含分页信息
     * </p>
     *
     * @param roles 角色列表，包含角色数据，不能为null
     * @return 标准化后的角色列表响应对象，包含角色数据列表
     * @see Role
     * @see RoleResponse
     * @see ListResponse
     */
    public ListResponse<RoleResponse> createRoleListResponse(List<Role> roles) {
        ListResponse<RoleResponse> response = new ListResponse<>();
        response.setData(roles.stream()
                .map(this::createRoleResponse)
                .toList());
        return response;
    }

    /**
     * 创建租户响应对象
     * <p>
     * 将Tenant实体对象转换为标准化的API响应格式
     * 包含租户的基本信息和配置
     * </p>
     *
     * @param tenant 租户实体对象，包含租户基本信息，不能为null
     * @return 标准化后的租户响应对象，包含租户ID、名称、计划等信息
     * @see Tenant
     * @see TenantResponse
     */
    public TenantResponse createTenantResponse(Tenant tenant) {
        TenantResponse tenantResponse = new TenantResponse();
        // 设置租户ID
        tenantResponse.setId(tenant.getId());
        // 设置租户名称
        tenantResponse.setName(tenant.getName());
        // 设置租户计划
        tenantResponse.setPlan(tenant.getPlan());
        // 设置租户状态
        tenantResponse.setStatus(tenant.getStatus());
        // 设置创建时间
        tenantResponse.setCreatedAt(tenant.getCreatedAt());
        // 设置更新时间
        tenantResponse.setUpdatedAt(tenant.getUpdatedAt());
        // 设置自定义配置
        tenantResponse.setCustomConfig(tenant.getCustomConfig());
        return tenantResponse;
    }

    /**
     * 创建租户分页响应对象
     * <p>
     * 将租户分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的租户数据列表
     * </p>
     *
     * @param tenantPage 租户分页数据对象，包含分页信息和租户数据列表，不能为null
     * @return 标准化后的租户分页响应对象，包含分页元数据和租户列表
     * @see Tenant
     * @see TenantResponse
     * @see PageResponse
     */
    public PageResponse<TenantResponse> createTenantPageResponse(Page<Tenant> tenantPage) {
        PageResponse<TenantResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPage(tenantPage.getCurrent());
        // 设置每页记录数
        response.setLimit(tenantPage.getSize());
        // 设置总记录数
        response.setTotal(tenantPage.getTotal());
        // 设置租户数据列表
        response.setData(tenantPage.getRecords().stream()
                .map(this::createTenantResponse)
                .toList());
        return response;
    }

    /**
     * 创建租户列表响应对象
     * <p>
     * 将租户列表转换为标准化的API列表响应格式
     * 包含租户数据列表但不包含分页信息
     * </p>
     *
     * @param tenant 租户列表，包含租户数据，不能为null
     * @return 标准化后的租户列表响应对象，包含租户数据列表
     * @see Tenant
     * @see TenantResponse
     * @see ListResponse
     */
    public ListResponse<TenantResponse> createTenantListResponse(List<Tenant> tenant) {
        ListResponse<TenantResponse> response = new ListResponse<>("workspaces");
        response.setData(tenant.stream()
                .map(this::createTenantResponse)
                .toList());
        return response;
    }


    /**
     * 创建应用响应对象
     * <p>
     * 将App实体对象转换为标准化的API响应格式
     * 包含应用的基本信息、状态和时间戳
     * </p>
     *
     * @param app 应用实体对象，包含应用基本信息，可为null
     * @return 标准化后的应用响应对象，如果输入为null则返回null
     * @throws NumberFormatException 如果状态转换失败
     */
    public DifyAppResponse createAppResponse(DifyApp app) {
        DifyAppResponse response = new DifyAppResponse();
        // 设置应用ID
        response.setId(app.getId());
        // 设置租户ID
        response.setTenantId(app.getTenantId());
        // 设置应用名称
        response.setName(app.getName());
        // 设置应用模式
        response.setMode(app.getMode());
        // 设置应用图标URL
        response.setIcon(app.getIcon());
        // 设置图标背景色
        response.setIconBackground(app.getIconBackground());
        // 设置应用描述
        response.setDescription(app.getDescription());
        // 设置应用状态
        response.setStatus(app.getStatus());
        // 设置站点启用状态
        response.setEnableSite(app.getEnableSite());
        // 设置API启用状态
        response.setEnableApi(app.getEnableApi());
        // 设置是否为演示应用
        response.setIsDemo(app.getIsDemo());
        // 设置是否公开
        response.setIsPublic(app.getIsPublic());
        // 设置是否通用应用
        response.setIsUniversal(app.getIsUniversal());
        // 设置是否使用应用图标作为回答图标
       // response.setUseIconAsAnswerIcon(app.getUseIconAsAnswerIcon());
        // 设置API每分钟请求限制
        response.setApiRpm(app.getApiRpm());
        // 设置API每小时请求限制
        response.setApiRph(app.getApiRph());
        // 设置最大并发请求数
        response.setMaxActiveRequests(app.getMaxActiveRequests());
        // 设置工作流ID
        response.setWorkflowId(app.getWorkflowId());
        // 设置应用模型配置ID
        response.setAppModelConfigId(app.getAppModelConfigId());
        // 设置追踪配置
        response.setTracing(app.getTracing());
        // 设置创建时间
        response.setCreatedAt(app.getCreatedAt());
        // 设置更新时间
        response.setUpdatedAt(app.getUpdatedAt());
        return response;
    }

    /**
     * 创建菜单响应对象
     * <p>
     * 将Menu实体对象转换为标准化的API响应格式
     * 包含菜单的基本信息、元数据和权限标识
     * </p>
     *
     * @param menu 菜单实体对象，包含菜单基本信息，不能为null
     * @return 标准化后的菜单响应对象，包含菜单ID、名称、路径等信息
     * @see Menu
     * @see MenuResponse
     */
    public MenuResponse createMenuResponse(Menu menu) {
        if (menu == null) {
            return null;
        }

        MenuResponse response = new MenuResponse();
        // 设置菜单ID
        response.setId(menu.getId());
        // 设置父菜单ID
        response.setParentId(menu.getParentId());
        // 设置权重
        response.setWeight(menu.getWeight());
        // 设置菜单名称
        response.setName(menu.getName());
        // 设置菜单路径
        response.setPath(menu.getPath());
        // 设置是否固定标签
        response.setIsAffix(menu.getIsAffix());
        // 设置是否隐藏
        response.setIsHide(menu.getIsHide());
        // 设置菜单图标
        response.setIcon(menu.getIcon());
        // 设置英文名称
        response.setEnName(menu.getEnName());
        // 设置菜单标题
        response.setTitle(menu.getTitle());
        // 设置是否外链
        response.setIsLink(menu.getIsLink());
        // 设置是否内嵌
        response.setIsIframe(menu.getIsIframe());
        // 设置是否缓存
        response.setIsKeepAlive(menu.getIsKeepAlive());
        // 设置排序顺序
        response.setSortOrder(menu.getSortOrder());
        // 设置菜单类型
        response.setMenuType(menu.getMenuType());
        // 设置权限标识
        response.setPermission(menu.getPermission());
        // 设置创建时间
        response.setCreatedAt(menu.getCreatedAt());
        // 设置更新时间
        response.setUpdatedAt(menu.getUpdatedAt());
        return response;
    }

    /**
     * 创建菜单分页响应对象
     * <p>
     * 将菜单分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的菜单数据列表
     * </p>
     *
     * @param menus 菜单分页数据对象，包含分页信息和菜单数据列表，不能为null
     * @return 标准化后的菜单分页响应对象，包含分页元数据和菜单列表
     * @see Menu
     * @see MenuResponse
     * @see PageResponse
     */
    public PageResponse<MenuResponse> createMenuPageResponse(Page<Menu> menus) {
        PageResponse<MenuResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPage(menus.getCurrent());
        // 设置每页记录数
        response.setLimit(menus.getSize());
        // 设置总记录数
        response.setTotal(menus.getTotal());
        // 设置菜单数据列表
        response.setData(menus.getRecords().stream()
                .map(this::createMenuResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }

    /**
     * 创建菜单树形结构响应对象
     * <p>
     * 将树形结构的菜单实体列表转换为标准化的树形结构响应格式
     * 保留原有的层级关系并进行排序
     * </p>
     *
     * @param menus 树形结构的菜单实体列表，不能为null
     * @return 标准化后的菜单树形结构响应对象，包含层级关系的菜单列表
     * @see Menu
     * @see MenuTreeResponse
     * @see ListResponse
     */
    public ListResponse<MenuTreeResponse> createMenuTreeListResponse(List<Menu> menus) {
        ListResponse<MenuTreeResponse> response = new ListResponse<>();

        if (menus == null || menus.isEmpty()) {
            return response;
        }

        // 构建菜单映射表和根菜单列表
        Map<String, MenuTreeResponse> menuMap = new HashMap<>();
        List<MenuTreeResponse> rootMenus = new ArrayList<>();

        // 第一遍遍历：创建所有菜单节点并放入映射表
        for (Menu menu : menus) {
            MenuTreeResponse menuTree = new MenuTreeResponse();
            // 复制基本属性
            BeanUtils.copyProperties(createMenuResponse(menu), menuTree);

            // 设置菜单元数据
            MenuTreeResponse.MenuMetaResponse metaResponse = new MenuTreeResponse.MenuMetaResponse();
            metaResponse.setIsLink(menu.getIsLink());
            metaResponse.setIsIframe(menu.getIsIframe());
            metaResponse.setIsKeepAlive(menu.getIsKeepAlive());
            metaResponse.setIcon(menu.getIcon());
            metaResponse.setEnName(menu.getEnName());
            metaResponse.setIsAffix(menu.getIsAffix());
            metaResponse.setTitle(menu.getTitle());
            metaResponse.setIsHide(menu.getIsHide());
            menuTree.setMeta(metaResponse);

            menuMap.put(menu.getId(), menuTree);
        }

        // 第二遍遍历：构建树形结构
        for (MenuTreeResponse menuTree : menuMap.values()) {
            String parentId = menuTree.getParentId();
            //b61804f0-e99e-4c15-9f9c-0784b125888b--根菜单
            if (parentId == null || "b61804f0-e99e-4c15-9f9c-0784b125888b".equals(parentId)) {
                rootMenus.add(menuTree);
            } else {
                MenuTreeResponse parentMenu = menuMap.get(parentId);
                if (parentMenu != null) {
                    List<MenuTreeResponse> children = parentMenu.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parentMenu.setChildren(children);
                    }
                    children.add(menuTree);
                }
            }
        }

        // 递归排序整个树
        Comparator<MenuTreeResponse> menuComparator = Comparator
                .comparing(MenuTreeResponse::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(MenuTreeResponse::getWeight, Comparator.nullsFirst(Comparator.naturalOrder()));

        rootMenus.sort(menuComparator);
        for (MenuTreeResponse menu : rootMenus) {
            sortMenuTree(menu, menuComparator);
        }

        response.setData(rootMenus);
        return response;
    }

    /**
     * 递归排序菜单树
     */
    private void sortMenuTree(MenuTreeResponse menuTree, Comparator<MenuTreeResponse> comparator) {
        if (menuTree.getChildren() != null && !menuTree.getChildren().isEmpty()) {
            menuTree.getChildren().sort(comparator);
            for (MenuTreeResponse child : menuTree.getChildren()) {
                sortMenuTree(child, comparator);
            }
        }
    }


    /**
     * 创建文件响应对象
     * <p>
     * 将FileEntity实体对象转换为标准化的API响应格式
     * 包含文件的基本信息、URL和元数据
     * </p>
     *
     * @param file 文件实体对象，包含文件基本信息，不能为null
     * @return 标准化后的文件响应对象，包含文件ID、名称、URL、大小等信息
     * @see FileEntity
     * @see FileResponse
     */
    public FileResponse createFileResponse(FileEntity file) {
        if (file == null) {
            return null;
        }

        FileResponse response = new FileResponse();
        // 设置文件ID
        response.setId(file.getId());
        // 设置租户ID
        response.setTenantId(file.getTenantId());
        // 设置文件存储类型
        response.setStorageType(file.getStorageType());
        // 设置文件存储键
        response.setKey(file.getKey());
        // 设置文件名称
        response.setName(file.getName());
        // 设置文件大小(字节)
        response.setSize(file.getSize());
        // 设置文件扩展名
        response.setExtension(file.getExtension());
        // 设置文件MIME类型
        response.setMimeType(file.getMimeType());
        // 设置创建人ID
        response.setCreatedBy(file.getCreatedBy());
        // 设置创建时间
        response.setCreatedAt(file.getCreatedAt());
        // 设置是否已使用
        response.setUsed(file.isUsed());
        // 设置使用者ID
        response.setUsedBy(file.getUsedBy());
        // 设置使用时间
        response.setUsedAt(file.getUsedAt());
        // 设置文件哈希值
        response.setHash(file.getHash());
        // 设置创建人角色
        response.setCreatedByRole(file.getCreatedByRole());
        // 设置源文件URL
        response.setSourceUrl(file.getSourceUrl());
        return response;
    }

    /**
     * 创建文件分页响应对象
     * <p>
     * 将文件分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的文件数据列表
     * </p>
     *
     * @param files 文件分页数据对象，包含分页信息和文件数据列表，不能为null
     * @return 标准化后的文件分页响应对象，包含分页元数据和文件列表
     * @see FileEntity
     * @see FileResponse
     * @see PageResponse
     */
    public PageResponse<FileResponse> createFilePageResponse(Page<FileEntity> files) {
        PageResponse<FileResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPage(files.getCurrent());
        // 设置每页记录数
        response.setLimit(files.getSize());
        // 设置总记录数
        response.setTotal(files.getTotal());
        // 设置文件数据列表
        response.setData(files.getRecords().stream()
                .map(this::createFileResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }

    /**
     * 创建角色组列表响应对象
     * <p>
     * 将RoleGroup实体对象转换为标准化的API响应格式
     * 包含角色组的基本信息、权限标识和时间戳
     * </p>
     *
     * @param groups 角色组列表
     * @return 标准化后的角色组响应对象，包含角色组ID、名称、权限标识等信息
     * @see RoleGroup
     * @see RoleGroupResponse
     */
    public ListResponse<RoleGroupResponse> buildRoleGroupListResponse(List<RoleGroup> groups) {
        ListResponse<RoleGroupResponse> response = new ListResponse<>();
        response.setData(groups
                .stream().
                map(this::buildRoleGroupResponse)
                .toList());
        return response;
    }


    /**
     * 创建角色组响应对象
     * <p>
     * 将RoleGroup实体对象转换为标准化的API响应格式
     * 包含角色组的基本信息、权限标识和时间戳
     * </p>
     *
     * @param roleGroup 角色组实体对象，包含角色组基本信息，不能为null
     * @return 标准化后的角色组响应对象，包含角色组ID、名称、权限标识等信息
     * @see RoleGroup
     * @see RoleGroupResponse
     */
    public RoleGroupResponse buildRoleGroupResponse(RoleGroup roleGroup) {
        RoleGroupResponse response = new RoleGroupResponse();
        // 设置角色组ID
        response.setId(roleGroup.getId());
        // 设置角色组名称
        response.setName(roleGroup.getName());
        // 设置角色组类型
        response.setCategory(roleGroup.getCategory());
        // 设置创建人ID
        response.setCreatedBy(roleGroup.getCreatedBy());
        // 设置创建时间
        response.setCreatedAt(roleGroup.getCreatedAt());
        // 设置更新人ID
        response.setUpdatedBy(roleGroup.getUpdatedBy());
        // 设置更新时间
        response.setUpdatedAt(roleGroup.getUpdatedAt());
        return response;
    }

}
