package com.yonchain.ai.plugin.controller;

import com.yonchain.ai.plugin.dto.PluginResponse;
import com.yonchain.ai.plugin.dto.PluginPreviewResponse;
import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.enums.PluginStatus;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.manager.PluginManager;
import com.yonchain.ai.plugin.exception.PluginInstallException;
import com.yonchain.ai.plugin.service.PluginIconService;
import com.yonchain.ai.plugin.parser.PluginParser;
import com.yonchain.ai.plugin.exception.PluginParseException;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 插件管理REST控制器
 * 
 * @author yonchain
 */
@RestController
@RequestMapping("/plugins")
public class PluginController {
    
    private static final Logger log = LoggerFactory.getLogger(PluginController.class);
    
    private final PluginManager pluginManager;
    private final PluginIconService pluginIconService;
    private final PluginParser pluginParser;
    private final String pluginUploadDir;
    private final String tempIconDir;
    
    public PluginController(PluginManager pluginManager, PluginIconService pluginIconService, PluginParser pluginParser) {
        this.pluginManager = pluginManager;
        this.pluginIconService = pluginIconService;
        this.pluginParser = pluginParser;
        this.pluginUploadDir = System.getProperty("java.io.tmpdir") + "/yonchain-plugins";
        this.tempIconDir = System.getProperty("java.io.tmpdir") + "/yonchain-plugins/temp-icons";
        
        // 确保上传目录和临时图标目录存在
        try {
            Files.createDirectories(Paths.get(pluginUploadDir));
            Files.createDirectories(Paths.get(tempIconDir));
        } catch (IOException e) {
            log.error("Failed to create plugin directories: {}, {}", pluginUploadDir, tempIconDir, e);
        }
    }
    
    /**
     * 获取所有插件信息
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PluginResponse>>> getPlugins() {
        try {
            List<PluginInfo> plugins = pluginManager.getPlugins();
            List<PluginResponse> pluginResponses = plugins.stream()
                    .map(plugin -> PluginResponse.fromPluginInfo(plugin, pluginIconService))
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(pluginResponses));
        } catch (Exception e) {
            log.error("Failed to get all plugins", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get plugins: " + e.getMessage()));
        }
    }
    
    /**
     * 根据插件ID获取插件信息
     */
    @GetMapping("/{pluginId}")
    public ResponseEntity<ApiResponse<PluginResponse>> getPlugin(@PathVariable String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            if (plugin.isPresent()) {
                PluginResponse pluginResponse = PluginResponse.fromPluginInfo(plugin.get(), pluginIconService);
                return ResponseEntity.ok(ApiResponse.success(pluginResponse));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Plugin not found: " + pluginId));
            }
        } catch (Exception e) {
            log.error("Failed to get plugin: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get plugin: " + e.getMessage()));
        }
    }

    /**
     * 获取已启用的插件列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<ApiResponse<List<PluginResponse>>> getEnabledPlugins() {
        try {
            List<PluginInfo> plugins = pluginManager.getEnabledPlugins();
            List<PluginResponse> pluginResponses = plugins.stream()
                    .map(plugin -> PluginResponse.fromPluginInfo(plugin, pluginIconService))
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(pluginResponses));
        } catch (Exception e) {
            log.error("Failed to get enabled plugins", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get enabled plugins: " + e.getMessage()));
        }
    }
    
    /**
     * 预览插件信息（上传插件文件但不安装，返回插件基本信息）
     */
    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<PluginPreviewResponse>> previewPlugin(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin file cannot be empty"));
        }
        
        // 验证文件格式
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".jar")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid plugin file format. Only .jar files are supported."));
        }
        
        try {
            // 保存临时文件
            Path tempFile = Files.createTempFile("plugin-preview-", ".jar");
            try {
                Files.copy(file.getInputStream(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // 解析插件信息
                PluginDescriptor descriptor = pluginParser.parsePlugin(tempFile);
                
                // 处理临时图标
                String tempIconUrl = null;
                if (descriptor.getIconData() != null && descriptor.getIcon() != null) {
                    tempIconUrl = saveTempIcon(descriptor.getId(), descriptor.getIcon(), descriptor.getIconData());
                }
                
                // 创建预览响应
                PluginPreviewResponse previewResponse = PluginPreviewResponse.fromPluginDescriptor(descriptor, tempIconUrl);
                
                return ResponseEntity.ok(ApiResponse.success(previewResponse));
                
            } finally {
                // 清理临时文件
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp file: {}", tempFile, e);
                }
            }
            
        } catch (PluginParseException e) {
            log.error("Failed to parse plugin: {}", filename, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin format error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to preview plugin: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to preview plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 安装插件（通过文件上传）
     */
    @PostMapping("/install")
    public ResponseEntity<ApiResponse<String>> installPlugin(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin file cannot be empty"));
        }
        
        try {
            // 保存上传的文件到临时目录
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.endsWith(".jar")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid plugin file format. Only .jar files are supported."));
            }
            
            // 确保上传目录存在
            Path uploadDir = Paths.get(pluginUploadDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Created plugin upload directory: {}", uploadDir);
            }
            
            // 生成临时文件路径
            String tempFileName = "upload_" + System.currentTimeMillis() + "_" + filename;
            Path tempFilePath = uploadDir.resolve(tempFileName);
            
            // 保存上传的文件到本地临时文件
            try (InputStream inputStream = file.getInputStream()) {
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
            }
            
            try {
                // 使用文件路径安装插件
                pluginManager.installPluginFromPath(tempFilePath.toString());
                
                return ResponseEntity.ok(ApiResponse.success("success", "Plugin installed successfully"));
                
            } finally {
              /*  // 清理临时文件
                try {
                    Files.deleteIfExists(tempFilePath);
                    log.debug("Cleaned up temp file: {}", tempFilePath);
                } catch (IOException e) {
                    log.warn("Failed to delete temp file: {}", tempFilePath, e);
                }*/
            }
            
        } catch (PluginInstallException e) {
            log.error("Failed to install plugin", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin installation failed: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Failed to upload plugin file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload plugin file: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to install plugin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to install plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 安装插件（通过路径）
     */
    @PostMapping("/install-path")
    public ResponseEntity<ApiResponse<String>> installPluginByPath(@RequestBody Map<String, String> request) {
        String pluginPath = request.get("path");
        if (pluginPath == null || pluginPath.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin path cannot be empty"));
        }
        
        try {
            pluginManager.installPluginFromPath(pluginPath);
            return ResponseEntity.ok(ApiResponse.success("success", "Plugin installed successfully"));
            
        } catch (PluginInstallException e) {
            log.error("Failed to install plugin from path: {}", pluginPath, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin installation failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to install plugin from path: {}", pluginPath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to install plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 安装插件（通过URL下载）
     */
    @PostMapping("/install-url")
    public ResponseEntity<ApiResponse<String>> installPluginByUrl(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin URL cannot be empty"));
        }
        
        try {
            pluginManager.installPluginFromUrl(url);
            return ResponseEntity.ok(ApiResponse.success("success", "Plugin installed successfully from URL"));
            
        } catch (PluginInstallException e) {
            log.error("Failed to install plugin from URL: {}", url, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin installation failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to install plugin from URL: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to install plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 安装插件（通过插件市场，预留接口）
     */
    @PostMapping("/install-marketplace")
    public ResponseEntity<ApiResponse<String>> installPluginFromMarketplace(@RequestBody Map<String, String> request) {
        String marketplaceId = request.get("marketplace_id");
        if (marketplaceId == null || marketplaceId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Marketplace plugin ID cannot be empty"));
        }
        
        try {
            pluginManager.installPluginFromMarketplace(marketplaceId);
            return ResponseEntity.ok(ApiResponse.success("success", "Plugin installed successfully from marketplace"));
            
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error("Plugin marketplace installation not yet implemented"));
        } catch (PluginInstallException e) {
            log.error("Failed to install plugin from marketplace: {}", marketplaceId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Plugin installation failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to install plugin from marketplace: {}", marketplaceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to install plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 卸载插件
     */
    @DeleteMapping("/{pluginId}/uninstall")
    public ResponseEntity<ApiResponse<String>> uninstallPlugin(@PathVariable String pluginId) {
        try {
             pluginManager.uninstallPlugin(pluginId);

            return ResponseEntity.ok(ApiResponse.success("success", "成功"));
            
        } catch (Exception e) {
            log.error("Failed to uninstall plugin: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to uninstall plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 启用插件
     */
    @PostMapping("/{pluginId}/enable")
    public ResponseEntity<ApiResponse<String>> enablePlugin(@PathVariable String pluginId) {
        try {
            pluginManager.enablePlugin(pluginId);

            return ResponseEntity.ok(ApiResponse.success("success", "成功"));
            
        } catch (Exception e) {
            log.error("Failed to enable plugin: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to enable plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 禁用插件
     */
    @PostMapping("/{pluginId}/disable")
    public ResponseEntity<ApiResponse<String>> disablePlugin(@PathVariable String pluginId) {
        try {
             pluginManager.disablePlugin(pluginId);
            return ResponseEntity.ok(ApiResponse.success("success", "成功"));
            
        } catch (Exception e) {
            log.error("Failed to disable plugin: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to disable plugin: " + e.getMessage()));
        }
    }
    
    /**
     * 检查插件状态
     */
    @GetMapping("/{pluginId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPluginStatus(@PathVariable String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            if (plugin.isPresent()) {
                PluginInfo pluginInfo = plugin.get();
                Map<String, Object> status = new HashMap<>();
                status.put("pluginId", pluginId);
                status.put("status", pluginInfo.getStatus());
                status.put("enabled", pluginInfo.getEnabled());
                status.put("available", pluginInfo.isAvailable());
                status.put("type", pluginInfo.getType());
                status.put("version", pluginInfo.getVersion());
                
                return ResponseEntity.ok(ApiResponse.success(status));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Plugin not found: " + pluginId));
            }
        } catch (Exception e) {
            log.error("Failed to get plugin status: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get plugin status: " + e.getMessage()));
        }
    }
    
    /**
     * 获取插件统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPluginStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            List<PluginInfo> allPlugins = pluginManager.getPlugins();
            
            stats.put("total", allPlugins.size());
            stats.put("enabled", allPlugins.stream().mapToLong(p -> p.isAvailable() ? 1 : 0).sum());
            stats.put("disabled", allPlugins.stream().mapToLong(p -> !p.isAvailable() && PluginStatus.fromCode(p.getStatus()) == PluginStatus.DISABLED ? 1 : 0).sum());
            stats.put("failed", allPlugins.stream().mapToLong(p -> p.isFailedState() ? 1 : 0).sum());
            
            // 按类型统计
            Map<String, Long> byType = new HashMap<>();
            for (PluginType type : PluginType.values()) {
                long count = allPlugins.stream().mapToLong(p -> StringUtils.isNotBlank(p.getType()) && p.getType().equals(type) ? 1 : 0).sum();
                byType.put(type.getCode(), count);
            }
            stats.put("byType", byType);
            
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Failed to get plugin stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get plugin stats: " + e.getMessage()));
        }
    }
    
    /**
     * API响应包装类
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private long timestamp;
        
        private ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(true, "Success", data);
        }
        
        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(true, message, data);
        }
        
        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
        
        // Getters
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public T getData() {
            return data;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * 获取插件图标
     */
    @GetMapping("/{pluginId}/icon")
    public ResponseEntity<Resource> getPluginIcon(@PathVariable String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            if (!plugin.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            PluginInfo pluginInfo = plugin.get();
            
            // 获取图标文件名
            String iconFileName = null;
            iconFileName = pluginInfo.getIconPath();
            
            if (iconFileName == null || iconFileName.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // 检查图标文件是否存在
            if (!pluginIconService.iconExists(pluginId, iconFileName)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // 获取图标文件路径
            Path iconPath = pluginIconService.getIconPhysicalPath(pluginId, iconFileName);
            if (iconPath == null || !Files.exists(iconPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // 确定MIME类型
            String contentType = Files.probeContentType(iconPath);
            if (contentType == null) {
                // 根据文件扩展名确定类型
                String fileName = iconPath.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                } else if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            
            Resource resource = new FileSystemResource(iconPath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + iconPath.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Failed to get plugin icon: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取插件图标访问URL
     */
    @GetMapping("/{pluginId}/icon-url")
    public ResponseEntity<ApiResponse<String>> getPluginIconUrl(@PathVariable String pluginId) {
        try {
            Optional<PluginInfo> plugin = pluginManager.getPlugin(pluginId);
            if (!plugin.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Plugin not found: " + pluginId));
            }
            
            PluginInfo pluginInfo = plugin.get();
            
            // 获取图标文件名
            String iconFileName = null;
            iconFileName = pluginInfo.getIconPath();
            
            if (iconFileName == null || iconFileName.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success(null, "No icon available"));
            }
            
            // 检查图标文件是否存在
            if (!pluginIconService.iconExists(pluginId, iconFileName)) {
                return ResponseEntity.ok(ApiResponse.success(null, "Icon file not found"));
            }
            
            // 生成访问URL
            String iconUrl = "/plugins/" + pluginId + "/icon";
            
            return ResponseEntity.ok(ApiResponse.success(iconUrl));
            
        } catch (Exception e) {
            log.error("Failed to get plugin icon URL: {}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get plugin icon URL: " + e.getMessage()));
        }
    }
    
    /**
     * 获取临时插件图标
     */
    @GetMapping("/temp-icons/{iconId}")
    public ResponseEntity<Resource> getTempIcon(@PathVariable String iconId) {
        try {
            Path tempIconPath = Paths.get(tempIconDir, iconId);
            if (!Files.exists(tempIconPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // 确定MIME类型
            String contentType = Files.probeContentType(tempIconPath);
            if (contentType == null) {
                // 根据文件扩展名确定类型
                String fileName = tempIconPath.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                } else if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            
            Resource resource = new FileSystemResource(tempIconPath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + tempIconPath.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Failed to get temp icon: {}", iconId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 保存临时图标文件
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @param iconData 图标数据
     * @return 临时图标访问URL
     */
    private String saveTempIcon(String pluginId, String iconFileName, byte[] iconData) {
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
    
    /**
     * 获取文件扩展名（包含点号）
     * 
     * @param fileName 文件名
     * @return 文件扩展名，如果没有扩展名返回空字符串
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
    
}
