package com.yonchain.ai.model;

import java.util.Properties;

/**
 * 环境配置类
 * 
 * 参考 MyBatis Environment 设计，包含环境标识和配置属性，支持占位符解析
 */
public class ModelEnvironment {
    
    private final String id;
    private final Properties properties;
    
    public ModelEnvironment(String id) {
        this.id = id;
        this.properties = new Properties();
    }
    
    public ModelEnvironment(String id, Properties properties) {
        this.id = id;
        this.properties = new Properties(properties);
    }
    
    // ================== 基本属性 ==================
    
    public String getId() {
        return id;
    }
    
    // ================== Properties 管理 ==================
    
    public String getProperty(String key) {
        return resolveProperty(properties.getProperty(key));
    }
    
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? resolveProperty(value) : defaultValue;
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public Properties getProperties() {
        return new Properties(properties);
    }
    
    public void setProperties(Properties properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }
    
    
    // ================== 占位符解析 ==================
    
    /**
     * 解析属性值中的占位符
     * 支持格式：${key} 或 ${key:defaultValue}
     */
    private String resolveProperty(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        
        String resolved = value;
        int start = 0;
        
        while ((start = resolved.indexOf("${", start)) != -1) {
            int end = resolved.indexOf("}", start);
            if (end == -1) {
                break;
            }
            
            String placeholder = resolved.substring(start + 2, end);
            String replacement = resolvePlaceholder(placeholder);
            
            if (replacement != null) {
                resolved = resolved.substring(0, start) + replacement + resolved.substring(end + 1);
                start += replacement.length();
            } else {
                start = end + 1;
            }
        }
        
        return resolved;
    }
    
    /**
     * 解析单个占位符
     * 查找顺序：系统属性 -> 当前属性 -> 默认值
     */
    private String resolvePlaceholder(String placeholder) {
        String key;
        String defaultValue = null;
        
        // 解析默认值 ${key:defaultValue}
        int colonIndex = placeholder.indexOf(':');
        if (colonIndex != -1) {
            key = placeholder.substring(0, colonIndex);
            defaultValue = placeholder.substring(colonIndex + 1);
        } else {
            key = placeholder;
        }
        
        // 1. 查找系统属性
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        
        // 2. 查找当前属性
        value = properties.getProperty(key);
        if (value != null) {
            return value;
        }
        
        // 3. 返回默认值
        return defaultValue;
    }
    
    // ================== 便捷方法 ==================
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public long getLongProperty(String key, long defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public String toString() {
        return "Environment{" +
                "id='" + id + '\'' +
                ", propertiesCount=" + properties.size() +
                '}';
    }
}
