package com.yonchain.ai.plugin.service.impl;

import com.yonchain.ai.api.plugin.PluginService;
import com.yonchain.ai.api.plugin.dto.PluginInfo;
import com.yonchain.ai.plugin.PluginManager;
import com.yonchain.ai.plugin.config.PluginConfig;
import com.yonchain.ai.plugin.exception.PluginInstallException;
import com.yonchain.ai.plugin.exception.PluginParseException;
import com.yonchain.ai.plugin.entity.PluginInstallation;
import com.yonchain.ai.plugin.service.AiModelInstallationService;
import com.yonchain.ai.plugin.service.ToolInstallationService;
import com.yonchain.ai.plugin.service.PluginInstallationService;
import com.yonchain.ai.plugin.parser.PluginParser;
import com.yonchain.ai.plugin.service.PluginIconService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件服务实现类
 * 封装所有插件相关操作，包括管理、安装、卸载、图标处理等
 *
 * @author yonchain
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginServiceImpl implements PluginService {

    private final PluginManager pluginManager;
    private final PluginIconService pluginIconService;
    private final PluginParser pluginParser;
    private final AiModelInstallationService aiModelInstallationService;
    private final ToolInstallationService toolInstallationService;
    private final PluginInstallationService pluginInstallationService;

    // 配置目录
    private final String pluginUploadDir = System.getProperty("java.io.tmpdir") + "/yonchain-plugins";
    private final String tempIconDir = System.getProperty("java.io.tmpdir") + "/yonchain-plugins/temp-icons";

    // ==================== 插件查询接口实现 ====================


    @Override
    public PluginInfo getPlugin(String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            return plugin.map(this::convertToApiPluginInfo).orElse(null);
        } catch (Exception e) {
            log.error("Failed to get plugin: {}", pluginId, e);
            return null;
        }
    }


    @Override
    public com.yonchain.ai.api.common.Page<PluginInfo> getPlugins(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        if (tenantId == null) {
            return new com.yonchain.ai.api.common.Page<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }

        try {
            // 从查询参数中提取条件
            String status = (String) queryParam.get("status");
            String type = (String) queryParam.get("type");
            String name = (String) queryParam.get("name");

            log.debug("Querying plugins for tenant: {}, status: {}, type: {}, name: {}, pageNum: {}, pageSize: {}", 
                     tenantId, status, type, name, pageNum, pageSize);

            // 查询插件列表
            List<com.yonchain.ai.plugin.entity.PluginEntity> pluginEntities = 
                pluginInstallationService.getTenantPluginsWithConditions(tenantId, status, type, name, pageNum, pageSize);

            // 查询总数
            long total = pluginInstallationService.countTenantPluginsWithConditions(tenantId, status, type, name);

            // 转换为API PluginInfo格式
            List<PluginInfo> plugins = pluginEntities.stream()
                    .map(this::convertEntityToApiPluginInfo)
                    .collect(Collectors.toList());

            log.debug("Found {} plugins for tenant: {}, total: {}", plugins.size(), tenantId, total);
            
            return new com.yonchain.ai.api.common.Page<>(plugins, total, pageNum, pageSize);

        } catch (Exception e) {
            log.error("Failed to get plugins with pagination for tenant: {}", tenantId, e);
            return new com.yonchain.ai.api.common.Page<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
    }

    @Override
    public Map<String, Object> getTenantPluginInstallation(String tenantId, String pluginId) {
        if (tenantId == null || pluginId == null) {
            return Collections.emptyMap();
        }

        try {
            // 使用统一的插件安装服务
           PluginInstallation installation =
                pluginInstallationService.getPluginInstallation(tenantId, pluginId);
            
            if (installation != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("installationType", installation.getRuntimeType());
                result.put("installedAt", installation.getCreatedAt());
                result.put("pluginUniqueIdentifier", installation.getPluginUniqueIdentifier());
                result.put("endpointsSetups", installation.getEndpointsSetups());
                result.put("endpointsActive", installation.getEndpointsActive());
                result.put("source", installation.getSource());
                result.put("meta", installation.getMeta());
                return result;
            }

            // 未安装
            return Collections.emptyMap();

        } catch (Exception e) {
            log.error("Failed to get tenant plugin installation for plugin {} and tenant {}", pluginId, tenantId, e);
            return Collections.emptyMap();
        }
    }

    // ==================== 插件管理接口实现 ====================

    @Override
    public Map<String, Object> previewPlugin(InputStream inputStream, String fileName) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Plugin input stream cannot be null");
        }

        // 验证文件格式
        if (fileName == null || !fileName.endsWith(".jar")) {
            throw new IllegalArgumentException("Invalid plugin file format. Only .jar files are supported.");
        }

        try {
            // 保存临时文件
            Path tempFile = Files.createTempFile("plugin-preview-", ".jar");
            try {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

                // 解析插件信息
                PluginConfig pluginConfig = pluginParser.parsePlugin(tempFile);

                // 处理临时图标
                String tempIconUrl = null;
                if (pluginConfig.getIconData() != null && pluginConfig.getIcon() != null) {
                    tempIconUrl = saveTempIcon(pluginConfig.getId(), pluginConfig.getIcon(), pluginConfig.getIconData());
                }

                // 转换为Map返回
                return convertPluginConfigToMap(pluginConfig, tempIconUrl);

            } finally {
                // 清理临时文件
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp file: {}", tempFile, e);
                }
            }

        } catch (PluginParseException e) {
            log.error("Failed to parse plugin: {}", fileName, e);
            throw new IllegalArgumentException("Plugin format error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to preview plugin: {}", fileName, e);
            throw new RuntimeException("Failed to preview plugin: " + e.getMessage(), e);
        }
    }

    @Override
    public String installPlugin(InputStream inputStream, String fileName) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Plugin input stream cannot be null");
        }

        try {
            // 验证文件格式
            if (fileName == null || !fileName.endsWith(".jar")) {
                throw new IllegalArgumentException("Invalid plugin file format. Only .jar files are supported.");
            }

            // 确保上传目录存在
            Path uploadDir = Paths.get(pluginUploadDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Created plugin upload directory: {}", uploadDir);
            }

            // 生成临时文件路径
            String tempFileName = "upload_" + System.currentTimeMillis() + "_" + fileName;
            Path tempFilePath = uploadDir.resolve(tempFileName);

            // 保存上传的文件到本地临时文件
            long fileSize = Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Plugin file saved to: {}, size: {} bytes", tempFilePath, fileSize);

            // 验证文件是否成功保存
            if (!Files.exists(tempFilePath)) {
                throw new IOException("Failed to save plugin file to: " + tempFilePath);
            }

            long savedFileSize = Files.size(tempFilePath);
            if (savedFileSize == 0) {
                throw new IOException("Saved plugin file is empty: " + tempFilePath);
            }

            log.debug("File verification passed: {} bytes", savedFileSize);

            try {
                // 使用文件路径安装插件
                pluginManager.installPluginFromPath(tempFilePath.toString());
                return "Plugin installed successfully";

            } finally {
                // 清理临时文件（可选，保留用于调试）
                // Files.deleteIfExists(tempFilePath);
            }

        } catch (PluginInstallException e) {
            log.error("Failed to install plugin", e);
            throw new RuntimeException("Plugin installation failed: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Failed to upload plugin file", e);
            throw new RuntimeException("Failed to upload plugin file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to install plugin", e);
            throw new RuntimeException("Failed to install plugin: " + e.getMessage(), e);
        }
    }

    @Override
    public String installPluginByPath(String pluginPath) {
        if (pluginPath == null || pluginPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin path cannot be empty");
        }

        try {
            pluginManager.installPluginFromPath(pluginPath);
            return "Plugin installed successfully";

        } catch (PluginInstallException e) {
            log.error("Failed to install plugin from path: {}", pluginPath, e);
            throw new RuntimeException("Plugin installation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to install plugin from path: {}", pluginPath, e);
            throw new RuntimeException("Failed to install plugin: " + e.getMessage(), e);
        }
    }

    @Override
    public String installPluginByUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin URL cannot be empty");
        }

        try {
            pluginManager.installPluginFromUrl(url);
            return "Plugin installed successfully from URL";

        } catch (PluginInstallException e) {
            log.error("Failed to install plugin from URL: {}", url, e);
            throw new RuntimeException("Plugin installation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to install plugin from URL: {}", url, e);
            throw new RuntimeException("Failed to install plugin: " + e.getMessage(), e);
        }
    }

    @Override
    public String installPluginFromMarketplace(String marketplaceId) {
        if (marketplaceId == null || marketplaceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Marketplace plugin ID cannot be empty");
        }

        try {
            pluginManager.installPluginFromMarketplace(marketplaceId);
            return "Plugin installed successfully from marketplace";

        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Plugin marketplace installation not yet implemented");
        } catch (PluginInstallException e) {
            log.error("Failed to install plugin from marketplace: {}", marketplaceId, e);
            throw new RuntimeException("Plugin installation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to install plugin from marketplace: {}", marketplaceId, e);
            throw new RuntimeException("Failed to install plugin: " + e.getMessage(), e);
        }
    }

    // ==================== 租户感知的安装方法 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String installPluginForTenant(String tenantId, InputStream inputStream, String fileName) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("Plugin input stream cannot be null");
        }

        log.info("Installing plugin for tenant: {}, fileName: {}", tenantId, fileName);

        Path tempFilePath = null;
        try {
            // 1. 保存输入流到临时文件
            tempFilePath = saveInputStreamToTempFile(inputStream, fileName);
            
            // 2. 解析插件获取插件信息（在安装前）
            PluginConfig pluginConfig = pluginParser.parsePlugin(tempFilePath);
            String pluginId = pluginConfig.getId();
            String pluginType = pluginConfig.getType();
            
            // 3. 检查插件是否已经安装
            Optional<com.yonchain.ai.api.plugin.dto.PluginInfo> existingPlugin = pluginManager.getPlugin(pluginId);
            if (!existingPlugin.isPresent()) {
                // 4. 如果插件未安装，先进行全局插件安装
                pluginManager.installPluginFromPath(tempFilePath.toString());
                log.info("Global plugin installation completed for: {}", pluginId);
            } else {
                log.info("Plugin {} already installed globally, skipping global installation", pluginId);
            }
            
            // 5. 根据插件类型创建租户安装记录
            createTenantInstallationRecord(tenantId, pluginId, pluginType, pluginConfig);
            
            log.info("Successfully installed plugin {} for tenant: {}", pluginId, tenantId);
            return "Plugin installed successfully for tenant: " + tenantId;

        } catch (Exception e) {
            log.error("Failed to install plugin for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to install plugin for tenant: " + e.getMessage(), e);
        } finally {
            // 清理临时文件
            if (tempFilePath != null && Files.exists(tempFilePath)) {
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    log.warn("Failed to delete temp file: {}", tempFilePath, e);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String installPluginByPathForTenant(String tenantId, String pluginPath) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
        if (pluginPath == null || pluginPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin path cannot be empty");
        }

        log.info("Installing plugin from path for tenant: {}, path: {}", tenantId, pluginPath);

        try {
            // 1. 先进行全局插件安装
            installPluginByPath(pluginPath);
            
            // 2. 解析插件获取插件信息
            PluginConfig pluginConfig = pluginParser.parsePlugin(Paths.get(pluginPath));
            String pluginId = pluginConfig.getId();
            String pluginType = pluginConfig.getType();
            
            // 3. 根据插件类型创建租户安装记录
            createTenantInstallationRecord(tenantId, pluginId, pluginType, pluginConfig);
            
            log.info("Successfully installed plugin {} from path for tenant: {}", pluginId, tenantId);
            return "Plugin installed successfully from path for tenant: " + tenantId;

        } catch (Exception e) {
            log.error("Failed to install plugin from path for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to install plugin from path for tenant: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String installPluginByUrlForTenant(String tenantId, String url) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin URL cannot be empty");
        }

        log.info("Installing plugin from URL for tenant: {}, url: {}", tenantId, url);

        try {
            // 1. 先进行全局插件安装
            installPluginByUrl(url);
            
            // 2. 从URL下载并解析插件（这里需要重新下载解析，因为原方法没有返回插件信息）
            // TODO: 优化这里的实现，避免重复下载
            String fileName = extractFileNameFromUrl(url);
            PluginConfig pluginConfig = downloadAndParsePlugin(url, fileName);
            String pluginId = pluginConfig.getId();
            String pluginType = pluginConfig.getType();
            
            // 3. 根据插件类型创建租户安装记录
            createTenantInstallationRecord(tenantId, pluginId, pluginType, pluginConfig);
            
            log.info("Successfully installed plugin {} from URL for tenant: {}", pluginId, tenantId);
            return "Plugin installed successfully from URL for tenant: " + tenantId;

        } catch (Exception e) {
            log.error("Failed to install plugin from URL for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to install plugin from URL for tenant: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String installPluginFromMarketplaceForTenant(String tenantId, String marketplaceId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
        if (marketplaceId == null || marketplaceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Marketplace plugin ID cannot be empty");
        }

        log.info("Installing plugin from marketplace for tenant: {}, marketplaceId: {}", tenantId, marketplaceId);

        try {
            // 1. 先进行全局插件安装
            installPluginFromMarketplace(marketplaceId);
            
            // 2. 从市场获取插件信息（这里需要实现市场插件信息获取逻辑）
            // TODO: 实现市场插件信息获取
            throw new UnsupportedOperationException("Marketplace plugin installation for tenant not yet implemented");

        } catch (Exception e) {
            log.error("Failed to install plugin from marketplace for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to install plugin from marketplace for tenant: " + e.getMessage(), e);
        }
    }

    @Override
    public String uninstallPlugin(String pluginId) {
        try {
            pluginManager.uninstallPlugin(pluginId);
            return "Plugin uninstalled successfully";

        } catch (Exception e) {
            log.error("Failed to uninstall plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to uninstall plugin: " + e.getMessage(), e);
        }
    }



    @Override
    public String enablePlugin(String pluginId) {
        try {
            pluginManager.enablePlugin(pluginId);
            return "Plugin enabled successfully";

        } catch (Exception e) {
            log.error("Failed to enable plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to enable plugin: " + e.getMessage(), e);
        }
    }

    @Override
    public String disablePlugin(String pluginId) {
        try {
            pluginManager.disablePlugin(pluginId);
            return "Plugin disabled successfully";

        } catch (Exception e) {
            log.error("Failed to disable plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to disable plugin: " + e.getMessage(), e);
        }
    }


    // ==================== 插件图标接口实现 ====================

    @Override
    public Map<String, Object> getPluginIcon(String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            if (!plugin.isPresent()) {
                return null;
            }

            PluginInfo pluginInfo = plugin.get();

            // 获取图标文件名
            String iconFileName = pluginInfo.getIconPath();

            if (iconFileName == null || iconFileName.trim().isEmpty()) {
                return null;
            }

            // 检查图标文件是否存在
            if (!pluginIconService.iconExists(pluginId, iconFileName)) {
                return null;
            }

            // 获取图标文件路径
            Path iconPath = pluginIconService.getIconPhysicalPath(pluginId, iconFileName);
            if (iconPath == null || !Files.exists(iconPath)) {
                return null;
            }

            // 读取图标数据
            byte[] iconData = Files.readAllBytes(iconPath);
            String contentType = determineContentType(iconPath);

            Map<String, Object> result = new HashMap<>();
            result.put("data", iconData);
            result.put("contentType", contentType);
            result.put("fileName", iconPath.getFileName().toString());
            return result;

        } catch (Exception e) {
            log.error("Failed to get plugin icon: {}", pluginId, e);
            return null;
        }
    }

    @Override
    public String getPluginIconUrl(String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            if (!plugin.isPresent()) {
                return null;
            }

            PluginInfo pluginInfo = plugin.get();

            // 获取图标文件名
            String iconFileName = pluginInfo.getIconPath();

            if (iconFileName == null || iconFileName.trim().isEmpty()) {
                return null;
            }

            // 检查图标文件是否存在
            if (!pluginIconService.iconExists(pluginId, iconFileName)) {
                return null;
            }

            // 生成访问URL
            return "/plugins/" + pluginId + "/icon";

        } catch (Exception e) {
            log.error("Failed to get plugin icon URL: {}", pluginId, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getTempIcon(String iconId) {
        try {
            Path tempIconPath = Paths.get(tempIconDir, iconId);
            if (!Files.exists(tempIconPath)) {
                return null;
            }

            // 读取图标数据
            byte[] iconData = Files.readAllBytes(tempIconPath);
            String contentType = determineContentType(tempIconPath);

            Map<String, Object> result = new HashMap<>();
            result.put("data", iconData);
            result.put("contentType", contentType);
            result.put("fileName", tempIconPath.getFileName().toString());
            return result;

        } catch (Exception e) {
            log.error("Failed to get temp icon: {}", iconId, e);
            return null;
        }
    }

    @Override
    public String saveTempIcon(String pluginId, String iconFileName, byte[] iconData) {
        if (iconData == null || iconData.length == 0 || iconFileName == null) {
            return null;
        }

        try {
            // 确保临时图标目录存在
            Path tempIconDirectory = Paths.get(tempIconDir);
            if (!Files.exists(tempIconDirectory)) {
                Files.createDirectories(tempIconDirectory);
                log.debug("Created temp icon directory: {}", tempIconDirectory);
            }

            // 生成唯一的临时文件名
            String fileExtension = getFileExtension(iconFileName);
            String tempIconId = "temp_" + pluginId + "_" + System.currentTimeMillis() + fileExtension;
            Path tempIconPath = tempIconDirectory.resolve(tempIconId);

            // 保存图标数据
            Files.write(tempIconPath, iconData);
            log.debug("Saved temp icon: {}", tempIconPath);

            // 返回临时访问URL
            return "/plugins/temp-icons/" + tempIconId;

        } catch (Exception e) {
            log.error("Failed to save temp icon for plugin {}: {}", pluginId, iconFileName, e);
            return null;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将内部PluginInfo转换为API PluginInfo（现在已经是同一个对象）
     */
    private com.yonchain.ai.api.plugin.dto.PluginInfo convertToApiPluginInfo(com.yonchain.ai.api.plugin.dto.PluginInfo pluginInfo) {
        // 设置图标URL
        pluginInfo.setIconUrl(getPluginIconUrl(pluginInfo.getPluginId()));
        return pluginInfo;
    }

    /**
     * 将PluginEntity转换为API PluginInfo
     */
    private com.yonchain.ai.api.plugin.dto.PluginInfo convertEntityToApiPluginInfo(com.yonchain.ai.plugin.entity.PluginEntity entity) {
        com.yonchain.ai.api.plugin.dto.PluginInfo pluginInfo = new com.yonchain.ai.api.plugin.dto.PluginInfo();
        
        pluginInfo.setPluginId(entity.getPluginId());
        pluginInfo.setName(entity.getName());
        pluginInfo.setVersion(entity.getVersion());
        pluginInfo.setDescription(entity.getDescription());
        pluginInfo.setAuthor(entity.getAuthor());
        pluginInfo.setType(entity.getType());
        pluginInfo.setStatus(entity.getStatus());
        pluginInfo.setPluginPath(entity.getPluginPath());
        pluginInfo.setMainClass(entity.getMainClass());
        pluginInfo.setCreatedAt(entity.getCreatedAt());
        pluginInfo.setUpdatedAt(entity.getUpdatedAt());
        pluginInfo.setIconPath(entity.getIconPath());
        
        // 设置安装时间
        if (entity.getInstalledAt() != null) {
            pluginInfo.setInstallTime(entity.getInstalledAt());
        }
        
        // 根据状态设置enabled和available
        boolean isEnabled = "enabled".equals(entity.getStatus());
        pluginInfo.setEnabled(isEnabled);
        pluginInfo.setAvailable(isEnabled);
        
        // 设置图标URL
        pluginInfo.setIconUrl(getPluginIconUrl(entity.getPluginId()));
        
        return pluginInfo;
    }

    /**
     * 将PluginConfig转换为Map
     */
    private Map<String, Object> convertPluginConfigToMap(PluginConfig pluginConfig, String tempIconUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("pluginId", pluginConfig.getId());
        map.put("name", pluginConfig.getName());
        map.put("version", pluginConfig.getVersion());
        map.put("description", pluginConfig.getLocalizedDescription("zh_Hans"));
        map.put("author", pluginConfig.getAuthor());
        map.put("type", pluginConfig.getType());
        map.put("mainClass", null); // PluginConfig 不再有 pluginClass 字段
        map.put("iconUrl", tempIconUrl);
        map.put("dependencies", pluginConfig.getPlugins()); // 使用 plugins 字段作为 dependencies
        map.put("extensions", null); // extensions 字段暂时为空
        map.put("services", null); // services 字段暂时为空
        map.put("createdAt", LocalDateTime.now());
        return map;
    }

    /**
     * 确定文件的MIME类型
     */
    private String determineContentType(Path filePath) {
        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType != null) {
                return contentType;
            }
        } catch (Exception e) {
            log.debug("Failed to probe content type for file: {}", filePath, e);
        }
        
        // 根据文件扩展名确定类型
        String fileName = filePath.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        
        return "application/octet-stream";
    }

    /**
     * 获取文件扩展名（包含点号）
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }

        return "";
    }

    // ==================== 租户安装辅助方法 ====================

    /**
     * 保存输入流到临时文件
     */
    private Path saveInputStreamToTempFile(InputStream inputStream, String fileName) throws IOException {
        // 确保上传目录存在
        Path uploadDir = Paths.get(pluginUploadDir);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("Created plugin upload directory: {}", uploadDir);
        }

        // 生成临时文件路径
        String tempFileName = "tenant_install_" + System.currentTimeMillis() + "_" + fileName;
        Path tempFilePath = uploadDir.resolve(tempFileName);

        // 保存输入流到临时文件
        long fileSize = Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        log.debug("Plugin file saved to temp path: {}, size: {} bytes", tempFilePath, fileSize);

        // 验证文件是否成功保存
        if (!Files.exists(tempFilePath)) {
            throw new IOException("Failed to save plugin file to: " + tempFilePath);
        }

        long savedFileSize = Files.size(tempFilePath);
        if (savedFileSize == 0) {
            throw new IOException("Saved plugin file is empty: " + tempFilePath);
        }

        return tempFilePath;
    }

    /**
     * 根据插件类型创建租户安装记录
     */
    private void createTenantInstallationRecord(String tenantId, String pluginId, String pluginType, PluginConfig pluginConfig) {
        try {
            log.info("Creating tenant installation record for tenant: {}, plugin: {}, type: {}", tenantId, pluginId, pluginType);
            
            // 1. 首先插入到统一的plugin_installation表
            String runtimeType = determineRuntimeType(pluginType);
            String pluginUniqueIdentifier = generatePluginUniqueIdentifier(pluginConfig);
            String meta = generatePluginMeta(pluginConfig, pluginType);
            
            pluginInstallationService.installPlugin(tenantId, pluginId, runtimeType, pluginUniqueIdentifier, meta);
            log.info("Created unified plugin installation record for tenant: {}, plugin: {}", tenantId, pluginId);
            
            // 2. 然后根据插件类型插入到专用表（保持兼容性）
            if ("ai_model".equalsIgnoreCase(pluginType) || "model".equalsIgnoreCase(pluginType)) {
                // AI模型插件安装
                String provider = extractProviderFromPluginConfig(pluginConfig);
                if (provider != null) {
                    aiModelInstallationService.installAiModelPlugin(tenantId, provider, pluginId);
                    log.info("Created AI model installation record for tenant: {}, provider: {}", tenantId, provider);
                }
            } else if ("tool".equalsIgnoreCase(pluginType)) {
                // 工具插件安装
                String toolName = extractToolNameFromPluginConfig(pluginConfig);
                if (toolName != null) {
                    toolInstallationService.installToolPlugin(tenantId, toolName, pluginId);
                    log.info("Created tool installation record for tenant: {}, tool: {}", tenantId, toolName);
                }
            } else {
                log.warn("Unknown plugin type: {}, only created unified installation record", pluginType);
            }
            
        } catch (Exception e) {
            log.error("Failed to create tenant installation record for tenant: {}, plugin: {}", tenantId, pluginId, e);
            throw new RuntimeException("Failed to create tenant installation record: " + e.getMessage(), e);
        }
    }

    /**
     * 从插件配置中提取提供商名称
     */
    private String extractProviderFromPluginConfig(PluginConfig pluginConfig) {
        // 优先从providerConfig中获取provider
        String provider = pluginConfig.getProvider();
        if (provider != null && !provider.trim().isEmpty()) {
            return provider;
        }
        
        // 从resource中获取provider信息
        if (pluginConfig.getResource() != null) {
            Object providerObj = pluginConfig.getResource().get("provider");
            if (providerObj != null) {
                return providerObj.toString();
            }
        }
        
        // 如果没有明确的provider，使用插件ID作为provider
        return pluginConfig.getId();
    }

    /**
     * 从插件配置中提取工具名称
     */
    private String extractToolNameFromPluginConfig(PluginConfig pluginConfig) {
        // 从resource中获取toolName信息
        if (pluginConfig.getResource() != null) {
            Object toolNameObj = pluginConfig.getResource().get("toolName");
            if (toolNameObj != null) {
                return toolNameObj.toString();
            }
        }
        
        // 如果没有明确的toolName，使用插件名称
        return pluginConfig.getName();
    }

    /**
     * 从URL提取文件名
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "plugin.jar";
        }
        
        try {
            String path = url.substring(url.lastIndexOf('/') + 1);
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf('?'));
            }
            return path.isEmpty() ? "plugin.jar" : path;
        } catch (Exception e) {
            return "plugin.jar";
        }
    }

    /**
     * 下载并解析插件
     */
    private PluginConfig downloadAndParsePlugin(String url, String fileName) throws Exception {
        // TODO: 实现从URL下载并解析插件的逻辑
        // 这里需要重新下载插件文件并解析，或者优化现有的安装流程
        throw new UnsupportedOperationException("Download and parse plugin from URL not yet implemented");
    }

    /**
     * 确定运行时类型
     */
    private String determineRuntimeType(String pluginType) {
        if ("ai_model".equalsIgnoreCase(pluginType) || "model".equalsIgnoreCase(pluginType)) {
            return "ai_model";
        } else if ("tool".equalsIgnoreCase(pluginType)) {
            return "tool";
        } else {
            return pluginType != null ? pluginType.toLowerCase() : "unknown";
        }
    }

    /**
     * 生成插件唯一标识符
     */
    private String generatePluginUniqueIdentifier(PluginConfig pluginConfig) {
        // 使用插件ID + 版本作为唯一标识符
        return pluginConfig.getId() + ":" + pluginConfig.getVersion();
    }

    /**
     * 生成插件元数据
     */
    private String generatePluginMeta(PluginConfig pluginConfig, String pluginType) {
        try {
            Map<String, Object> meta = new HashMap<>();
            meta.put("pluginType", pluginType);
            meta.put("pluginName", pluginConfig.getName());
            meta.put("pluginVersion", pluginConfig.getVersion());
            meta.put("pluginAuthor", pluginConfig.getAuthor());
            
            if ("ai_model".equalsIgnoreCase(pluginType) || "model".equalsIgnoreCase(pluginType)) {
                meta.put("provider", extractProviderFromPluginConfig(pluginConfig));
            } else if ("tool".equalsIgnoreCase(pluginType)) {
                meta.put("toolName", extractToolNameFromPluginConfig(pluginConfig));
            }
            
            // 转换为JSON字符串
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(meta);
        } catch (Exception e) {
            log.warn("Failed to generate plugin meta for plugin: {}", pluginConfig.getId(), e);
            return null;
        }
    }
}