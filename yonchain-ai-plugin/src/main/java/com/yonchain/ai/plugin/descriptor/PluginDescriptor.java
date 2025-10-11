package com.yonchain.ai.plugin.descriptor;

import com.yonchain.ai.plugin.config.ProviderConfig;
import com.yonchain.ai.plugin.config.ModelConfigData;
import com.yonchain.ai.plugin.config.PluginConfig;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件描述符
 * 用于描述插件的基本信息（从plugin.yaml解析）
 * 
 * @author yonchain
 */
public class PluginDescriptor {
    
    /**
     * 插件唯一标识符
     */
    private String id;
    
    /**
     * 插件名称
     */
    private String name;
    
    /**
     * 插件版本
     */
    private String version;
    
    /**
     * 插件作者
     */
    private String author;
    
    /**
     * 插件类型
     */
    private String type;
    
    /**
     * 创建时间
     */
    private String createdAt;
    
    /**
     * 描述信息（多语言支持）
     */
    private Map<String, String> description;
    
    /**
     * 图标文件名
     */
    private String icon;
    
    /**
     * 图标数据（从JAR中提取）
     */
    private byte[] iconData;
    
    /**
     * 标签信息（多语言支持）
     */
    private Map<String, String> label;
    
    /**
     * 提供商配置文件列表（如deepseek.yaml）
     */
    private List<String> plugins;
    
    /**
     * 资源配置（简化版）
     */
    private Map<String, Object> resource;
    
    /**
     * 插件路径（运行时设置）
     */
    private Path pluginPath;
    
    /**
     * 插件主类（新增字段）
     */
    private String pluginClass;
    
    /**
     * 提供商配置（整合配置信息）
     */
    private ProviderConfig providerConfig;
    
    /**
     * 模型配置列表
     */
    private List<ModelConfigData> modelConfigs;
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Map<String, String> getDescription() {
        return description;
    }
    
    public void setDescription(Map<String, String> description) {
        this.description = description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public byte[] getIconData() {
        return iconData;
    }
    
    public void setIconData(byte[] iconData) {
        this.iconData = iconData;
    }
    
    public Map<String, String> getLabel() {
        return label;
    }
    
    public void setLabel(Map<String, String> label) {
        this.label = label;
    }
    
    public List<String> getPlugins() {
        return plugins;
    }
    
    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
    
    public Map<String, Object> getResource() {
        return resource;
    }
    
    public void setResource(Map<String, Object> resource) {
        this.resource = resource;
    }
    
    public Path getPluginPath() {
        return pluginPath;
    }
    
    public void setPluginPath(Path pluginPath) {
        this.pluginPath = pluginPath;
    }
    
    public String getPluginClass() {
        return pluginClass;
    }
    
    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }
    
    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }
    
    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }
    
    public List<ModelConfigData> getModelConfigs() {
        return modelConfigs;
    }
    
    public void setModelConfigs(List<ModelConfigData> modelConfigs) {
        this.modelConfigs = modelConfigs;
    }
    
    /**
     * 获取本地化描述
     * 
     * @param locale 语言环境
     * @return 本地化描述
     */
    public String getLocalizedDescription(String locale) {
        if (description != null) {
            return description.get(locale);
        }
        return null;
    }
    
    /**
     * 获取本地化标签
     * 
     * @param locale 语言环境
     * @return 本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label != null) {
            return label.get(locale);
        }
        return null;
    }
    
    /**
     * 检查是否有提供商配置
     * 
     * @return 是否有提供商配置
     */
    public boolean hasProviders() {
        return plugins != null && !plugins.isEmpty();
    }
    
    /**
     * 获取配置文件的输入流
     * 
     * @param configFileName 配置文件名
     * @return 输入流
     */
    public InputStream getConfigInputStream(String configFileName) {
        try {
            if (pluginPath != null) {
                JarFile jarFile = new JarFile(pluginPath.toFile());
                JarEntry entry = jarFile.getJarEntry(configFileName);
                if (entry != null) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    // 注意：调用者需要负责关闭返回的InputStream，JarFile会在InputStream关闭时自动关闭
                    return inputStream;
                } else {
                    jarFile.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get config input stream for: " + configFileName, e);
        }
        throw new RuntimeException("Config file not found: " + configFileName);
    }
    
    /**
     * 获取模型配置文件列表
     * 
     * @return 模型配置文件列表
     */
    public List<String> getModelConfigFiles() {
        List<String> modelConfigFiles = new ArrayList<>();
        
        try {
            if (pluginPath != null) {
                JarFile jarFile = new JarFile(pluginPath.toFile());
                jarFile.stream()
                    .map(JarEntry::getName)
                    .filter(name -> name.startsWith("models/") && name.endsWith(".yaml"))
                    .filter(name -> !name.endsWith("_position.yaml")) // 排除位置配置文件
                    .forEach(modelConfigFiles::add);
                jarFile.close();
            }
        } catch (Exception e) {
            // 忽略错误，返回空列表
        }
        
        return modelConfigFiles;
    }
    
    // ==================== 便利方法 ====================
    
    /**
     * 获取提供商名称
     */
    public String getProvider() {
        return providerConfig != null ? providerConfig.getProvider() : null;
    }
    
    /**
     * 获取提供商源类
     */
    public String getProviderSource() {
        return providerConfig != null ? providerConfig.getProviderSource() : null;
    }
    
    /**
     * 获取背景色
     */
    public String getBackground() {
        return providerConfig != null ? providerConfig.getBackground() : null;
    }
    
    /**
     * 获取提供商标签
     */
    public Map<String, String> getProviderLabel() {
        return providerConfig != null ? providerConfig.getLabel() : null;
    }
    
    /**
     * 获取提供商描述
     */
    public Map<String, String> getProviderDescription() {
        return providerConfig != null ? providerConfig.getDescription() : null;
    }
    
    /**
     * 获取小图标
     */
    public Map<String, String> getIconSmall() {
        return providerConfig != null ? providerConfig.getIconSmall() : null;
    }
    
    /**
     * 获取大图标
     */
    public Map<String, String> getIconLarge() {
        return providerConfig != null ? providerConfig.getIconLarge() : null;
    }
    
    /**
     * 获取支持的模型类型
     */
    public List<String> getSupportedModelTypes() {
        return providerConfig != null ? providerConfig.getSupportedModelTypes() : null;
    }
    
    /**
     * 获取配置方法
     */
    public List<String> getConfigurateMethods() {
        return providerConfig != null ? providerConfig.getConfigurateMethods() : null;
    }
    
    /**
     * 获取选项处理器映射
     */
    public Map<String, String> getOptionsHandlers() {
        return providerConfig != null ? providerConfig.getOptionsHandlers() : new HashMap<>();
    }
    
    /**
     * 获取帮助信息
     */
    public Map<String, Object> getHelp() {
        return providerConfig != null ? providerConfig.getHelp() : null;
    }
    
    /**
     * 获取提供商凭证架构
     */
    public Map<String, Object> getProviderCredentialSchema() {
        return providerConfig != null ? providerConfig.getProviderCredentialSchema() : null;
    }
    
    /**
     * 从PluginConfig复制基础信息
     */
    public void copyFrom(PluginConfig pluginConfig) {
        if (pluginConfig != null) {
            this.id = pluginConfig.getId();
            this.name = pluginConfig.getName();
            this.version = pluginConfig.getVersion();
            this.author = pluginConfig.getAuthor();
            this.type = pluginConfig.getType();
      //      this.pluginClass = pluginConfig.getPluginClass();
            this.icon = pluginConfig.getIcon();
            this.description = pluginConfig.getDescription();
            this.label = pluginConfig.getLabel();
            this.plugins = pluginConfig.getPlugins();
            this.resource = pluginConfig.getResource();
        }
    }
    
    /**
     * 从配置创建描述符
     */
    public static PluginDescriptor from(PluginConfig pluginConfig, ProviderConfig providerConfig) {
        PluginDescriptor descriptor = new PluginDescriptor();
        descriptor.copyFrom(pluginConfig);
        descriptor.setProviderConfig(providerConfig);
        return descriptor;
    }
    
    /**
     * 从配置创建描述符（包含模型配置）
     */
    public static PluginDescriptor from(PluginConfig pluginConfig, ProviderConfig providerConfig, List<ModelConfigData> modelConfigs) {
        PluginDescriptor descriptor = from(pluginConfig, providerConfig);
        descriptor.setModelConfigs(modelConfigs);
        return descriptor;
    }
    
    @Override
    public String toString() {
        return "PluginDescriptor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", provider='" + getProvider() + '\'' +
                ", pluginClass='" + pluginClass + '\'' +
                '}';
    }
}