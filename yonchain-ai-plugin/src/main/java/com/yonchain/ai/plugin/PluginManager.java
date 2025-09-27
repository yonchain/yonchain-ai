package com.yonchain.ai.plugin;

import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.manager.PluginEventPublisher;
import com.yonchain.ai.plugin.parser.PluginParser;
import com.yonchain.ai.plugin.exception.PluginParseException;
import com.yonchain.ai.plugin.registry.PluginRegistry;
import com.yonchain.ai.plugin.exception.PluginInstallException;
import com.yonchain.ai.plugin.service.PluginIconService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.yonchain.ai.plugin.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 优化后的插件管理器
 * 支持InputStream安装，简化异常处理
 *
 * @author yonchain
 */
@Service
public class PluginManager {

    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);

    private final PluginRegistry pluginRegistry;
    private final PluginParser pluginParser;
    private final Map<String, PluginAdapter> adapters;
    private final PluginEventPublisher eventPublisher;
    private final PluginIconService pluginIconService;

    public PluginManager(
            PluginRegistry pluginRegistry,
            PluginParser pluginParser,
            List<PluginAdapter> adapters,
            PluginEventPublisher eventPublisher,
            PluginIconService pluginIconService) {

        this.pluginRegistry = pluginRegistry;
        this.pluginParser = pluginParser;
        this.eventPublisher = eventPublisher;
        this.pluginIconService = pluginIconService;

        // 构建适配器映射
        this.adapters = adapters.stream()
                .collect(java.util.stream.Collectors.toMap(
                        adapter -> adapter.getSupportedType().getCode(),
                        adapter -> adapter
                ));

        log.info("Registered plugin adapters: {}", this.adapters.keySet());
    }

    /**
     * 通过输入流安装插件
     *
     * @param inputStream    插件输入流
     * @param pluginFileName 插件文件名（用于日志和临时文件）
     * @throws PluginInstallException 安装异常
     */
    public void installPlugin(InputStream inputStream, String pluginFileName) throws PluginInstallException {
        if (inputStream == null) {
            throw new PluginInstallException("Plugin input stream cannot be null");
        }
        if (pluginFileName == null || pluginFileName.trim().isEmpty()) {
            pluginFileName = "plugin-" + System.currentTimeMillis() + ".jar";
        }

        log.info("Starting plugin installation from input stream: {}", pluginFileName);

        Path tempFilePath = null;
        try {
            // 1. 将输入流保存到临时文件
            String tempDirPath = System.getProperty("java.io.tmpdir") + "yonchain-plugins";
            Path tempDir = Paths.get(tempDirPath);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
                log.debug("Created temp icon directory: {}", tempDir);
            }
            tempFilePath = Paths.get(tempDirPath + "/" + pluginFileName);


            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Plugin saved to temporary file: {}", tempFilePath);

            // 2. 解析插件描述符
            PluginDescriptor descriptor = pluginParser.parsePlugin(tempFilePath);
            String pluginId = descriptor.getId();

            // 3. 检查插件是否已安装
            Optional<PluginInfo> existingPlugin = pluginRegistry.findByPluginId(pluginId);
            if (existingPlugin.isPresent()) {
                throw new PluginInstallException("Plugin already installed: " + pluginId);
            }

            // 4. 验证插件
            ValidationResult validation = pluginParser.validatePlugin(descriptor);
            if (!validation.isValid()) {
                throw new PluginInstallException("Plugin validation failed: " + validation.getErrorMessage());
            }

            // 5. 获取对应的适配器
            String pluginType = descriptor.getType();
            PluginAdapter adapter = getAdapterForType(pluginType);
            if (adapter == null) {
                throw new PluginInstallException("No adapter found for plugin type: " + pluginType);
            }

            // 6. 创建插件信息
            PluginInfo pluginInfo = PluginInfo.fromDescriptor(descriptor);
            pluginInfo.setStatus("installing");

            // 7. 保存插件信息
            pluginRegistry.save(pluginInfo);

            try {
                // 8. 保存插件图标（如果有的话）
                if (descriptor.getIconData() != null && descriptor.getIcon() != null) {
                    String iconPath = pluginIconService.saveIconData(
                            descriptor.getId(),
                            descriptor.getIcon(),
                            descriptor.getIconData()
                    );
                    if (iconPath != null) {
                        pluginInfo.setIconPath(iconPath);
                        log.debug("Icon saved for plugin {}: {}", pluginId, iconPath);
                    }
                }

                // 9. 调用适配器安装逻辑
                adapter.onPluginInstall(descriptor);

                // 10. 更新状态为已安装但禁用
                pluginInfo.setStatus("disabled");
                pluginRegistry.save(pluginInfo);

                // 11. 发布安装事件
                eventPublisher.publishInstalled(pluginId);

                log.info("Plugin installed successfully: {}", pluginId);

            } catch (Exception e) {
                log.error("Plugin installation failed: {}", pluginId, e);

                // 更新状态为安装失败
                pluginInfo.setStatus("install_failed");
                pluginRegistry.save(pluginInfo);

                throw new PluginInstallException("Plugin installation failed: " + e.getMessage(), e);
            }

        } catch (PluginParseException e) {
            log.error("Plugin parsing failed from input stream: {}", pluginFileName, e);
            throw new PluginInstallException("Plugin parsing failed: " + e.getMessage(), e);
        } catch (PluginInstallException e) {
            throw e; // 重新抛出已包装的异常
        } catch (Exception e) {
            log.error("Unexpected error during plugin installation from input stream: {}", pluginFileName, e);
            throw new PluginInstallException("Unexpected error: " + e.getMessage(), e);
        } finally {
            /*// 清理临时文件
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                    log.debug("Temporary file deleted: {}", tempFilePath);
                } catch (IOException e) {
                    log.warn("Failed to delete temporary file: {}", tempFilePath, e);
                }
            }*/
        }
    }

    /**
     * 通过本地路径安装插件（兼容性方法）
     *
     * @param pluginPath 插件路径
     * @throws PluginInstallException 安装异常
     */
    public void installPluginFromPath(String pluginPath) throws PluginInstallException {
        if (pluginPath == null || pluginPath.trim().isEmpty()) {
            throw new PluginInstallException("Plugin path cannot be null or empty");
        }

        try (InputStream inputStream = Files.newInputStream(Paths.get(pluginPath))) {
            String fileName = Paths.get(pluginPath).getFileName().toString();
            installPlugin(inputStream, fileName);
        } catch (IOException e) {
            log.error("Failed to read plugin file: {}", pluginPath, e);
            throw new PluginInstallException("Failed to read plugin file: " + e.getMessage(), e);
        }
    }

    /**
     * 通过URL下载并安装插件
     *
     * @param url 插件下载URL
     * @throws PluginInstallException 安装异常
     */
    public void installPluginFromUrl(String url) throws PluginInstallException {
        log.info("Starting plugin installation from URL: {}", url);

        if (url == null || url.trim().isEmpty()) {
            throw new PluginInstallException("Plugin URL cannot be null or empty");
        }

        try {
            // 1. 从URL获取输入流并安装插件
            String fileName = extractFileNameFromUrl(url);

            try (InputStream inputStream = new URL(url).openStream()) {
                installPlugin(inputStream, fileName);
            }

            log.info("Plugin installed successfully from URL: {}", url);

        } catch (PluginInstallException e) {
            throw e; // 重新抛出插件安装异常
        } catch (IOException e) {
            log.error("Failed to download plugin from URL: {}", url, e);
            throw new PluginInstallException("Failed to download plugin: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during URL plugin installation: {}", url, e);
            throw new PluginInstallException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * 通过插件市场安装插件（预留接口）
     *
     * @param marketplaceId 插件市场ID
     * @throws PluginInstallException 安装异常
     */
    public void installPluginFromMarketplace(String marketplaceId) throws PluginInstallException {
        log.info("Starting plugin installation from marketplace: {}", marketplaceId);

        if (marketplaceId == null || marketplaceId.trim().isEmpty()) {
            throw new PluginInstallException("Marketplace plugin ID cannot be null or empty");
        }

        // TODO: 实现插件市场安装逻辑
        // 1. 查询插件市场API获取插件信息
        // 2. 下载插件文件
        // 3. 安装插件

        throw new UnsupportedOperationException("Plugin marketplace installation not yet implemented");
    }

    /**
     * 启用插件
     *
     * @param pluginId 插件ID
     * @throws PluginInstallException 启用异常
     */
    public void enablePlugin(String pluginId) throws PluginInstallException {
        log.info("Starting plugin enablement: {}", pluginId);

        if (pluginId == null || pluginId.trim().isEmpty()) {
            throw new PluginInstallException("Plugin ID cannot be null or empty");
        }

        try {
            // 1. 查找插件信息
            PluginInfo pluginInfo = pluginRegistry.findByPluginId(pluginId)
                    .orElseThrow(() -> new PluginInstallException("Plugin not found: " + pluginId));

            // 2. 检查插件状态
            if (!"disabled".equals(pluginInfo.getStatus())) {
                throw new PluginInstallException("Plugin is not in disabled state: " + pluginInfo.getStatus());
            }

            // 3. 获取对应的适配器
            String pluginType = pluginInfo.getType();
            PluginAdapter adapter = getAdapterForType(pluginType);
            if (adapter == null) {
                throw new PluginInstallException("No adapter found for plugin type: " + pluginType);
            }

            // 4. 更新状态为启用中
            pluginInfo.setStatus("enabling");
            pluginRegistry.save(pluginInfo);

            try {
                // 5. 调用适配器启用逻辑
                adapter.onPluginEnable(pluginId);

                // 6. 更新状态为已启用
                pluginInfo.setStatus("enabled");
                pluginRegistry.save(pluginInfo);

                // 7. 发布启用事件
                eventPublisher.publishEnabled(pluginId);

                log.info("Plugin enabled successfully: {}", pluginId);

            } catch (Exception e) {
                log.error("Plugin enable failed: {}", pluginId, e);

                // 启用失败，回滚状态
                pluginInfo.setStatus("disabled");
                pluginRegistry.save(pluginInfo);

                throw new PluginInstallException("Plugin enable failed: " + e.getMessage(), e);
            }

        } catch (PluginInstallException e) {
            throw e; // 重新抛出已包装的异常
        } catch (Exception e) {
            log.error("Unexpected error during plugin enablement: {}", pluginId, e);
            throw new PluginInstallException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * 禁用插件
     *
     * @param pluginId 插件ID
     * @throws PluginInstallException 禁用异常
     */
    public void disablePlugin(String pluginId) throws PluginInstallException {
        log.info("Starting plugin disablement: {}", pluginId);

        if (pluginId == null || pluginId.trim().isEmpty()) {
            throw new PluginInstallException("Plugin ID cannot be null or empty");
        }

        try {
            // 1. 查找插件信息
            PluginInfo pluginInfo = pluginRegistry.findByPluginId(pluginId)
                    .orElseThrow(() -> new PluginInstallException("Plugin not found: " + pluginId));

            // 2. 检查插件状态
            if (!"enabled".equals(pluginInfo.getStatus())) {
                throw new PluginInstallException("Plugin is not in enabled state: " + pluginInfo.getStatus());
            }

            // 3. 获取对应的适配器
            String pluginType = pluginInfo.getType();
            PluginAdapter adapter = getAdapterForType(pluginType);
            if (adapter == null) {
                throw new PluginInstallException("No adapter found for plugin type: " + pluginType);
            }

            // 4. 更新状态为禁用中
            pluginInfo.setStatus("disabling");
            pluginRegistry.save(pluginInfo);

            try {
                // 5. 调用适配器禁用逻辑
                adapter.onPluginDisable(pluginId);

                // 6. 更新状态为已禁用
                pluginInfo.setStatus("disabled");
                pluginRegistry.save(pluginInfo);

                // 7. 发布禁用事件
                eventPublisher.publishDisabled(pluginId);

                log.info("Plugin disabled successfully: {}", pluginId);

            } catch (Exception e) {
                log.error("Plugin disable failed: {}", pluginId, e);

                // 禁用失败，回滚状态
                pluginInfo.setStatus("enabled");
                pluginRegistry.save(pluginInfo);

                throw new PluginInstallException("Plugin disable failed: " + e.getMessage(), e);
            }

        } catch (PluginInstallException e) {
            throw e; // 重新抛出已包装的异常
        } catch (Exception e) {
            log.error("Unexpected error during plugin disablement: {}", pluginId, e);
            throw new PluginInstallException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * 卸载插件
     *
     * @param pluginId 插件ID
     * @throws PluginInstallException 卸载异常
     */
    public void uninstallPlugin(String pluginId) throws PluginInstallException {
        log.info("Starting plugin uninstallation: {}", pluginId);

        if (pluginId == null || pluginId.trim().isEmpty()) {
            throw new PluginInstallException("Plugin ID cannot be null or empty");
        }

        try {
            // 1. 查找插件信息
            PluginInfo pluginInfo = pluginRegistry.findByPluginId(pluginId)
                    .orElseThrow(() -> new PluginInstallException("Plugin not found: " + pluginId));

            // 2. 如果插件处于启用状态，先禁用
            if ("enabled".equals(pluginInfo.getStatus())) {
                disablePlugin(pluginId);
            }

            // 3. 获取对应的适配器
            String pluginType = pluginInfo.getType();
            PluginAdapter adapter = getAdapterForType(pluginType);
            if (adapter == null) {
                throw new PluginInstallException("No adapter found for plugin type: " + pluginType);
            }

            // 4. 更新状态为卸载中
            pluginInfo.setStatus("uninstalling");
            pluginRegistry.save(pluginInfo);

            try {
                // 5. 删除插件图标文件
                boolean iconDeleted = pluginIconService.deleteIcon(pluginId, pluginInfo.getIconPath());
                if (iconDeleted) {
                    log.debug("Icon deleted for plugin: {}", pluginId);
                }

                // 6. 调用适配器卸载逻辑
                adapter.onPluginUninstall(pluginId);

                // 7. 删除插件记录
                pluginRegistry.deleteByPluginId(pluginId);

                // 8. 发布卸载事件
                eventPublisher.publishUninstalled(pluginId);

                log.info("Plugin uninstalled successfully: {}", pluginId);

            } catch (Exception e) {
                log.error("Plugin uninstallation failed: {}", pluginId, e);

                // 更新状态为卸载失败
                pluginInfo.setStatus("uninstall_failed");
                pluginRegistry.save(pluginInfo);

                throw new PluginInstallException("Plugin uninstallation failed: " + e.getMessage(), e);
            }

        } catch (PluginInstallException e) {
            throw e; // 重新抛出已包装的异常
        } catch (Exception e) {
            log.error("Unexpected error during plugin uninstallation: {}", pluginId, e);
            throw new PluginInstallException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有插件
     */
    public List<PluginInfo> getPlugins() {
        return pluginRegistry.findAll();
    }

    /**
     * 获取指定插件
     */
    public Optional<PluginInfo> getPlugin(String pluginId) {
        return pluginRegistry.findByPluginId(pluginId);
    }

    /**
     * 获取已启用的插件
     */
    public List<PluginInfo> getEnabledPlugins() {
        return pluginRegistry.findByStatus("enabled");
    }

    /**
     * 自动加载已安装的插件
     */
    public void loadInstalledPlugins() {
        log.info("Loading installed plugins...");

        try {
            List<PluginInfo> installedPlugins = pluginRegistry.findByStatus("enabled");

            for (PluginInfo pluginInfo : installedPlugins) {
                try {
                    // 重新启用已启用的插件
                    String pluginType = pluginInfo.getType();
                    PluginAdapter adapter = getAdapterForType(pluginType);

                    if (adapter != null) {
                        adapter.onPluginEnable(pluginInfo.getPluginId());
                        log.info("Auto-loaded plugin: {}", pluginInfo.getPluginId());
                    } else {
                        log.warn("No adapter found for plugin type: {}", pluginType);
                    }

                } catch (Exception e) {
                    log.error("Failed to auto-load plugin: {}", pluginInfo.getPluginId(), e);
                }
            }

            log.info("Completed loading {} installed plugins", installedPlugins.size());

        } catch (Exception e) {
            log.error("Failed to load installed plugins", e);
        }
    }

    /**
     * 从URL提取文件名
     */
    private String extractFileNameFromUrl(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        if (!fileName.contains(".")) {
            fileName += ".jar"; // 默认添加.jar扩展名
        }
        return fileName;
    }

    /**
     * 获取插件类型对应的适配器
     */
    private PluginAdapter getAdapterForType(String pluginType) {
        return adapters.get(pluginType);
    }
}
