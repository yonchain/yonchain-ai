package com.yonchain.ai.plugin.parser.impl;

import com.yonchain.ai.plugin.config.PluginConfig;
// import com.yonchain.ai.plugin.descriptor.ResourceConfig; // 已删除，简化处理
import com.yonchain.ai.plugin.exception.PluginParseException;
import com.yonchain.ai.plugin.parser.PluginParser;
import com.yonchain.ai.plugin.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * YAML格式插件解析器实现
 * 
 * @author yonchain
 */
@Component
public class YamlPluginParser implements PluginParser {
    
    private static final Logger log = LoggerFactory.getLogger(YamlPluginParser.class);
    
    private static final String PLUGIN_YAML = "plugin.yaml";
    private static final String PLUGIN_XML = "plugin.xml"; // 兼容XML格式
    
    private final Yaml yaml = new Yaml();
    
    @Override
    public PluginConfig parsePlugin(Path pluginPath) throws PluginParseException {
        if (!isValidPluginPath(pluginPath)) {
            throw new PluginParseException("Invalid plugin path: " + pluginPath);
        }
        
        try {
            // 只支持JAR文件格式
            if (Files.isRegularFile(pluginPath) && pluginPath.toString().endsWith(".jar")) {
                return parsePluginFromJar(pluginPath);
            } else {
                throw new PluginParseException("Only JAR plugin format is supported: " + pluginPath);
            }
            
        } catch (IOException e) {
            throw new PluginParseException("Failed to parse plugin: " + pluginPath, e);
        }
    }


    @Override
    public ValidationResult validatePlugin(PluginConfig descriptor) {
        ValidationResult result = new ValidationResult();

        // 验证基本信息
        if (descriptor.getId() == null || descriptor.getId().trim().isEmpty()) {
            result.addError("Plugin ID cannot be null or empty");
        }

        if (descriptor.getName() == null || descriptor.getName().trim().isEmpty()) {
            result.addError("Plugin name cannot be null or empty");
        }

        if (descriptor.getVersion() == null || descriptor.getVersion().trim().isEmpty()) {
            result.addError("Plugin version cannot be null or empty");
        }

        if (descriptor.getType() == null || descriptor.getType().trim().isEmpty()) {
            result.addError("Plugin type cannot be null or empty");
        }

        if (descriptor.getAuthor() == null || descriptor.getAuthor().trim().isEmpty()) {
            result.addWarning("Plugin author is not specified");
        }

        // 验证提供商配置文件
        if ("model".equals(descriptor.getType())) {
            if (descriptor.getPlugins() == null || descriptor.getPlugins().isEmpty()) {
                result.addError("Model plugin must specify provider configuration files");
            }
        }

        return result;
    }



    /*
        if (!isValidPluginPath(pluginPath)) {
            throw new PluginParseException("Invalid plugin path: " + pluginPath);
        }
        
        try {
            // 只支持JAR文件格式
            if (Files.isRegularFile(pluginPath) && pluginPath.toString().endsWith(".jar")) {
                return parsePluginFromJar(pluginPath);
            } else {
                throw new PluginParseException("Only JAR plugin format is supported: " + pluginPath);
            }
            
        } catch (IOException e) {
            throw new PluginParseException("Failed to parse plugin: " + pluginPath, e);
        }
    }
    
    @Override
    public ValidationResult validatePlugin(PluginConfig pluginConfig) {
        ValidationResult result = new ValidationResult();
        
        // 验证基本信息
        if (pluginConfig.getId() == null || pluginConfig.getId().trim().isEmpty()) {
            result.addError("Plugin ID cannot be null or empty");
        }
        
        if (pluginConfig.getName() == null || pluginConfig.getName().trim().isEmpty()) {
            result.addError("Plugin name cannot be null or empty");
        }
        
        if (pluginConfig.getVersion() == null || pluginConfig.getVersion().trim().isEmpty()) {
            result.addError("Plugin version cannot be null or empty");
        }
        
        if (pluginConfig.getType() == null || pluginConfig.getType().trim().isEmpty()) {
            result.addError("Plugin type cannot be null or empty");
        }
        
        if (pluginConfig.getAuthor() == null || pluginConfig.getAuthor().trim().isEmpty()) {
            result.addWarning("Plugin author is not specified");
        }
        
        // 验证提供商配置文件
        if ("model".equals(pluginConfig.getType())) {
            if (pluginConfig.getPlugins() == null || pluginConfig.getPlugins().isEmpty()) {
                result.addError("Model plugin must specify provider configuration files");
            }
        }
        
        return result;
    }

    /**
     * 从JAR文件解析插件
     * 
     * @param jarPath JAR文件路径
     * @return 插件配置
     * @throws IOException IO异常
     * @throws PluginParseException 解析异常
     */
    private PluginConfig parsePluginFromJar(Path jarPath) throws IOException, PluginParseException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            // 优先尝试解析plugin.yaml
            JarEntry yamlEntry = jarFile.getJarEntry(PLUGIN_YAML);
            if (yamlEntry != null) {
                PluginConfig pluginConfig;
                try (InputStream inputStream = jarFile.getInputStream(yamlEntry)) {
                    pluginConfig = parsePluginYamlFromStream(inputStream, jarPath);
                }
                
                // 设置插件路径
                pluginConfig.setPluginPath(jarPath);
                
                // 提取图标数据
                extractIconFromJar(jarFile, pluginConfig);
                
                return pluginConfig;
            }
            
            // 如果没有YAML文件，尝试XML文件（兼容性）
            JarEntry xmlEntry = jarFile.getJarEntry(PLUGIN_XML);
            if (xmlEntry != null) {
                log.warn("Using deprecated plugin.xml format for plugin: {}", jarPath);
                throw new PluginParseException("XML plugin format not yet supported, please use plugin.yaml");
            }
            
            throw new PluginParseException("No plugin descriptor found (plugin.yaml or plugin.xml) in JAR: " + jarPath);
        }
    }
    
    /**
     * 从输入流解析plugin.yaml
     * 
     * @param inputStream 输入流
     * @param pluginPath 插件路径（用于错误信息）
     * @return 插件配置
     * @throws PluginParseException 解析异常
     */
    private PluginConfig parsePluginYamlFromStream(InputStream inputStream, Path pluginPath) throws PluginParseException {
        try {
            Map<String, Object> yamlData = yaml.load(inputStream);
            
            if (yamlData == null) {
                throw new PluginParseException("Empty or invalid YAML file in plugin: " + pluginPath);
            }
            
            return buildPluginConfig(yamlData, pluginPath);
            
        } catch (Exception e) {
            throw new PluginParseException("Failed to parse plugin YAML in: " + pluginPath, e);
        }
    }
    
    /**
     * 构建插件配置
     * 
     * @param yamlData YAML数据
     * @param pluginPath 插件路径（JAR文件）
     * @return 插件配置
     * @throws PluginParseException 解析异常
     */
    @SuppressWarnings("unchecked")
    private PluginConfig buildPluginConfig(Map<String, Object> yamlData, Path pluginPath) throws PluginParseException {
        try {
            PluginConfig pluginConfig = new PluginConfig();
            
            // 基本信息
            pluginConfig.setId(getString(yamlData, "id"));
            pluginConfig.setName(getString(yamlData, "name"));
            pluginConfig.setVersion(getString(yamlData, "version"));
            pluginConfig.setAuthor(getString(yamlData, "author"));
            pluginConfig.setType(getString(yamlData, "type"));
            pluginConfig.setIcon(getString(yamlData, "icon"));
            
            // 描述信息（多语言支持）
            Object descObj = yamlData.get("description");
            if (descObj instanceof Map) {
                pluginConfig.setDescription((Map<String, String>) descObj);
            }
            
            // 标签信息（多语言支持）
            Object labelObj = yamlData.get("label");
            if (labelObj instanceof Map) {
                pluginConfig.setLabel((Map<String, String>) labelObj);
            }
            
            // 提供商配置文件列表（如deepseek.yaml）
            List<String> pluginFiles = getStringList(yamlData, "plugins");
            if (pluginFiles != null) {
                pluginConfig.setPlugins(pluginFiles);
            }
            
            // 资源配置
            Map<String, Object> resourceData = (Map<String, Object>) yamlData.get("resource");
            if (resourceData != null) {
                pluginConfig.setResource(resourceData);
            }
            
            return pluginConfig;
            
        } catch (Exception e) {
            throw new PluginParseException("Failed to build plugin config", e);
        }
    }
    
    /**
     * 从YAML数据中获取字符串值
     * 
     * @param data YAML数据
     * @param key 键
     * @return 字符串值
     */
    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 从YAML数据中获取字符串列表
     * 
     * @param data YAML数据
     * @param key 键
     * @return 字符串列表
     */
    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return null;
    }
    
    @Override
    public boolean isValidPluginPath(Path pluginPath) {
        if (!Files.exists(pluginPath)) {
            return false;
        }
        
        // 只检查JAR文件
        if (Files.isRegularFile(pluginPath) && pluginPath.toString().endsWith(".jar")) {
            return isValidPluginJar(pluginPath);
        }
        
        return false;
    }
    
    /**
     * 从JAR文件中提取图标数据
     * 
     * @param jarFile JAR文件
     * @param pluginConfig 插件配置
     */
    private void extractIconFromJar(JarFile jarFile, PluginConfig pluginConfig) {
        String iconFileName = pluginConfig.getIcon();
        if (iconFileName == null || iconFileName.trim().isEmpty()) {
            log.debug("No icon specified for plugin: {}", pluginConfig.getId());
            return;
        }
        
        try {
            // 在JAR文件中查找图标文件
            String iconPath = findIconInJar(jarFile, iconFileName);
            if (iconPath == null) {
                log.warn("Icon file not found in JAR for plugin {}: {}", pluginConfig.getId(), iconFileName);
                return;
            }
            
            // 提取图标数据
            JarEntry iconEntry = jarFile.getJarEntry(iconPath);
            if (iconEntry != null) {
                try (InputStream iconStream = jarFile.getInputStream(iconEntry)) {
                    byte[] iconData = iconStream.readAllBytes();
                    pluginConfig.setIconData(iconData);
                    log.debug("Icon data extracted for plugin {}: {} bytes", pluginConfig.getId(), iconData.length);
                } catch (IOException e) {
                    log.warn("Failed to read icon data for plugin {}: {}", pluginConfig.getId(), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.warn("Failed to extract icon for plugin {}: {}", pluginConfig.getId(), e.getMessage());
        }
    }
    
    /**
     * 在JAR文件中查找图标文件
     * 
     * @param jarFile JAR文件
     * @param iconFileName 图标文件名
     * @return 图标在JAR中的完整路径，未找到返回null
     */
    private String findIconInJar(JarFile jarFile, String iconFileName) {
        // 优先在_assets目录中查找
        String assetsIconPath = "_assets/" + iconFileName;
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
    }
    
    /**
     * 检查JAR文件是否是有效的插件JAR
     * 
     * @param jarPath JAR文件路径
     * @return 是否有效
     */
    private boolean isValidPluginJar(Path jarPath) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            // 检查JAR中是否包含plugin.yaml或plugin.xml
            return jarFile.getEntry(PLUGIN_YAML) != null || 
                   jarFile.getEntry(PLUGIN_XML) != null;
        } catch (IOException e) {
            log.debug("Failed to check JAR file: {}", jarPath, e);
            return false;
        }
    }
}
