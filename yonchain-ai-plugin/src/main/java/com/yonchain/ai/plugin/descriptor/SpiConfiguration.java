package com.yonchain.ai.plugin.descriptor;

import java.util.List;
import java.util.Map;

/**
 * SPI配置信息
 * 描述插件的SPI实现
 * 
 * @author yonchain
 */
public class SpiConfiguration {
    
    /**
     * 模型源列表
     * 包含插件提供的模型实现类
     */
    private List<String> modelSources;
    
    /**
     * 提供商源
     * 插件的主要提供商实现类
     */
    private String providerSource;
    
    /**
     * 提供商接口
     * 插件实现的接口类
     */
    private String providerInterface;
    
    /**
     * 服务列表
     * 插件提供的服务类列表
     */
    private List<String> services;
    
    /**
     * 工具源列表
     * 包含插件提供的工具实现类
     */
    private List<String> toolSources;
    
    /**
     * UI源列表
     * 包含插件提供的UI组件实现类
     */
    private List<String> uiSources;
    
    /**
     * 额外配置
     * 其他类型的SPI配置
     */
    private Map<String, Object> additionalConfig;
    
    public SpiConfiguration() {
    }
    
    public List<String> getModelSources() {
        return modelSources;
    }
    
    public void setModelSources(List<String> modelSources) {
        this.modelSources = modelSources;
    }
    
    public String getProviderSource() {
        return providerSource;
    }
    
    public void setProviderSource(String providerSource) {
        this.providerSource = providerSource;
    }
    
    public String getProviderInterface() {
        return providerInterface;
    }
    
    public void setProviderInterface(String providerInterface) {
        this.providerInterface = providerInterface;
    }
    
    public List<String> getServices() {
        return services;
    }
    
    public void setServices(List<String> services) {
        this.services = services;
    }
    
    public List<String> getToolSources() {
        return toolSources;
    }
    
    public void setToolSources(List<String> toolSources) {
        this.toolSources = toolSources;
    }
    
    public List<String> getUiSources() {
        return uiSources;
    }
    
    public void setUiSources(List<String> uiSources) {
        this.uiSources = uiSources;
    }
    
    public Map<String, Object> getAdditionalConfig() {
        return additionalConfig;
    }
    
    public void setAdditionalConfig(Map<String, Object> additionalConfig) {
        this.additionalConfig = additionalConfig;
    }
    
    /**
     * 检查是否有模型源配置
     * 
     * @return 是否有模型源
     */
    public boolean hasModelSources() {
        return modelSources != null && !modelSources.isEmpty();
    }
    
    /**
     * 检查是否有提供商源配置
     * 
     * @return 是否有提供商源
     */
    public boolean hasProviderSource() {
        return providerSource != null && !providerSource.trim().isEmpty();
    }
    
    /**
     * 检查是否有工具源配置
     * 
     * @return 是否有工具源
     */
    public boolean hasToolSources() {
        return toolSources != null && !toolSources.isEmpty();
    }
    
    /**
     * 检查是否有UI源配置
     * 
     * @return 是否有UI源
     */
    public boolean hasUiSources() {
        return uiSources != null && !uiSources.isEmpty();
    }
    
    @Override
    public String toString() {
        return "SpiConfiguration{" +
                "modelSources=" + modelSources +
                ", providerSource='" + providerSource + '\'' +
                ", toolSources=" + toolSources +
                ", uiSources=" + uiSources +
                ", additionalConfig=" + additionalConfig +
                '}';
    }
}

