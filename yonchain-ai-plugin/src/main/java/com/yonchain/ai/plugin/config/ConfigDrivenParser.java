package com.yonchain.ai.plugin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 配置驱动的解析器
 */
@Component
public class ConfigDrivenParser {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigDrivenParser.class);
    
    /**
     * 解析插件配置
     */
    public PluginConfig parsePluginConfig(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(inputStream);
            
            PluginConfig config = new PluginConfig();
            config.setId((String) data.get("id"));
            config.setName((String) data.get("name"));
            config.setVersion((String) data.get("version"));
            config.setAuthor((String) data.get("author"));
            config.setType((String) data.get("type"));
            config.setPluginClass((String) data.get("plugin_class"));
            config.setIcon((String) data.get("icon"));
            
            // 解析描述信息
            @SuppressWarnings("unchecked")
            Map<String, String> descriptions = (Map<String, String>) data.get("description");
            if (descriptions != null) {
                config.setDescription(descriptions);
            }
            
            // 解析标签信息
            @SuppressWarnings("unchecked")
            Map<String, String> labels = (Map<String, String>) data.get("label");
            if (labels != null) {
                config.setLabel(labels);
            }
            
            // 解析插件列表
            @SuppressWarnings("unchecked")
            List<String> plugins = (List<String>) data.get("plugins");
            if (plugins != null) {
                config.setPlugins(plugins);
            }
            
            return config;
            
        } catch (Exception e) {
            log.error("Failed to parse plugin config", e);
            throw new RuntimeException("Failed to parse plugin config", e);
        }
    }
    
    /**
     * 解析提供商配置
     */
    public ProviderConfig parseProviderConfig(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(inputStream);
            
            ProviderConfig config = new ProviderConfig();
            config.setProvider((String) data.get("provider"));
            config.setProviderSource((String) data.get("provider_source"));
            config.setBackground((String) data.get("background"));
            
            // 解析标签
            @SuppressWarnings("unchecked")
            Map<String, String> labels = (Map<String, String>) data.get("label");
            if (labels != null) {
                config.setLabel(labels);
            }
            
            // 解析描述
            @SuppressWarnings("unchecked")
            Map<String, String> descriptions = (Map<String, String>) data.get("description");
            if (descriptions != null) {
                config.setDescription(descriptions);
            }
            
            // 解析图标
            @SuppressWarnings("unchecked")
            Map<String, String> iconSmall = (Map<String, String>) data.get("icon_small");
            if (iconSmall != null) {
                config.setIconSmall(iconSmall);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, String> iconLarge = (Map<String, String>) data.get("icon_large");
            if (iconLarge != null) {
                config.setIconLarge(iconLarge);
            }
            
            // 解析支持的模型类型
            @SuppressWarnings("unchecked")
            List<String> supportedModelTypes = (List<String>) data.get("supported_model_types");
            if (supportedModelTypes != null) {
                config.setSupportedModelTypes(supportedModelTypes);
            }
            
            // 解析配置方法
            @SuppressWarnings("unchecked")
            List<String> configurateMethods = (List<String>) data.get("configurate_methods");
            if (configurateMethods != null) {
                config.setConfigurateMethods(configurateMethods);
            }
            
            // 解析模型配置
            @SuppressWarnings("unchecked")
            Map<String, Object> models = (Map<String, Object>) data.get("models");
            if (models != null) {
                config.setModels(models);
            }
            
            // 解析凭证配置Schema
            @SuppressWarnings("unchecked")
            Map<String, Object> credentialSchema = (Map<String, Object>) data.get("provider_credential_schema");
            if (credentialSchema != null) {
                config.setProviderCredentialSchema(credentialSchema);
            }
            
            // 解析帮助信息
            @SuppressWarnings("unchecked")
            Map<String, Object> help = (Map<String, Object>) data.get("help");
            if (help != null) {
                config.setHelp(help);
            }
            
            return config;
            
        } catch (Exception e) {
            log.error("Failed to parse provider config", e);
            throw new RuntimeException("Failed to parse provider config", e);
        }
    }
    
    /**
     * 解析模型配置
     */
    public ModelConfigData parseModelConfig(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(inputStream);
            
            ModelConfigData config = new ModelConfigData();
            config.setModel((String) data.get("model"));
            config.setModelType((String) data.get("model_type"));
            
            // 解析标签
            @SuppressWarnings("unchecked")
            Map<String, String> labels = (Map<String, String>) data.get("label");
            if (labels != null) {
                config.setLabel(labels);
            }
            
            // 解析特性
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) data.get("features");
            if (features != null) {
                config.setFeatures(features);
            }
            
            // 解析模型属性
            @SuppressWarnings("unchecked")
            Map<String, Object> modelProperties = (Map<String, Object>) data.get("model_properties");
            if (modelProperties != null) {
                config.setModelProperties(modelProperties);
            }
            
            // 解析参数规则
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parameterRules = (List<Map<String, Object>>) data.get("parameter_rules");
            if (parameterRules != null) {
                config.setParameterRules(parameterRules);
            }
            
            // 解析定价信息
            @SuppressWarnings("unchecked")
            Map<String, Object> pricing = (Map<String, Object>) data.get("pricing");
            if (pricing != null) {
                config.setPricing(pricing);
            }
            
            return config;
            
        } catch (Exception e) {
            log.error("Failed to parse model config", e);
            throw new RuntimeException("Failed to parse model config", e);
        }
    }
}
