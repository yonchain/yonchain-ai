package com.yonchain.ai.autoconfigure.idm;

import com.yonchain.ai.api.idm.*;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.idm.IdmConfiguration;
import com.yonchain.ai.idm.mapper.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 身份证服务 自动装配配置
 *
 * @author Cgy
 */
@AutoConfiguration(after = SecurityAutoConfiguration.class)
@ConditionalOnClass(IdmConfiguration.class)
public class IdmAutoConfiguration {

    /**
     * 创建IdmConfiguration Bean
     * 使用@Lazy注解解决与SecurityAutoConfiguration的循环依赖问题
     *
     * @param securityService 安全服务，使用@Lazy延迟加载
     * @return IdmConfiguration实例
     */
    @Bean
    @ConditionalOnMissingBean
    public IdmConfiguration idmConfiguration(@Lazy SecurityService securityService,
                                             SqlSessionTemplate sqlSessionTemplate,
                                              RedisTemplate<String, Object> redisTemplate) {
        UserMapper userMapper = sqlSessionTemplate.getMapper(UserMapper.class);
        RoleMapper roleMapper = sqlSessionTemplate.getMapper(RoleMapper.class);
        TenantMapper tenantMapper = sqlSessionTemplate.getMapper(TenantMapper.class);
        TenantAccountJoinMapper tenantAccountJoinMapper = sqlSessionTemplate.getMapper(TenantAccountJoinMapper.class);
        MenuMapper menuMapper = sqlSessionTemplate.getMapper(MenuMapper.class);
        RoleMenuMapper roleMenuMapper = sqlSessionTemplate.getMapper(RoleMenuMapper.class);
        RoleGroupMapper roleGroupMapper = sqlSessionTemplate.getMapper(RoleGroupMapper.class);
        return IdmConfiguration.builder()
                .securityService(securityService)
                .userMapper(userMapper)
                .roleMapper(roleMapper)
                .tenantAccountJoinMapper(tenantAccountJoinMapper)
                .tenantMapper(tenantMapper)
                .menuMapper(menuMapper)
                .roleMenuMapper(roleMenuMapper)
                .roleGroupMapper(roleGroupMapper)
                .redisTemplate(redisTemplate)
                .build();
    }

    /**
     * 创建UserService Bean
     *
     * @param idmConfiguration IDM配置
     * @return UserService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public UserService userService(IdmConfiguration idmConfiguration) {
        return idmConfiguration.getUserService();
    }

    /**
     * 创建TenantService Bean
     *
     * @param idmConfiguration IDM配置
     * @return TenantService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantService tenantService(IdmConfiguration idmConfiguration) {
        return idmConfiguration.getTenantService();
    }

    /**
     * 创建RoleService Bean
     *
     * @param idmConfiguration IDM配置
     * @return RoleService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RoleService roleService(IdmConfiguration idmConfiguration) {
        return idmConfiguration.getRoleService();
    }

    /**
     * 创建MenuService Bean
     *
     * @param idmConfiguration IDM配置
     * @return MenuService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MenuService menuService(IdmConfiguration idmConfiguration) {
        return idmConfiguration.getMenuService();
    }


    /**
     * 创建IdmService Bean
     *
     * @param idmConfiguration IDM配置
     * @return IdmService实例
     */
    @Bean
    public IdmService idmService(IdmConfiguration idmConfiguration) {
        return idmConfiguration.getIdmService();
    }

}
