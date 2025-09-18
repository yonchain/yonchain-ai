package com.yonchain.ai.plugin.descriptor;

import com.yonchain.ai.plugin.enums.PluginType;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * 插件描述符
 * 包含从plugin.yaml解析出的插件基本信息
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
     * 插件描述
     */
    private String description;
    
    /**
     * 插件作者
     */
    private String author;
    
    /**
     * 插件供应商
     */
    private String vendor;
    
    /**
     * 插件主页
     */
    private String homepage;
    
    /**
     * 插件类型
     */
    private PluginType type;
    
    /**
     * 依赖的其他插件文件列表
     */
    private List<String> plugins;
    
    /**
     * 依赖关系
     */
    private List<Dependency> dependencies;
    
    /**
     * 扩展点配置
     */
    private List<Extension> extensions;
    
    /**
     * SPI配置
     */
    private SpiConfiguration spi;
    
    /**
     * 插件路径
     */
    private Path pluginPath;
    
    /**
     * 其他元数据
     */
    private Map<String, Object> metadata;
    
    public PluginDescriptor() {
    }
    
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public String getHomepage() {
        return homepage;
    }
    
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
    
    public PluginType getType() {
        return type;
    }
    
    public void setType(PluginType type) {
        this.type = type;
    }
    
    public List<String> getPlugins() {
        return plugins;
    }
    
    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
    
    public List<Dependency> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    public List<Extension> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }
    
    public SpiConfiguration getSpi() {
        return spi;
    }
    
    public void setSpi(SpiConfiguration spi) {
        this.spi = spi;
    }
    
    public Path getPluginPath() {
        return pluginPath;
    }
    
    public void setPluginPath(Path pluginPath) {
        this.pluginPath = pluginPath;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * 检查插件是否有依赖
     * 
     * @return 是否有依赖
     */
    public boolean hasDependencies() {
        return dependencies != null && !dependencies.isEmpty();
    }
    
    /**
     * 检查插件是否有扩展点
     * 
     * @return 是否有扩展点
     */
    public boolean hasExtensions() {
        return extensions != null && !extensions.isEmpty();
    }
    
    /**
     * 检查插件是否有SPI配置
     * 
     * @return 是否有SPI配置
     */
    public boolean hasSpiConfiguration() {
        return spi != null;
    }
    
    /**
     * 获取插件的完整标识符
     * 格式：{vendor}:{name}:{version}
     * 
     * @return 完整标识符
     */
    public String getFullId() {
        StringBuilder sb = new StringBuilder();
        if (vendor != null && !vendor.trim().isEmpty()) {
            sb.append(vendor).append(":");
        }
        sb.append(name);
        if (version != null && !version.trim().isEmpty()) {
            sb.append(":").append(version);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "PluginDescriptor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type=" + type +
                ", author='" + author + '\'' +
                ", vendor='" + vendor + '\'' +
                ", pluginPath=" + pluginPath +
                '}';
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final PluginDescriptor descriptor = new PluginDescriptor();
        
        public Builder id(String id) {
            descriptor.setId(id);
            return this;
        }
        
        public Builder name(String name) {
            descriptor.setName(name);
            return this;
        }
        
        public Builder version(String version) {
            descriptor.setVersion(version);
            return this;
        }
        
        public Builder description(String description) {
            descriptor.setDescription(description);
            return this;
        }
        
        public Builder author(String author) {
            descriptor.setAuthor(author);
            return this;
        }
        
        public Builder vendor(String vendor) {
            descriptor.setVendor(vendor);
            return this;
        }
        
        public Builder type(PluginType type) {
            descriptor.setType(type);
            return this;
        }
        
        public Builder plugins(List<String> plugins) {
            descriptor.setPlugins(plugins);
            return this;
        }
        
        public Builder dependencies(List<Dependency> dependencies) {
            descriptor.setDependencies(dependencies);
            return this;
        }
        
        public Builder extensions(List<Extension> extensions) {
            descriptor.setExtensions(extensions);
            return this;
        }
        
        public Builder spi(SpiConfiguration spi) {
            descriptor.setSpi(spi);
            return this;
        }
        
        public Builder pluginPath(Path pluginPath) {
            descriptor.setPluginPath(pluginPath);
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            descriptor.setMetadata(metadata);
            return this;
        }
        
        public PluginDescriptor build() {
            return descriptor;
        }
    }
}

