package com.yonchain.ai.plugin.spi;

import java.util.List;
import java.util.Map;

/**
 * 提供商元数据
 * 包含从插件配置文件中读取的信息
 * 
 * @author yonchain
 */
public class ProviderMetadata {
    
    private String provider;
    private String providerSource;
    private String background;
    private Map<String, String> label;
    private Map<String, String> description;
    private Map<String, String> iconLarge;
    private Map<String, String> iconSmall;
    private List<String> supportedModelTypes;
    private List<String> configurateMethods;
    private Map<String, Object> help;
    private Map<String, Object> providerCredentialSchema;
    
    public ProviderMetadata() {}
    
    public ProviderMetadata(String provider) {
        this.provider = provider;
    }
    
    // Getters and Setters
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getProviderSource() {
        return providerSource;
    }
    
    public void setProviderSource(String providerSource) {
        this.providerSource = providerSource;
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
    
    public Map<String, String> getIconLarge() {
        return iconLarge;
    }
    
    public void setIconLarge(Map<String, String> iconLarge) {
        this.iconLarge = iconLarge;
    }
    
    public Map<String, String> getIconSmall() {
        return iconSmall;
    }
    
    public void setIconSmall(Map<String, String> iconSmall) {
        this.iconSmall = iconSmall;
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
     * 
     * @param locale 语言环境，如 "en_US", "zh_Hans"
     * @return 本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label == null) return provider;
        return label.getOrDefault(locale, label.getOrDefault("en_US", provider));
    }
    
    /**
     * 获取本地化描述
     * 
     * @param locale 语言环境
     * @return 本地化描述
     */
    public String getLocalizedDescription(String locale) {
        if (description == null) return null;
        return description.getOrDefault(locale, description.get("en_US"));
    }
    
    /**
     * 获取本地化图标
     * 
     * @param locale 语言环境
     * @param large 是否获取大图标
     * @return 图标路径
     */
    public String getLocalizedIcon(String locale, boolean large) {
        Map<String, String> iconMap = large ? iconLarge : iconSmall;
        if (iconMap == null) return null;
        return iconMap.getOrDefault(locale, iconMap.get("en_US"));
    }
}



