package com.yonchain.ai.service;

import com.yonchain.ai.entity.PluginEntity;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 插件管理服务
 * 负责插件的完整生命周期管理：安装、启用、禁用、卸载、更新
 *
 * @author yonchain
 * @version 1.0.0
 */
public interface PluginService {

    /**
     * 获取所有插件列表
     */
    List<PluginEntity> getPlugins();

    /**
     * 获取已安装的插件列表
     */
    List<PluginEntity> getInstalledPlugins();

    /**
     * 获取已启用的插件列表
     */
    List<PluginEntity> getEnabledPlugins();

    /**
     * 获取运行中的插件列表
     */
    List<PluginEntity> getRunningPlugins();

    /**
     * 安装插件
     */
    String installPlugin(MultipartFile jarFile, String installSource);

    /**
     * 启用插件
     */
    void enablePlugin(String pluginId);

    /**
     * 禁用插件
     */
    void disablePlugin(String pluginId);

    /**
     * 卸载插件
     */
    void uninstallPlugin(String pluginId);

    /**
     * 更新插件
     */
    void updatePlugin(String pluginId, MultipartFile newJarFile);


}
