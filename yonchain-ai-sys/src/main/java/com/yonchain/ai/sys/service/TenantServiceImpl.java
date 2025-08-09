package com.yonchain.ai.sys.service;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.sys.enums.RoleCategory;
import com.yonchain.ai.api.sys.enums.RoleGroupCategory;
import com.yonchain.ai.api.sys.enums.RoleType;
import com.yonchain.ai.sys.entity.UserTenantEntity;
import com.yonchain.ai.sys.mapper.*;
import com.yonchain.ai.util.IdUtil;
import com.yonchain.ai.util.PageUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 租户服务实现类
 *
 * @author Cgy
 */
public class TenantServiceImpl implements TenantService {

    private final TenantMapper tenantMapper;

    private final UserTenantMapper tenantAccountJoinMapper;

    private final RoleMapper roleMapper;

    private final RoleMenuMapper roleMenuMapper;

    private final MenuMapper menuMapper;

    private final RoleGroupMapper roleGroupMapper;

    private final IdmCacheService idmCacheService;

    public TenantServiceImpl(TenantMapper tenantMapper, UserTenantMapper tenantAccountJoinMapper,
                             RoleMapper roleMapper, RoleMenuMapper roleMenuMapper, MenuMapper menuMapper,
                             RoleGroupMapper roleGroupMapper,
                             IdmCacheService idmCacheService) {
        this.tenantMapper = tenantMapper;
        this.tenantAccountJoinMapper = tenantAccountJoinMapper;
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.menuMapper = menuMapper;
        this.roleGroupMapper = roleGroupMapper;
        this.idmCacheService = idmCacheService;
    }


    @Override
    public Tenant getTenantById(String id) {
        return tenantMapper.selectById(id);
    }

    @Override
    public Tenant getTenantDetailById(String id) {
        return tenantMapper.selectDetailById(id);
    }

    @Override
    public Tenant getCurrentTenantByUserId(String userId) {
        return tenantMapper.selectCurrentTenantByUserId(userId);
    }

    @Override
    public List<Tenant> getTenantsByUserId(String userId) {
        return tenantMapper.selectListByUserId(userId);
    }

    @Override
    public Page<Tenant> pageTenants(String userId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Tenant> tenant = tenantMapper.selectList(userId, queryParam);
        return PageUtil.convert(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTenant(Tenant tenant) {

        if (StringUtils.hasText(tenant.getId())) {
            tenant.setId(IdUtil.generateId());
        }
        // 如果未设置plan和status，设置默认值
        if (tenant.getPlan() == null) {
            tenant.setPlan("basic");
        }
        if (tenant.getStatus() == null) {
            tenant.setStatus("normal");
        }

        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());

        //插入租户
        tenantMapper.insert(tenant);

        //插入默认角色组
        RoleGroup roleGroup = new DefaultRoleGroup();
        roleGroup.setId(IdUtil.generateId());
        roleGroup.setTenantId(tenant.getId());
        roleGroup.setName("默认");
        roleGroup.setCategory(RoleGroupCategory.SYSTEM.getValue());
        roleGroup.setCreatedAt(LocalDateTime.now());
        roleGroupMapper.insert(roleGroup);

        //插入默认角色
        List<Role> systemRoles = new ArrayList<>();

        // 添加所有者角色 (OWNER)
        Role ownerRole = createRole(
                tenant.getId(),
                "所有者",
                RoleType.OWNER.getValue(),
                roleGroup.getId(),
                "系统默认角色，拥有所有权限",
                RoleCategory.SYSTEM.getValue(),
                "531a9f15-cfd7-456a-bda4-b4c3f7392800"
        );
        systemRoles.add(ownerRole);

        // 添加管理员角色 (ADMIN)
        systemRoles.add(createRole(
                tenant.getId(),
                "管理员",
                RoleType.ADMIN.getValue(),
                roleGroup.getId(),
                "系统默认角色，拥有大部分管理权限",
                RoleCategory.SYSTEM.getValue(),
                "531a9f15-cfd7-456a-bda4-b4c3f7392800"
        ));

        // 添加普通用户角色 (NORMAL)
        systemRoles.add(createRole(
                tenant.getId(),
                "普通用户",
                RoleType.NORMAL.getValue(),
                roleGroup.getId(),
                "系统默认角色，拥有基本操作权限",
                RoleCategory.SYSTEM.getValue(),
                "531a9f15-cfd7-456a-bda4-b4c3f7392800"
        ));

        // 添加编辑员角色 (EDITOR)
        systemRoles.add(createRole(
                tenant.getId(),
                "编辑员",
                RoleType.EDITOR.getValue(),
                roleGroup.getId(),
                "系统默认角色，拥有内容编辑权限",
                RoleCategory.SYSTEM.getValue(),
                "531a9f15-cfd7-456a-bda4-b4c3f7392800"
        ));

        // 批量插入系统角色
        roleMapper.batchInsert(systemRoles);

        // 添加管理员角色 (ADMIN)
        roleMenuMapper.batchInsert(ownerRole.getId(), menuMapper.selectList(new HashMap<>())
                .stream()
                .map(Menu::getId)
                .collect(Collectors.toList()));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTenant(Tenant tenant, String userId) {

        //创建租户
        this.createTenant(tenant);

        // 创建租户与账号、角色的关联
        UserTenantEntity tenantAccountJoin = new UserTenantEntity();
        tenantAccountJoin.setId(UUID.randomUUID().toString());
        tenantAccountJoin.setTenantId(tenant.getId());
        tenantAccountJoin.setAccountId(userId);
        tenantAccountJoin.setRole(RoleType.OWNER.getValue());
        tenantAccountJoin.setCurrent(false);
        tenantAccountJoin.setCreatedAt(new Date());
        tenantAccountJoin.setUpdatedAt(new Date());
        tenantAccountJoinMapper.insert(tenantAccountJoin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenant(Tenant tenant) {
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenantById(String id) {
        tenantMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchTenant(String tenantId, String userId) {
        tenantAccountJoinMapper.updateCurrentStatus(tenantId, userId);
        tenantAccountJoinMapper.updateOtherTenantsCurrentStatus(tenantId, userId);

        // 缓存当前租户信息
        idmCacheService.cacheTenant(userId,this.getCurrentTenantByUserId(userId));
    }

    /**
     * 创建角色辅助方法
     *
     * @param tenantId    租户ID
     * @param name        角色名称
     * @param code        角色代码
     * @param groupId     角色组id
     * @param description 角色描述
     * @param category    角色类别
     * @param createdBy   创建者ID
     * @return 角色对象
     */
    private Role createRole(String tenantId, String name, String code, String groupId, String description, String category, String createdBy) {
        Role role = new DefaultRole();
        role.setId(IdUtil.generateId());
        role.setName(name);
        role.setCode(code);
        role.setStatus("normal");
        role.setGroupId(groupId);
        role.setDescription(description);
        role.setCategory(category);
        role.setTenantId(tenantId);
        role.setCreatedBy(createdBy);
        role.setUpdatedBy(createdBy);
        LocalDateTime now = LocalDateTime.now();
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        return role;
    }
}
