package com.yonchain.ai.plugin.entity;

/**
 * 插件服务实体
 * 
 * @author yonchain
 */
public class PluginServiceEntity {
    
    private Long id;
    private String pluginId;
    private String serviceName;
    private String serviceClass;
    
    // 默认构造函数
    public PluginServiceEntity() {}
    
    // 带参数构造函数
    public PluginServiceEntity(String pluginId, String serviceName, String serviceClass) {
        this.pluginId = pluginId;
        this.serviceName = serviceName;
        this.serviceClass = serviceClass;
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
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceClass() {
        return serviceClass;
    }
    
    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
    
    @Override
    public String toString() {
        return "PluginServiceEntity{" +
                "id=" + id +
                ", pluginId='" + pluginId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceClass='" + serviceClass + '\'' +
                '}';
    }
}
