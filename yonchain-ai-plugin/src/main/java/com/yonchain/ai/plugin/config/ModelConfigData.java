package com.yonchain.ai.plugin.config;

import java.util.List;
import java.util.Map;

/**
 * 模型配置数据类
 * 从deepseek-chat.yaml等模型配置文件解析的纯配置数据
 */
public class ModelConfigData {
    private String model;
    private String modelType;
    private Map<String, String> label;
    private List<String> features;
    private Map<String, Object> modelProperties;
    private List<Map<String, Object>> parameterRules;
    private Map<String, Object> pricing;
    
    // getters and setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    
    public Map<String, String> getLabel() { return label; }
    public void setLabel(Map<String, String> label) { this.label = label; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    public Map<String, Object> getModelProperties() { return modelProperties; }
    public void setModelProperties(Map<String, Object> modelProperties) { this.modelProperties = modelProperties; }
    
    public List<Map<String, Object>> getParameterRules() { return parameterRules; }
    public void setParameterRules(List<Map<String, Object>> parameterRules) { this.parameterRules = parameterRules; }
    
    public Map<String, Object> getPricing() { return pricing; }
    public void setPricing(Map<String, Object> pricing) { this.pricing = pricing; }
    
    /**
     * 获取本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label == null) return model;
        return label.getOrDefault(locale, label.getOrDefault("en_US", model));
    }
}