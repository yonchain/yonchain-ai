package com.yonchain.ai.plugin.descriptor;

import java.util.Map;

/**
 * 插件扩展点信息
 * 
 * @author yonchain
 */
public class Extension {
    
    /**
     * 扩展点名称
     */
    private String point;
    
    /**
     * 实现类名
     */
    private String implementation;
    
    /**
     * 扩展配置
     */
    private Map<String, Object> config;
    
    /**
     * 排序索引
     */
    private int order;
    
    /**
     * 是否激活
     */
    private boolean active;
    
    public Extension() {
        this.active = true;
        this.order = 0;
    }
    
    public Extension(String point, String implementation) {
        this();
        this.point = point;
        this.implementation = implementation;
    }
    
    public Extension(String point, String implementation, Map<String, Object> config) {
        this(point, implementation);
        this.config = config;
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
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * 检查是否有配置信息
     * 
     * @return 是否有配置
     */
    public boolean hasConfig() {
        return config != null && !config.isEmpty();
    }
    
    /**
     * 获取配置值
     * 
     * @param key 配置键
     * @return 配置值
     */
    public Object getConfigValue(String key) {
        return hasConfig() ? config.get(key) : null;
    }
    
    /**
     * 获取配置值（带默认值）
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public Object getConfigValue(String key, Object defaultValue) {
        Object value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public String toString() {
        return "Extension{" +
                "point='" + point + '\'' +
                ", implementation='" + implementation + '\'' +
                ", config=" + config +
                ", order=" + order +
                ", active=" + active +
                '}';
    }
}

