package com.yonchain.ai.plugin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 插件静态资源配置
 * 配置插件图标文件的访问路径
 * 
 * @author yonchain
 */
@Configuration
public class PluginStaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${yonchain.plugin.icon.storage-path:${java.io.tmpdir}/yonchain-plugins/icons}")
    private String iconStoragePath;
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置插件图标的静态资源访问路径
        registry.addResourceHandler("/plugins/icons/**")
                .addResourceLocations("file:" + iconStoragePath + "/")
                .setCachePeriod(3600); // 缓存1小时
    }
}
