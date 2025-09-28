package com.yonchain.ai.plugin.config;

import java.util.List;
import java.util.Map;

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
    private String pluginClass;
    private String icon;
    private Map<String, String> description;
    private Map<String, String> label;
    private List<String> plugins;
    private Map<String, Object> resource;
    
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
    
    public String getPluginClass() { return pluginClass; }
    public void setPluginClass(String pluginClass) { this.pluginClass = pluginClass; }
    
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
    
    /**
     * 检查是否有插件类
     */
    public boolean hasPluginClass() {
        return pluginClass != null && !pluginClass.trim().isEmpty();
    }
}
