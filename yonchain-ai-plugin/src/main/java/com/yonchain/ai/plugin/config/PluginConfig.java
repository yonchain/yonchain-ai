package com.yonchain.ai.plugin.config;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件配置数据类
 * 从plugin.yaml解析的纯配置数据
 */
public class PluginConfig {
    private String id;
    private String name;
    private String version;
    private String author;
    private String type;
    private String icon;
    private Map<String, String> description;
    private Map<String, String> label;
    private List<String> plugins;
    private Map<String, Object> resource;
    
    // 运行时字段
    private Path pluginPath;
    private ProviderConfig providerConfig;
    private List<ModelConfigData> modelConfigs;
    private byte[] iconData; // 图标数据（从JAR中提取）
    
    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public Map<String, String> getDescription() { return description; }
    public void setDescription(Map<String, String> description) { this.description = description; }
    
    public Map<String, String> getLabel() { return label; }
    public void setLabel(Map<String, String> label) { this.label = label; }
    
    public List<String> getPlugins() { return plugins; }
    public void setPlugins(List<String> plugins) { this.plugins = plugins; }
    
    public Map<String, Object> getResource() { return resource; }
    public void setResource(Map<String, Object> resource) { this.resource = resource; }
    
    // 运行时字段的 getters and setters
    public Path getPluginPath() { return pluginPath; }
    public void setPluginPath(Path pluginPath) { this.pluginPath = pluginPath; }
    
    public ProviderConfig getProviderConfig() { return providerConfig; }
    public void setProviderConfig(ProviderConfig providerConfig) { this.providerConfig = providerConfig; }
    
    public List<ModelConfigData> getModelConfigs() { return modelConfigs; }
    public void setModelConfigs(List<ModelConfigData> modelConfigs) { this.modelConfigs = modelConfigs; }
    
    public byte[] getIconData() { return iconData; }
    public void setIconData(byte[] iconData) { this.iconData = iconData; }
    
    /**
     * 获取本地化描述
     */
    public String getLocalizedDescription(String locale) {
        if (description == null) return name;
        return description.getOrDefault(locale, description.getOrDefault("en_US", name));
    }
    
    /**
     * 获取本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label == null) return name;
        return label.getOrDefault(locale, label.getOrDefault("en_US", name));
    }
    
    // === 从 PluginDescriptor 迁移的方法 ===
    
    /**
     * 检查是否有提供商配置
     */
    public boolean hasProviders() {
        return plugins != null && !plugins.isEmpty();
    }
    
    /**
     * 获取配置文件的输入流
     */
    public InputStream getConfigInputStream(String configFileName) {
        try {
            if (pluginPath != null) {
                JarFile jarFile = new JarFile(pluginPath.toFile());
                JarEntry entry = jarFile.getJarEntry(configFileName);
                if (entry != null) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    // 注意：调用者需要负责关闭返回的InputStream，JarFile会在InputStream关闭时自动关闭
                    return inputStream;
                } else {
                    jarFile.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get config input stream for: " + configFileName, e);
        }
        throw new RuntimeException("Config file not found: " + configFileName);
    }
    
    /**
     * 获取模型配置文件列表
     */
    public List<String> getModelConfigFiles() {
        List<String> modelConfigFiles = new ArrayList<>();
        
        try {
            if (pluginPath != null) {
                JarFile jarFile = new JarFile(pluginPath.toFile());
                jarFile.stream()
                    .map(JarEntry::getName)
                    .filter(name -> name.startsWith("models/") && name.endsWith(".yaml"))
                    .filter(name -> !name.endsWith("_position.yaml")) // 排除位置配置文件
                    .forEach(modelConfigFiles::add);
                jarFile.close();
            }
        } catch (Exception e) {
            // 忽略错误，返回空列表
        }
        
        return modelConfigFiles;
    }
    
    // === 便利方法 ===
    
    /**
     * 获取提供商名称
     */
    public String getProvider() {
        return providerConfig != null ? providerConfig.getProvider() : null;
    }
    
    /**
     * 获取提供商源类
     */
    public String getProviderSource() {
        return providerConfig != null ? providerConfig.getProviderSource() : null;
    }
    
    /**
     * 获取选项处理器映射
     */
    public Map<String, String> getOptionsHandlers() {
        return providerConfig != null ? providerConfig.getOptionsHandlers() : new HashMap<>();
    }
    
}
