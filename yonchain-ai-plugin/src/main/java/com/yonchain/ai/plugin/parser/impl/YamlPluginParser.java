package com.yonchain.ai.plugin.parser.impl;

import com.yonchain.ai.plugin.descriptor.Dependency;
import com.yonchain.ai.plugin.descriptor.Extension;
import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.descriptor.SpiConfiguration;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.parser.ModelDefinition;
import com.yonchain.ai.plugin.parser.PluginParseException;
import com.yonchain.ai.plugin.parser.PluginParser;
import com.yonchain.ai.plugin.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * YAML格式插件解析器实现
 * 
 * @author yonchain
 */
@Component
public class YamlPluginParser implements PluginParser {
    
    private static final Logger log = LoggerFactory.getLogger(YamlPluginParser.class);
    
    private static final String PLUGIN_YAML = "plugin.yaml";
    private static final String PLUGIN_XML = "plugin.xml"; // 兼容XML格式
    
    private final Yaml yaml = new Yaml();
    
    @Override
    public PluginDescriptor parsePlugin(Path pluginDir) throws PluginParseException {
        if (!isValidPluginDirectory(pluginDir)) {
            throw new PluginParseException("Invalid plugin directory: " + pluginDir);
        }
        
        try {
            // 优先尝试解析plugin.yaml
            Path yamlFile = pluginDir.resolve(PLUGIN_YAML);
            if (Files.exists(yamlFile)) {
                return parsePluginYaml(yamlFile, pluginDir);
            }
            
            // 如果没有YAML文件，尝试XML文件（兼容性）
            Path xmlFile = pluginDir.resolve(PLUGIN_XML);
            if (Files.exists(xmlFile)) {
                log.warn("Using deprecated plugin.xml format for plugin: {}", pluginDir);
                // TODO: 可以添加XML解析支持
                throw new PluginParseException("XML plugin format not yet supported, please use plugin.yaml");
            }
            
            throw new PluginParseException("No plugin descriptor found (plugin.yaml or plugin.xml) in: " + pluginDir);
            
        } catch (IOException e) {
            throw new PluginParseException("Failed to parse plugin in directory: " + pluginDir, e);
        }
    }
    
    @Override
    public ValidationResult validatePlugin(PluginDescriptor descriptor) {
        ValidationResult result = new ValidationResult();
        
        // 验证基本信息
        if (descriptor.getId() == null || descriptor.getId().trim().isEmpty()) {
            result.addError("Plugin ID cannot be null or empty");
        }
        
        if (descriptor.getName() == null || descriptor.getName().trim().isEmpty()) {
            result.addError("Plugin name cannot be null or empty");
        }
        
        if (descriptor.getVersion() == null || descriptor.getVersion().trim().isEmpty()) {
            result.addError("Plugin version cannot be null or empty");
        }
        
        if (descriptor.getType() == null) {
            result.addError("Plugin type cannot be null");
        }
        
        // 验证SPI配置（对于MODEL类型插件）
        if (descriptor.getType() == PluginType.MODEL) {
            if (descriptor.getSpi() == null) {
                result.addError("Model plugin must have SPI configuration");
            } else {
                if (descriptor.getSpi().getProviderSource() == null || 
                    descriptor.getSpi().getProviderSource().trim().isEmpty()) {
                    result.addError("Model plugin must specify provider source class");
                }
            }
        }
        
        // 验证依赖关系
        if (descriptor.getDependencies() != null) {
            for (Dependency dependency : descriptor.getDependencies()) {
                if (dependency.getId() == null || dependency.getId().trim().isEmpty()) {
                    result.addError("Dependency ID cannot be null or empty");
                }
                if (dependency.getMinVersion() == null || dependency.getMinVersion().trim().isEmpty()) {
                    result.addWarning("Dependency " + dependency.getId() + " has no minimum version specified");
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ModelDefinition> parseModelDefinitions(Path modelsDir) throws PluginParseException {
        List<ModelDefinition> modelDefinitions = new ArrayList<>();
        
        if (!Files.exists(modelsDir) || !Files.isDirectory(modelsDir)) {
            log.warn("Models directory does not exist or is not a directory: {}", modelsDir);
            return modelDefinitions;
        }
        
        try (Stream<Path> paths = Files.walk(modelsDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".yaml") || path.toString().endsWith(".yml"))
                 .forEach(yamlFile -> {
                     try {
                         ModelDefinition modelDef = parseModelDefinition(yamlFile);
                         if (modelDef != null) {
                             modelDefinitions.add(modelDef);
                         }
                     } catch (Exception e) {
                         log.error("Failed to parse model definition from: {}", yamlFile, e);
                     }
                 });
        } catch (IOException e) {
            throw new PluginParseException("Failed to scan models directory: " + modelsDir, e);
        }
        
        return modelDefinitions;
    }
    
    @Override
    public boolean isValidPluginDirectory(Path pluginDir) {
        if (!Files.exists(pluginDir) || !Files.isDirectory(pluginDir)) {
            return false;
        }
        
        // 检查是否存在plugin.yaml或plugin.xml
        return Files.exists(pluginDir.resolve(PLUGIN_YAML)) || 
               Files.exists(pluginDir.resolve(PLUGIN_XML));
    }
    
    /**
     * 解析plugin.yaml文件
     * 
     * @param yamlFile YAML文件路径
     * @param pluginDir 插件目录
     * @return 插件描述符
     * @throws IOException IO异常
     * @throws PluginParseException 解析异常
     */
    private PluginDescriptor parsePluginYaml(Path yamlFile, Path pluginDir) throws IOException, PluginParseException {
        try (InputStream inputStream = Files.newInputStream(yamlFile)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlData = yaml.load(inputStream);
            
            if (yamlData == null) {
                throw new PluginParseException("Empty or invalid YAML file: " + yamlFile);
            }
            
            return buildPluginDescriptor(yamlData, pluginDir);
            
        } catch (Exception e) {
            throw new PluginParseException("Failed to parse plugin YAML: " + yamlFile, e);
        }
    }
    
    /**
     * 构建插件描述符
     * 
     * @param yamlData YAML数据
     * @param pluginDir 插件目录
     * @return 插件描述符
     * @throws PluginParseException 解析异常
     */
    @SuppressWarnings("unchecked")
    private PluginDescriptor buildPluginDescriptor(Map<String, Object> yamlData, Path pluginDir) throws PluginParseException {
        try {
            PluginDescriptor descriptor = new PluginDescriptor();
            
            // 基本信息
            descriptor.setId(getString(yamlData, "id"));
            descriptor.setName(getString(yamlData, "name"));
            descriptor.setVersion(getString(yamlData, "version"));
            descriptor.setAuthor(getString(yamlData, "author"));
            descriptor.setHomepage(getString(yamlData, "homepage"));
            descriptor.setPluginPath(pluginDir);
            
            // 描述信息（支持多语言）
            Object descObj = yamlData.get("description");
            if (descObj instanceof String) {
                descriptor.setDescription((String) descObj);
            } else if (descObj instanceof Map) {
                Map<String, String> descMap = (Map<String, String>) descObj;
                // 优先使用中文描述，其次英文，最后任意语言
                String description = descMap.getOrDefault("zh_Hans", 
                    descMap.getOrDefault("en_US", descMap.values().iterator().next()));
                descriptor.setDescription(description);
            }
            
            // 插件类型
            String typeStr = getString(yamlData, "type");
            if (typeStr != null) {
                try {
                    descriptor.setType(PluginType.fromCode(typeStr));
                } catch (IllegalArgumentException e) {
                    throw new PluginParseException("Invalid plugin type: " + typeStr);
                }
            } else {
                descriptor.setType(PluginType.MODEL); // 默认为模型插件
            }
            
            // SPI配置
            Map<String, Object> spiData = (Map<String, Object>) yamlData.get("spi");
            if (spiData != null) {
                SpiConfiguration spi = new SpiConfiguration();
                spi.setProviderSource(getString(spiData, "provider_source"));
                spi.setProviderInterface(getString(spiData, "provider_interface"));
                
                List<String> services = getStringList(spiData, "services");
                if (services != null) {
                    spi.setServices(services);
                }
                
                descriptor.setSpi(spi);
            }
            
            // 依赖关系
            List<Map<String, Object>> dependenciesData = (List<Map<String, Object>>) yamlData.get("dependencies");
            if (dependenciesData != null) {
                List<Dependency> dependencies = new ArrayList<>();
                for (Map<String, Object> depData : dependenciesData) {
                    Dependency dependency = new Dependency();
                    dependency.setId(getString(depData, "id"));
                    dependency.setMinVersion(getString(depData, "min_version"));
                    dependency.setMaxVersion(getString(depData, "max_version"));
                    dependency.setOptional(getBoolean(depData, "optional", false));
                    dependencies.add(dependency);
                }
                descriptor.setDependencies(dependencies);
            }
            
            // 扩展点
            List<Map<String, Object>> extensionsData = (List<Map<String, Object>>) yamlData.get("extensions");
            if (extensionsData != null) {
                List<Extension> extensions = new ArrayList<>();
                for (Map<String, Object> extData : extensionsData) {
                    Extension extension = new Extension();
                    extension.setPoint(getString(extData, "point"));
                    extension.setImplementation(getString(extData, "implementation"));
                    extensions.add(extension);
                }
                descriptor.setExtensions(extensions);
            }
            
            return descriptor;
            
        } catch (Exception e) {
            throw new PluginParseException("Failed to build plugin descriptor", e);
        }
    }
    
    /**
     * 解析单个模型定义文件
     * 
     * @param yamlFile 模型YAML文件
     * @return 模型定义
     */
    private ModelDefinition parseModelDefinition(Path yamlFile) {
        try (InputStream inputStream = Files.newInputStream(yamlFile)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlData = yaml.load(inputStream);
            
            if (yamlData == null) {
                log.warn("Empty model definition file: {}", yamlFile);
                return null;
            }
            
            return ModelDefinition.fromYamlData(yamlData);
            
        } catch (Exception e) {
            log.error("Failed to parse model definition from: {}", yamlFile, e);
            return null;
        }
    }
    
    /**
     * 从YAML数据中获取字符串值
     * 
     * @param data YAML数据
     * @param key 键
     * @return 字符串值
     */
    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 从YAML数据中获取字符串列表
     * 
     * @param data YAML数据
     * @param key 键
     * @return 字符串列表
     */
    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return null;
    }
    
    /**
     * 从YAML数据中获取布尔值
     * 
     * @param data YAML数据
     * @param key 键
     * @param defaultValue 默认值
     * @return 布尔值
     */
    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
}
