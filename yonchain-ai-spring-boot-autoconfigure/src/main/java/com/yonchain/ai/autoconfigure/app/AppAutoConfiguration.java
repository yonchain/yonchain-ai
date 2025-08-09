/*
package com.yonchain.ai.autoconfigure.app;

import com.yonchain.ai.api.app.AppService;
import com.yonchain.ai.api.idm.RoleService;
import com.yonchain.ai.app.mapper.AppMapper;
import com.yonchain.ai.app.mapper.AppRoleMapper;
import com.yonchain.ai.app.service.AppServiceImpl;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

*/
/**
 * 应用服务自动装配配置
 *
 * @author Cgy
 *//*

@AutoConfiguration
@ConditionalOnClass(AppServiceImpl.class)
public class AppAutoConfiguration {

    */
/**
     * 创建AppService Bean
     *
     * @param sqlSessionTemplate SQL会话模板
     * @param roleService 角色服务
     * @return AppService实例
     *//*

    @Bean
    @ConditionalOnMissingBean
    public AppService appService(SqlSessionTemplate sqlSessionTemplate, RoleService roleService) {
        AppMapper appMapper = sqlSessionTemplate.getMapper(AppMapper.class);
        AppRoleMapper appRoleMapper = sqlSessionTemplate.getMapper(AppRoleMapper.class);

        AppServiceImpl appService = new AppServiceImpl();
        // 手动设置依赖
        appService.setAppMapper(appMapper);
        appService.setAppRoleMapper(appRoleMapper);
        appService.setRoleService(roleService);

        return appService;
    }
}
*/
