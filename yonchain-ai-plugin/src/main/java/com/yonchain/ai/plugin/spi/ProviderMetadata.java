package com.yonchain.ai.plugin.spi;

import java.util.List;
import java.util.Map;

/**
 * 提供商元数据接口
 * 定义模型提供商的元数据信息
 */
public class ProviderMetadata {
    
    private String provider;
    private String background;
    private Map<String, String> label;
    private Map<String, String> description;
    private Map<String, String> iconSmall;
    private Map<String, String> iconLarge;
    private List<String> supportedModelTypes;
    private List<String> configurateMethods;
    private Map<String, Object> help;
    private Map<String, Object> providerCredentialSchema;
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getBackground() {
        return background;
    }
    
    public void setBackground(String background) {
        this.background = background;
    }
    
    public Map<String, String> getLabel() {
        return label;
    }
    
    public void setLabel(Map<String, String> label) {
        this.label = label;
    }
    
    public Map<String, String> getDescription() {
        return description;
    }
    
    public void setDescription(Map<String, String> description) {
        this.description = description;
    }
    
    public Map<String, String> getIconSmall() {
        return iconSmall;
    }
    
    public void setIconSmall(Map<String, String> iconSmall) {
        this.iconSmall = iconSmall;
    }
    
    public Map<String, String> getIconLarge() {
        return iconLarge;
    }
    
    public void setIconLarge(Map<String, String> iconLarge) {
        this.iconLarge = iconLarge;
    }
    
    public List<String> getSupportedModelTypes() {
        return supportedModelTypes;
    }
    
    public void setSupportedModelTypes(List<String> supportedModelTypes) {
        this.supportedModelTypes = supportedModelTypes;
    }
    
    public List<String> getConfigurateMethods() {
        return configurateMethods;
    }
    
    public void setConfigurateMethods(List<String> configurateMethods) {
        this.configurateMethods = configurateMethods;
    }
    
    public Map<String, Object> getHelp() {
        return help;
    }
    
    public void setHelp(Map<String, Object> help) {
        this.help = help;
    }
    
    public Map<String, Object> getProviderCredentialSchema() {
        return providerCredentialSchema;
    }
    
    public void setProviderCredentialSchema(Map<String, Object> providerCredentialSchema) {
        this.providerCredentialSchema = providerCredentialSchema;
    }
    
    /**
     * 获取本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label == null) return provider;
        return label.getOrDefault(locale, label.getOrDefault("en_US", provider));
    }
    
    /**
     * 获取本地化描述
     */
    public String getLocalizedDescription(String locale) {
        if (description == null) return null;
        return description.getOrDefault(locale, description.get("en_US"));
    }
}
