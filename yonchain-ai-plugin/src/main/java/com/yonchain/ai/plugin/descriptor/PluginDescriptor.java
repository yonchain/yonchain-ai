package com.yonchain.ai.plugin.descriptor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * 插件描述符
 * 用于描述插件的基本信息（从plugin.yaml解析）
 * 
 * @author yonchain
 */
public class PluginDescriptor {
    
    /**
     * 插件唯一标识符
     */
    private String id;
    
    /**
     * 插件名称
     */
    private String name;
    
    /**
     * 插件版本
     */
    private String version;
    
    /**
     * 插件作者
     */
    private String author;
    
    /**
     * 插件类型
     */
    private String type;
    
    /**
     * 创建时间
     */
    private String createdAt;
    
    /**
     * 描述信息（多语言支持）
     */
    private Map<String, String> description;
    
    /**
     * 图标文件名
     */
    private String icon;
    
    /**
     * 图标数据（从JAR中提取）
     */
    private byte[] iconData;
    
    /**
     * 标签信息（多语言支持）
     */
    private Map<String, String> label;
    
    /**
     * 提供商配置文件列表（如deepseek.yaml）
     */
    private List<String> plugins;
    
    /**
     * 资源配置
     */
    private ResourceConfig resource;
    
    /**
     * 插件路径（运行时设置）
     */
    private Path pluginPath;
    
    /**
     * 插件主类（新增字段）
     */
    private String pluginClass;
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Map<String, String> getDescription() {
        return description;
    }
    
    public void setDescription(Map<String, String> description) {
        this.description = description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public byte[] getIconData() {
        return iconData;
    }
    
    public void setIconData(byte[] iconData) {
        this.iconData = iconData;
    }
    
    public Map<String, String> getLabel() {
        return label;
    }
    
    public void setLabel(Map<String, String> label) {
        this.label = label;
    }
    
    public List<String> getPlugins() {
        return plugins;
    }
    
    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
    
    public ResourceConfig getResource() {
        return resource;
    }
    
    public void setResource(ResourceConfig resource) {
        this.resource = resource;
    }
    
    public Path getPluginPath() {
        return pluginPath;
    }
    
    public void setPluginPath(Path pluginPath) {
        this.pluginPath = pluginPath;
    }
    
    public String getPluginClass() {
        return pluginClass;
    }
    
    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }
    
    /**
     * 获取本地化描述
     * 
     * @param locale 语言环境
     * @return 本地化描述
     */
    public String getLocalizedDescription(String locale) {
        if (description != null) {
            return description.get(locale);
        }
        return null;
    }
    
    /**
     * 获取本地化标签
     * 
     * @param locale 语言环境
     * @return 本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label != null) {
            return label.get(locale);
        }
        return null;
    }
    
    /**
     * 检查是否有提供商配置
     * 
     * @return 是否有提供商配置
     */
    public boolean hasProviders() {
        return plugins != null && !plugins.isEmpty();
    }
    
    @Override
    public String toString() {
        return "PluginDescriptor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", pluginClass='" + pluginClass + '\'' +
                '}';
    }
}