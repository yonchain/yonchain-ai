package com.yonchain.ai.plugin.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供商配置数据类
 */
public class ProviderConfig {
    private String provider;
    private String providerSource;
    private String background;
    private Map<String, String> label;
    private Map<String, String> description;
    private Map<String, String> iconSmall;
    private Map<String, String> iconLarge;
    private List<String> supportedModelTypes;
    private List<String> configurateMethods;
    private Map<String, Object> models;
    private Map<String, Object> providerCredentialSchema;
    private Map<String, Object> help;
    
    // getters and setters
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getProviderSource() { return providerSource; }
    public void setProviderSource(String providerSource) { this.providerSource = providerSource; }
    
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    
    public Map<String, String> getLabel() { return label; }
    public void setLabel(Map<String, String> label) { this.label = label; }
    
    public Map<String, String> getDescription() { return description; }
    public void setDescription(Map<String, String> description) { this.description = description; }
    
    public Map<String, String> getIconSmall() { return iconSmall; }
    public void setIconSmall(Map<String, String> iconSmall) { this.iconSmall = iconSmall; }
    
    public Map<String, String> getIconLarge() { return iconLarge; }
    public void setIconLarge(Map<String, String> iconLarge) { this.iconLarge = iconLarge; }
    
    public List<String> getSupportedModelTypes() { return supportedModelTypes; }
    public void setSupportedModelTypes(List<String> supportedModelTypes) { this.supportedModelTypes = supportedModelTypes; }
    
    public List<String> getConfigurateMethods() { return configurateMethods; }
    public void setConfigurateMethods(List<String> configurateMethods) { this.configurateMethods = configurateMethods; }
    
    public Map<String, Object> getModels() { return models; }
    public void setModels(Map<String, Object> models) { this.models = models; }
    
    public Map<String, Object> getProviderCredentialSchema() { return providerCredentialSchema; }
    public void setProviderCredentialSchema(Map<String, Object> providerCredentialSchema) { this.providerCredentialSchema = providerCredentialSchema; }
    
    public Map<String, Object> getHelp() { return help; }
    public void setHelp(Map<String, Object> help) { this.help = help; }
    
    /**
     * 获取选项处理器映射
     */
    public Map<String, String> getOptionsHandlers() {
        Map<String, String> handlers = new HashMap<>();
        
        if (models != null) {
            for (Map.Entry<String, Object> entry : models.entrySet()) {
                String modelType = entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> typeConfig = (Map<String, Object>) entry.getValue();
                
                String optionsHandler = (String) typeConfig.get("options_handler");
                if (optionsHandler != null && !optionsHandler.isEmpty()) {
                    handlers.put(modelType, optionsHandler);
                }
            }
        }
        
        return handlers;
    }
}
