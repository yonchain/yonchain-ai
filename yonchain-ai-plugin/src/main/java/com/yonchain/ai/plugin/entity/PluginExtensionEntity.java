package com.yonchain.ai.plugin.entity;

/**
 * 插件扩展点实体
 * 
 * @author yonchain
 */
public class PluginExtensionEntity {
    
    private Long id;
    private String pluginId;
    private String point;
    private String implementation;
    
    // 默认构造函数
    public PluginExtensionEntity() {}
    
    // 带参数构造函数
    public PluginExtensionEntity(String pluginId, String point, String implementation) {
        this.pluginId = pluginId;
        this.point = point;
        this.implementation = implementation;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
    
    public String getPoint() {
        return point;
    }
    
    public void setPoint(String point) {
        this.point = point;
    }
    
    public String getImplementation() {
        return implementation;
    }
    
    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }
    
    @Override
    public String toString() {
        return "PluginExtensionEntity{" +
                "id=" + id +
                ", pluginId='" + pluginId + '\'' +
                ", point='" + point + '\'' +
                ", implementation='" + implementation + '\'' +
                '}';
    }
}
