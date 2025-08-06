package com.yonchain.ai.api.idm;

public class DefaultIdmServiceImpl implements IdmService {

    private final TenantService tenantService;
    private final UserService userService;
    private final RoleService roleService;

    public DefaultIdmServiceImpl(TenantService tenantService, UserService userService, RoleService roleService) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public TenantService getTenantService() {
        return tenantService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public RoleService getRoleService() {
        return roleService;
    }
}
