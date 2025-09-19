package com.yonchain.ai.plugin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件图标管理服务
 * 负责从JAR文件中提取插件图标并保存到本地存储
 * 
 * @author yonchain
 */
@Service
public class PluginIconService {
    
    private static final Logger log = LoggerFactory.getLogger(PluginIconService.class);
    
    private static final String ASSETS_DIR = "_assets/";
    
    @Value("${yonchain.plugin.icon.storage-path:${java.io.tmpdir}/yonchain-plugins/icons}")
    private String iconStoragePath;
    
    /**
     * 保存插件图标数据到文件
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @param iconData 图标数据
     * @return 保存后的图标文件路径，如果保存失败返回null
     */
    public String saveIconData(String pluginId, String iconFileName, byte[] iconData) {
        if (iconData == null || iconData.length == 0) {
            log.debug("No icon data to save for plugin: {}", pluginId);
            return null;
        }
        
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            log.debug("No icon filename specified for plugin: {}", pluginId);
            return null;
        }
        
        try {
            // 确保图标存储目录存在
            Path iconDir = createIconStorageDirectory();
            
            // 生成保存的文件名：插件ID_icon.扩展名
            String fileExtension = getFileExtension(iconFileName);
            String savedFileName = pluginId + "_icon" + fileExtension;
            Path savedIconPath = iconDir.resolve(savedFileName);
            
            // 保存图标数据到文件
            Files.write(savedIconPath, iconData);
            
            log.info("Successfully saved icon for plugin {}: {}", pluginId, savedIconPath);
            return savedIconPath.toString();
            
        } catch (Exception e) {
            log.error("Failed to save icon data for plugin {}: {}", pluginId, iconFileName, e);
            return null;
        }
    }
    
    /**
     * 从JAR文件中提取并保存插件图标（已废弃，使用saveIconData替代）
     * 
     * @param jarPath JAR文件路径
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名（来自plugin.yaml的icon字段）
     * @return 保存后的图标文件路径，如果提取失败返回null
     * @deprecated 使用 saveIconData(String, String, byte[]) 替代
     */
    @Deprecated
    public String extractAndSaveIcon(Path jarPath, String pluginId, String iconFileName) {
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            log.debug("No icon specified for plugin: {}", pluginId);
            return null;
        }
        
        try {
            // 确保图标存储目录存在
            Path iconDir = createIconStorageDirectory();
            
            // 在JAR文件中查找图标文件
            String iconPath = findIconInJar(jarPath, iconFileName);
            if (iconPath == null) {
                log.warn("Icon file not found in JAR for plugin {}: {}", pluginId, iconFileName);
                return null;
            }
            
            // 提取并保存图标文件
            String savedIconPath = extractIconFromJar(jarPath, iconPath, pluginId, iconFileName, iconDir);
            
            if (savedIconPath != null) {
                log.info("Successfully extracted icon for plugin {}: {}", pluginId, savedIconPath);
            }
            
            return savedIconPath;
            
        } catch (Exception e) {
            log.error("Failed to extract icon for plugin {}: {}", pluginId, iconFileName, e);
            return null;
        }
    }
    
    /**
     * 获取插件图标的访问路径
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @return 图标访问路径
     */
    public String getIconAccessPath(String pluginId, String iconFileName) {
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            return null;
        }
        
        String fileExtension = getFileExtension(iconFileName);
        String savedFileName = pluginId + "_icon" + fileExtension;
        
        return "/plugins/icons/" + savedFileName;
    }
    
    /**
     * 检查插件图标是否存在
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @return 图标文件是否存在
     */
    public boolean iconExists(String pluginId, String iconFileName) {
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path iconDir = Paths.get(iconStoragePath);
            String fileExtension = getFileExtension(iconFileName);
            String savedFileName = pluginId + "_icon" + fileExtension;
            Path iconFile = iconDir.resolve(savedFileName);
            
            return Files.exists(iconFile);
        } catch (Exception e) {
            log.error("Failed to check icon existence for plugin {}: {}", pluginId, iconFileName, e);
            return false;
        }
    }
    
    /**
     * 删除插件图标文件
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @return 是否删除成功
     */
    public boolean deleteIcon(String pluginId, String iconFileName) {
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            return true; // 没有图标文件，认为删除成功
        }
        
        try {
            Path iconDir = Paths.get(iconStoragePath);
            String fileExtension = getFileExtension(iconFileName);
            String savedFileName = pluginId + "_icon" + fileExtension;
            Path iconFile = iconDir.resolve(savedFileName);
            
            if (Files.exists(iconFile)) {
                Files.delete(iconFile);
                log.info("Deleted icon file for plugin {}: {}", pluginId, iconFile);
                return true;
            }
            
            return true; // 文件不存在，认为删除成功
            
        } catch (Exception e) {
            log.error("Failed to delete icon for plugin {}: {}", pluginId, iconFileName, e);
            return false;
        }
    }
    
    /**
     * 获取图标文件的物理路径
     * 
     * @param pluginId 插件ID
     * @param iconFileName 图标文件名
     * @return 图标文件的物理路径
     */
    public Path getIconPhysicalPath(String pluginId, String iconFileName) {
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            return null;
        }
        
        try {
            Path iconDir = Paths.get(iconStoragePath);
            String fileExtension = getFileExtension(iconFileName);
            String savedFileName = pluginId + "_icon" + fileExtension;
            
            return iconDir.resolve(savedFileName);
        } catch (Exception e) {
            log.error("Failed to get icon physical path for plugin {}: {}", pluginId, iconFileName, e);
            return null;
        }
    }
    
    /**
     * 创建图标存储目录
     * 
     * @return 图标存储目录路径
     * @throws IOException IO异常
     */
    private Path createIconStorageDirectory() throws IOException {
        Path iconDir = Paths.get(iconStoragePath);
        if (!Files.exists(iconDir)) {
            Files.createDirectories(iconDir);
            log.info("Created icon storage directory: {}", iconDir);
        }
        return iconDir;
    }
    
    /**
     * 在JAR文件中查找图标文件
     * 
     * @param jarPath JAR文件路径
     * @param iconFileName 图标文件名
     * @return 图标在JAR中的完整路径，未找到返回null
     */
    private String findIconInJar(Path jarPath, String iconFileName) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            // 优先在_assets目录中查找
            String assetsIconPath = ASSETS_DIR + iconFileName;
            JarEntry assetsEntry = jarFile.getJarEntry(assetsIconPath);
            if (assetsEntry != null) {
                return assetsIconPath;
            }
            
            // 如果_assets目录中没有，尝试直接查找
            JarEntry directEntry = jarFile.getJarEntry(iconFileName);
            if (directEntry != null) {
                return iconFileName;
            }
            
            // 搜索所有以该文件名结尾的条目
            return jarFile.stream()
                    .map(JarEntry::getName)
                    .filter(name -> name.endsWith("/" + iconFileName) || name.equals(iconFileName))
                    .findFirst()
                    .orElse(null);
                    
        } catch (IOException e) {
            log.error("Failed to search icon in JAR: {}", jarPath, e);
            return null;
        }
    }
    
    /**
     * 从JAR文件中提取图标文件
     * 
     * @param jarPath JAR文件路径
     * @param iconPath 图标在JAR中的路径
     * @param pluginId 插件ID
     * @param iconFileName 原始图标文件名
     * @param iconDir 图标存储目录
     * @return 保存后的图标文件路径
     */
    private String extractIconFromJar(Path jarPath, String iconPath, String pluginId, 
                                     String iconFileName, Path iconDir) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry iconEntry = jarFile.getJarEntry(iconPath);
            if (iconEntry == null) {
                return null;
            }
            
            // 生成保存的文件名：插件ID_icon.扩展名
            String fileExtension = getFileExtension(iconFileName);
            String savedFileName = pluginId + "_icon" + fileExtension;
            Path savedIconPath = iconDir.resolve(savedFileName);
            
            // 提取图标文件
            try (InputStream iconStream = jarFile.getInputStream(iconEntry)) {
                Files.copy(iconStream, savedIconPath, StandardCopyOption.REPLACE_EXISTING);
                log.debug("Icon extracted to: {}", savedIconPath);
                return savedIconPath.toString();
            }
            
        } catch (IOException e) {
            log.error("Failed to extract icon from JAR: {}", jarPath, e);
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
