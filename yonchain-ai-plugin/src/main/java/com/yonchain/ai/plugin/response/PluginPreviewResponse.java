package com.yonchain.ai.plugin.response;

import com.yonchain.ai.plugin.descriptor.PluginDescriptor;

import java.util.Map;

/**
 * 插件预览响应对象
 * 用于在安装插件前返回插件基本信息
 * 
 * @author yonchain
 */
public class PluginPreviewResponse {
    
    /**
     * 插件唯一标识符
     */
    private String pluginId;
    
    /**
     * 插件名称
     */
    private String name;
    
    /**
     * 插件版本
     */
    private String version;
    
    /**
     * 插件描述（多语言支持）
     */
    private Map<String, String> description;
    
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
     * 标签信息（多语言支持）
     */
    private Map<String, String> label;
    
    /**
     * 图标文件名
     */
    private String iconFileName;
    
    /**
     * 图标访问URL（临时URL）
     */
    private String iconUrl;
    
    /**
     * 插件主类
     */
    private String pluginClass;
    
    /**
     * 是否有图标
     */
    private boolean hasIcon;
    
    // 私有构造函数，通过静态方法创建
    private PluginPreviewResponse() {
    }
    
    /**
     * 从PluginDescriptor创建预览响应对象
     * 
     * @param descriptor 插件描述符
     * @param tempIconUrl 临时图标URL
     * @return 预览响应对象
     */
    public static PluginPreviewResponse fromPluginDescriptor(PluginDescriptor descriptor, String tempIconUrl) {
        PluginPreviewResponse response = new PluginPreviewResponse();
        
        // 基本信息
        response.pluginId = descriptor.getId();
        response.name = descriptor.getName();
        response.version = descriptor.getVersion();
        response.description = descriptor.getDescription();
        response.author = descriptor.getAuthor();
        response.type = descriptor.getType();
        response.createdAt = descriptor.getCreatedAt();
        response.label = descriptor.getLabel();
        response.iconFileName = descriptor.getIcon();
        response.pluginClass = descriptor.getPluginClass();
        
        // 图标信息
        response.hasIcon = descriptor.getIcon() != null && !descriptor.getIcon().trim().isEmpty();
        response.iconUrl = tempIconUrl;
        
        return response;
    }
    
    // Getters
    public String getPluginId() {
        return pluginId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public Map<String, String> getDescription() {
        return description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getType() {
        return type;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public Map<String, String> getLabel() {
        return label;
    }
    
    public String getIconFileName() {
        return iconFileName;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public String getPluginClass() {
        return pluginClass;
    }
    
    public boolean isHasIcon() {
        return hasIcon;
    }
    
    /**
     * 获取本地化描述
     * 
     * @param locale 语言环境（如：zh_Hans, en_US）
     * @return 本地化描述，如果没有对应语言返回null
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
     * @param locale 语言环境（如：zh_Hans, en_US）
     * @return 本地化标签，如果没有对应语言返回null
     */
    public String getLocalizedLabel(String locale) {
        if (label != null) {
            return label.get(locale);
        }
        return null;
    }
}
