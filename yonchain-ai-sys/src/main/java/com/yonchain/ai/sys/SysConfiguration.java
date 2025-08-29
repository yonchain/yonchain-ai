package com.yonchain.ai.sys;

import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.sys.mapper.*;
import com.yonchain.ai.sys.service.*;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 系统管理 配置类
 *
 * @author Cgy
 */
public class SysConfiguration {

    // 服务
    protected UserService userService;
    protected TenantService tenantService;
    protected RoleService roleService;
    protected SecurityService securityService;
    protected IdmService idmService;
    protected IdmCacheService idmCacheService;

    // 数据访问层依赖
    protected UserMapper userMapper;
    protected TenantMapper tenantMapper;
    protected UserTenantMapper tenantAccountJoinMapper;
    protected RoleMapper roleMapper;
    protected RolePermissionMapper rolePermissionMapper;

    //默认系统租户id
    protected String defaultTenantId;

    //默认系统超级管理员角色id
    protected String defaultAdminRoleId;

    //缓存模板
    protected RedisTemplate<String, Object> redisTemplate;


    protected SysConfiguration(Builder builder) {
        this.securityService = builder.securityService;
        this.userService = builder.userService;
        this.tenantService = builder.tenantService;
        this.roleService = builder.roleService;
        this.userMapper = builder.userMapper;
        this.tenantMapper = builder.tenantMapper;
        this.roleMapper = builder.roleMapper;
        this.tenantAccountJoinMapper = builder.tenantAccountJoinMapper;
        this.rolePermissionMapper = builder.rolePermissionMapper;
        this.redisTemplate = builder.redisTemplate;

        init();
    }

    /**
     * 初始化
     */
    protected void init() {

        // 初始化IdmCacheService
        if (this.idmCacheService == null) {
            this.idmCacheService = new RedisCacheService(redisTemplate);
        }

        // 初始化RoleService
        if (this.roleService == null) {
            this.roleService = new RoleServiceImpl(this.roleMapper, this.rolePermissionMapper);
        }

        // 初始化UserService
        if (this.userService == null) {
            this.userService = new UserServiceImpl(this.userMapper, this.securityService, rolePermissionMapper,
                    this.roleMapper, this.tenantAccountJoinMapper);
        }

        // 初始化TenantService
        if (this.tenantService == null) {
            this.tenantService = new TenantServiceImpl(this.tenantMapper, this.tenantAccountJoinMapper,
                    this.roleMapper, this.rolePermissionMapper, this.idmCacheService);
        }

        // 初始化IdmService
        this.idmService = new DefaultIdmServiceImpl(this.tenantService, this.userService, this.roleService);
    }

    /**
     * 获取UserService实例
     * <p>
     * 返回在构造时已初始化的实例
     * </p>
     *
     * @return UserService实例
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * 获取TenantService实例
     * <p>
     * 返回在构造时已初始化的实例
     * </p>
     *
     * @return TenantService实例
     */
    public TenantService getTenantService() {
        return tenantService;
    }

    /**
     * 获取RoleService实例
     * <p>
     * 返回在构造时已初始化的实例
     * </p>
     *
     * @return RoleService实例
     */
    public RoleService getRoleService() {
        return roleService;
    }

    /**
     * 获取IdmService实例
     * <p>
     * 返回在构造时已初始化的实例
     * </p>
     *
     * @return IdmService实例
     */
    public IdmService getIdmService() {
        return idmService;
    }

    /**
     * 创建一个Builder实例
     *
     * @return 新的Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * IdmConfiguration的Builder类
     */
    public static class Builder {

        protected SecurityService securityService;
        protected UserMapper userMapper;
        protected TenantMapper tenantMapper;
        protected UserTenantMapper tenantAccountJoinMapper;
        protected RoleMapper roleMapper;
        protected UserService userService;
        protected TenantService tenantService;
        protected RoleService roleService;
        protected RolePermissionMapper rolePermissionMapper;
        protected RedisTemplate<String, Object> redisTemplate;

        protected Builder() {
            // 受保护的构造函数，允许子类继承
        }

        public Builder securityService(SecurityService securityService) {
            this.securityService = securityService;
            return this;
        }

        public Builder userMapper(UserMapper userMapper) {
            this.userMapper = userMapper;
            return this;
        }

        public Builder tenantMapper(TenantMapper tenantMapper) {
            this.tenantMapper = tenantMapper;
            return this;
        }

        public Builder tenantAccountJoinMapper(UserTenantMapper tenantAccountJoinMapper) {
            this.tenantAccountJoinMapper = tenantAccountJoinMapper;
            return this;
        }

        public Builder roleMapper(RoleMapper roleMapper) {
            this.roleMapper = roleMapper;
            return this;
        }

        public Builder userService(UserService userService) {
            this.userService = userService;
            return this;
        }

        public Builder tenantService(TenantService tenantService) {
            this.tenantService = tenantService;
            return this;
        }

        public Builder roleService(RoleService roleService) {
            this.roleService = roleService;
            return this;
        }

        public Builder rolePermissionMapper(RolePermissionMapper rolePermissionMapper) {
            this.rolePermissionMapper = rolePermissionMapper;
            return this;
        }

        public Builder redisTemplate(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }

        /**
         * 构建IdmConfiguration实例
         *
         * @return 新的IdmConfiguration实例
         * @throws IllegalStateException 如果配置无效
         */
        public SysConfiguration build() {
            validate();
            return new SysConfiguration(this);
        }

        /**
         * 验证Builder配置是否有效
         * 确保所有必要的依赖都已配置，以便在构造函数中初始化服务实例
         */
        protected void validate() {
            // 检查UserService的依赖
            if (userService == null) {
                if (userMapper == null) {
                    throw new IllegalStateException("未配置UserMapper，必须配置UserMapper或直接设置UserService");
                }
                if (securityService == null) {
                    throw new IllegalStateException("未配置SecurityService，必须配置SecurityService或直接设置UserService");
                }
            }

            // 检查TenantService的依赖
            if (tenantService == null) {
                if (tenantMapper == null) {
                    throw new IllegalStateException("未配置TenantMapper，必须配置TenantMapper或直接设置TenantService");
                }
            }

            // 检查RoleService的依赖
            if (roleService == null) {
                if (roleMapper == null) {
                    throw new IllegalStateException("未配置RoleMapper，必须配置RoleMapper或直接设置RoleService");
                }
            }

            // 检查IdmCacheService的依赖
            if (redisTemplate == null) {
                throw new IllegalStateException("未配置RedisTemplate，必须配置RedisTemplate");
            }
        }
    }
}
