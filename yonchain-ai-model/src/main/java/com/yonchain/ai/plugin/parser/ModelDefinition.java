package com.yonchain.ai.plugin.parser;

import com.yonchain.ai.model.ModelType;

import java.util.Map;

/**
 * 模型定义
 * 从模型YAML文件解析出的模型信息
 * 
 * @author yonchain
 */
public class ModelDefinition {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 模型标签（多语言）
     */
    private Map<String, String> label;
    
    /**
     * 模型类型
     */
    private String modelType;
    
    /**
     * 模型特性
     */
    private String[] features;
    
    /**
     * 模型属性
     */
    private Map<String, Object> modelProperties;
    
    /**
     * 参数规则
     */
    private ParameterRule[] parameterRules;
    
    /**
     * 定价信息
     */
    private Map<String, Object> pricing;
    
    /**
     * 提供商名称
     */
    private String provider;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    public ModelDefinition() {
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Map<String, String> getLabel() {
        return label;
    }
    
    public void setLabel(Map<String, String> label) {
        this.label = label;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
    
    public String[] getFeatures() {
        return features;
    }
    
    public void setFeatures(String[] features) {
        this.features = features;
    }
    
    public Map<String, Object> getModelProperties() {
        return modelProperties;
    }
    
    public void setModelProperties(Map<String, Object> modelProperties) {
        this.modelProperties = modelProperties;
    }
    
    public ParameterRule[] getParameterRules() {
        return parameterRules;
    }
    
    public void setParameterRules(ParameterRule[] parameterRules) {
        this.parameterRules = parameterRules;
    }
    
    public Map<String, Object> getPricing() {
        return pricing;
    }
    
    public void setPricing(Map<String, Object> pricing) {
        this.pricing = pricing;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * 获取Spring AI模型类型
     * 
     * @return Spring AI模型类型
     */
    public ModelType getSpringAIModelType() {
        if (modelType == null) {
            return ModelType.TEXT; // 默认值
        }
        
        switch (modelType.toLowerCase()) {
            case "llm":
            case "chat":
                return ModelType.TEXT;
            case "image":
                return ModelType.IMAGE;
            case "audio":
                return ModelType.AUDIO;
            case "video":
                return ModelType.VIDEO;
            case "embedding":
                return ModelType.EMBEDDING;
            case "multimodal":
                return ModelType.MULTIMODAL;
            default:
                return ModelType.TEXT;
        }
    }
    
    /**
     * 获取模型显示名称
     * 
     * @param locale 语言代码，如"zh_Hans", "en_US"
     * @return 显示名称
     */
    public String getDisplayName(String locale) {
        if (label != null && label.containsKey(locale)) {
            return label.get(locale);
        }
        if (label != null && label.containsKey("en_US")) {
            return label.get("en_US");
        }
        return model;
    }
    
    /**
     * 获取默认显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return getDisplayName("zh_Hans");
    }
    
    /**
     * 检查是否支持指定特性
     * 
     * @param feature 特性名称
     * @return 是否支持
     */
    public boolean hasFeature(String feature) {
        if (features == null || feature == null) {
            return false;
        }
        
        for (String f : features) {
            if (feature.equals(f)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取模型完整标识符
     * 格式：provider:model
     * 
     * @return 完整标识符
     */
    public String getFullModelId() {
        if (provider != null && !provider.trim().isEmpty()) {
            return provider + ":" + model;
        }
        return model;
    }
    
    @Override
    public String toString() {
        return "ModelDefinition{" +
                "model='" + model + '\'' +
                ", modelType='" + modelType + '\'' +
                ", provider='" + provider + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
    
    /**
     * 从YAML数据创建ModelDefinition实例
     * 
     * @param yamlData YAML数据映射
     * @return ModelDefinition实例
     */
    @SuppressWarnings("unchecked")
    public static ModelDefinition fromYamlData(Map<String, Object> yamlData) {
        if (yamlData == null) {
            return null;
        }
        
        ModelDefinition definition = new ModelDefinition();
        
        // 解析model字段（可能是字符串或对象）
        Object modelData = yamlData.get("model");
        if (modelData instanceof String) {
            definition.setModel((String) modelData);
        } else if (modelData instanceof Map) {
            Map<String, Object> modelMap = (Map<String, Object>) modelData;
            definition.setModel(getString(modelMap, "code"));
        }
        
        // 直接从根级别解析其他字段
        if (definition.getModel() == null) {
            definition.setModel(getString(yamlData, "model"));
        }
        
        definition.setProvider(getString(yamlData, "provider"));
        definition.setModelType(getString(yamlData, "model_type"));
        
        // 解析label
        Object labelData = yamlData.get("label");
        if (labelData instanceof Map) {
            definition.setLabel((Map<String, String>) labelData);
        }
        
        // 解析features
        Object features = yamlData.get("features");
        if (features instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<String> featureList = (java.util.List<String>) features;
            definition.setFeatures(featureList.toArray(new String[0]));
        }
        
        // 解析model_properties
        Object modelProperties = yamlData.get("model_properties");
        if (modelProperties instanceof Map) {
            definition.setModelProperties((Map<String, Object>) modelProperties);
        }
        
        // 解析parameter_rules
        Object parameterRules = yamlData.get("parameter_rules");
        if (parameterRules instanceof java.util.List) {
            // 这里需要更复杂的转换逻辑，暂时跳过
            // TODO: 实现ParameterRule的YAML解析
        }
        
        // 解析pricing
        Object pricingData = yamlData.get("pricing");
        if (pricingData instanceof Map) {
            definition.setPricing((Map<String, Object>) pricingData);
        }
        
        return definition;
    }
    
    /**
     * 从Map中获取字符串值
     */
    private static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 参数规则内部类
     */
    public static class ParameterRule {
        private String name;
        private String useTemplate;
        private String type;
        private Object defaultValue;
        private Object min;
        private Object max;
        private Map<String, String> help;
        private String[] options;
        private Boolean required;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getUseTemplate() {
            return useTemplate;
        }
        
        public void setUseTemplate(String useTemplate) {
            this.useTemplate = useTemplate;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Object getDefaultValue() {
            return defaultValue;
        }
        
        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        public Object getMin() {
            return min;
        }
        
        public void setMin(Object min) {
            this.min = min;
        }
        
        public Object getMax() {
            return max;
        }
        
        public void setMax(Object max) {
            this.max = max;
        }
        
        public Map<String, String> getHelp() {
            return help;
        }
        
        public void setHelp(Map<String, String> help) {
            this.help = help;
        }
        
        public String[] getOptions() {
            return options;
        }
        
        public void setOptions(String[] options) {
            this.options = options;
        }
        
        public Boolean getRequired() {
            return required;
        }
        
        public void setRequired(Boolean required) {
            this.required = required;
        }
    }
}

