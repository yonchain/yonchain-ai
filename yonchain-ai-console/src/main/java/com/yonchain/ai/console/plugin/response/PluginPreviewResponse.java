package com.yonchain.ai.console.plugin.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 插件预览响应类
 * 用于插件预览功能
 * 
 * @author yonchain
 */
@Data
public class PluginPreviewResponse {
    
    private String pluginId;
    private String name;
    private String version;
    private String description;
    private String author;
    private String type;
    private String mainClass;
    private String iconUrl;
    private List<String> dependencies;
    private List<String> extensions;
    private List<String> services;
    private LocalDateTime createdAt;
    
    /**
     * 从PluginDescriptor Map创建预览响应
     * 
     * @param descriptorMap 插件描述符Map
     * @param tempIconUrl 临时图标URL
     * @return PluginPreviewResponse对象
     */
    @SuppressWarnings("unchecked")
    public static PluginPreviewResponse fromDescriptorMap(Map<String, Object> descriptorMap, String tempIconUrl) {
        PluginPreviewResponse response = new PluginPreviewResponse();
        
        response.setPluginId((String) descriptorMap.get("pluginId"));
        response.setName((String) descriptorMap.get("name"));
        response.setVersion((String) descriptorMap.get("version"));
        response.setDescription((String) descriptorMap.get("description"));
        response.setAuthor((String) descriptorMap.get("author"));
        response.setType((String) descriptorMap.get("type"));
        response.setMainClass((String) descriptorMap.get("mainClass"));
        response.setIconUrl(tempIconUrl);
        response.setDependencies((List<String>) descriptorMap.get("dependencies"));
        response.setExtensions((List<String>) descriptorMap.get("extensions"));
        response.setServices((List<String>) descriptorMap.get("services"));
        response.setCreatedAt((LocalDateTime) descriptorMap.get("createdAt"));
        
        return response;
    }

    /**
     * 从PluginDescriptor创建预览响应（保留兼容性）
     * 
     * @param descriptor 插件描述符
     * @param tempIconUrl 临时图标URL
     * @return PluginPreviewResponse对象
     * @deprecated 使用 fromDescriptorMap 替代
     */
    @Deprecated
    public static PluginPreviewResponse fromPluginDescriptor(Object descriptor, String tempIconUrl) {
        // 这个方法保留是为了向后兼容，实际上现在不会被调用
        throw new UnsupportedOperationException("Use fromDescriptorMap instead");
    }
}
