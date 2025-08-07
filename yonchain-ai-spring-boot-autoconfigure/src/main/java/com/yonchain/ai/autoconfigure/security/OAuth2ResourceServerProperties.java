package com.dify4j.autoconfigure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OAuth2 资源服务器配置属性类
 *
 * @author Cgy
 */
@ConfigurationProperties("dify4j.security.oauth2.resourceserver")
public class OAuth2ResourceServerProperties {

    /**
     * 系统默认的必需放行路径（不可被覆盖）
     */
    private static final List<String> DEFAULT_PERMIT_PATHS = Arrays.asList(
            //dify登录
            "/login/**",
            //图形验证码
            "/captcha",
            //swagger
            "/swagger-ui/**",
            "/v3/api-docs/**",
            //文件预览
            "/files/*/file-preview"
    );

    /**
     * 放行的路径列表
     */
    private List<String> permitPaths = new ArrayList<>();


    /**
     * 获取放行路径列表
     *
     * @return
     */
    public List<String> getPermitPaths() {
        return Stream.concat(DEFAULT_PERMIT_PATHS.stream(), permitPaths.stream()).collect(Collectors.toList());
    }

    /**
     * 设置放行路径列表
     *
     * @param permitPaths 放行路径列表
     */
    public void setPermitPaths(List<String> permitPaths) {
        this.permitPaths = permitPaths;
    }
}
