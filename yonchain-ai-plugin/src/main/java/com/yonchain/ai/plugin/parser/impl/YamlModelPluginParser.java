package com.yonchain.ai.plugin.parser.impl;

import com.yonchain.ai.plugin.descriptor.ProviderDescriptor;
import com.yonchain.ai.plugin.parser.ModelDefinition;
import com.yonchain.ai.plugin.parser.ModelPluginParser;
import com.yonchain.ai.plugin.exception.PluginParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * YAML格式模型插件解析器实现
 * 专门处理模型插件的提供商和模型定义解析
 *
 * @author yonchain
 */
@Component
public class YamlModelPluginParser implements ModelPluginParser {

    private static final Logger log = LoggerFactory.getLogger(YamlModelPluginParser.class);
    private final Yaml yaml = new Yaml();

    @Override
    public ProviderDescriptor parseProvider(String providerYamlPath, Path jarPath) throws PluginParseException {
        try {
            Map<String, Object> providerYamlData = loadYamlFromJar(providerYamlPath, jarPath);
            if (providerYamlData == null) {
                throw new PluginParseException("Provider YAML file not found or empty: " + providerYamlPath);
            }
            return buildProviderDescriptor(providerYamlData);
        } catch (IOException e) {
            throw new PluginParseException("Failed to parse provider YAML: " + providerYamlPath, e);
        }
    }

    @Override
    public List<ModelDefinition> parseModelDefinitions(ProviderDescriptor providerDescriptor, Path jarPath) throws PluginParseException {
        List<ModelDefinition> modelDefinitions = new ArrayList<>();
        try {
            Map<String, ProviderDescriptor.ModelTypeConfig> modelsConfig = providerDescriptor.getModels();
            if (modelsConfig != null) {
                for (Map.Entry<String, ProviderDescriptor.ModelTypeConfig> entry : modelsConfig.entrySet()) {
                    String modelType = entry.getKey();
                    ProviderDescriptor.ModelTypeConfig typeConfig = entry.getValue();
                    if (typeConfig != null && typeConfig.getPredefined() != null) {
                        for (String pathPattern : typeConfig.getPredefined()) {
                            List<ModelDefinition> typeModels = parseModelsFromPathPattern(pathPattern, modelType, jarPath);
                            modelDefinitions.addAll(typeModels);
                        }
                    }
                }
            }
            return modelDefinitions;
        } catch (Exception e) {
            throw new PluginParseException("Failed to parse model definitions", e);
        }
    }

    @Override
    public boolean validateProvider(ProviderDescriptor providerDescriptor) {
        if (providerDescriptor == null) {
            return false;
        }
        if (providerDescriptor.getProvider() == null || providerDescriptor.getProvider().trim().isEmpty()) {
            log.warn("Provider name is missing or empty");
            return false;
        }
        if (providerDescriptor.getProviderSource() == null || providerDescriptor.getProviderSource().trim().isEmpty()) {
            log.warn("Provider source class is missing or empty");
            return false;
        }
        if (providerDescriptor.getSupportedModelTypes() == null || providerDescriptor.getSupportedModelTypes().isEmpty()) {
            log.warn("No supported model types specified");
            return false;
        }
        if (providerDescriptor.getModels() == null || providerDescriptor.getModels().isEmpty()) {
            log.warn("No model configurations found");
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadYamlFromJar(String yamlPath, Path jarPath) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry entry = jarFile.getJarEntry(yamlPath);
            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    return yaml.load(inputStream);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ProviderDescriptor buildProviderDescriptor(Map<String, Object> yamlData) {
        ProviderDescriptor descriptor = new ProviderDescriptor();

        // 基本信息
        descriptor.setBackground(getString(yamlData, "background"));
        descriptor.setProvider(getString(yamlData, "provider"));
        descriptor.setProviderSource(getString(yamlData, "provider_source"));

        // 配置方法
        List<String> configurateMethods = getStringList(yamlData, "configurate_methods");
        if (configurateMethods != null) {
            descriptor.setConfigurateMethods(configurateMethods);
        }

        // 描述信息（多语言支持）
        Object descObj = yamlData.get("description");
        if (descObj instanceof Map) {
            descriptor.setDescription((Map<String, String>) descObj);
        }

        // 标签信息（多语言支持）
        Object labelObj = yamlData.get("label");
        if (labelObj instanceof Map) {
            descriptor.setLabel((Map<String, String>) labelObj);
        }

        // 帮助信息
        Map<String, Object> helpData = (Map<String, Object>) yamlData.get("help");
        if (helpData != null) {
            ProviderDescriptor.HelpConfig helpConfig = new ProviderDescriptor.HelpConfig();
            Object titleObj = helpData.get("title");
            if (titleObj instanceof Map) {
                helpConfig.setTitle((Map<String, String>) titleObj);
            }
            Object urlObj = helpData.get("url");
            if (urlObj instanceof Map) {
                helpConfig.setUrl((Map<String, String>) urlObj);
            }
            descriptor.setHelp(helpConfig);
        }

        // 图标配置
        Object iconLargeObj = yamlData.get("icon_large");
        if (iconLargeObj instanceof Map) {
            descriptor.setIconLarge((Map<String, String>) iconLargeObj);
        }
        Object iconSmallObj = yamlData.get("icon_small");
        if (iconSmallObj instanceof Map) {
            descriptor.setIconSmall((Map<String, String>) iconSmallObj);
        }

        // 模型配置 - 支持新的source字段
        Map<String, Object> modelsData = (Map<String, Object>) yamlData.get("models");
        if (modelsData != null) {
            Map<String, ProviderDescriptor.ModelTypeConfig> modelsConfig = new java.util.HashMap<>();
            for (Map.Entry<String, Object> entry : modelsData.entrySet()) {
                String modelType = entry.getKey();
                Map<String, Object> typeConfigData = (Map<String, Object>) entry.getValue();
                if (typeConfigData != null) {
                    ProviderDescriptor.ModelTypeConfig typeConfig = new ProviderDescriptor.ModelTypeConfig();
                    typeConfig.setSource(getString(typeConfigData, "source")); // 新增：模型处理器类
                    typeConfig.setPosition(getString(typeConfigData, "position"));
                    List<String> predefined = getStringList(typeConfigData, "predefined");
                    if (predefined != null) {
                        typeConfig.setPredefined(predefined);
                    }
                    modelsConfig.put(modelType, typeConfig);
                }
            }
            descriptor.setModels(modelsConfig);
        }

        // 提供商凭证架构
        Map<String, Object> credentialSchemaData = (Map<String, Object>) yamlData.get("provider_credential_schema");
        if (credentialSchemaData != null) {
            ProviderDescriptor.ProviderCredentialSchema credentialSchema = new ProviderDescriptor.ProviderCredentialSchema();
            List<Map<String, Object>> formSchemasData = (List<Map<String, Object>>) credentialSchemaData.get("credential_form_schemas");
            if (formSchemasData != null) {
                List<ProviderDescriptor.CredentialFormSchema> formSchemas = new ArrayList<>();
                for (Map<String, Object> formSchemaData : formSchemasData) {
                    ProviderDescriptor.CredentialFormSchema formSchema = new ProviderDescriptor.CredentialFormSchema();
                    Object labelObj2 = formSchemaData.get("label");
                    if (labelObj2 instanceof Map) {
                        formSchema.setLabel((Map<String, String>) labelObj2);
                    }
                    Object placeholderObj = formSchemaData.get("placeholder");
                    if (placeholderObj instanceof Map) {
                        formSchema.setPlaceholder((Map<String, String>) placeholderObj);
                    }
                    formSchema.setRequired(getBoolean(formSchemaData, "required", false));
                    formSchema.setType(getString(formSchemaData, "type"));
                    formSchema.setVariable(getString(formSchemaData, "variable"));
                    formSchemas.add(formSchema);
                }
                credentialSchema.setCredentialFormSchemas(formSchemas);
            }
            descriptor.setProviderCredentialSchema(credentialSchema);
        }

        // 支持的模型类型
        List<String> supportedModelTypes = getStringList(yamlData, "supported_model_types");
        if (supportedModelTypes != null) {
            descriptor.setSupportedModelTypes(supportedModelTypes);
        }

        return descriptor;
    }

    private List<ModelDefinition> parseModelsFromPathPattern(String pathPattern, String modelType, Path jarPath) throws PluginParseException {
        try {
            return parseModelsFromJarPattern(pathPattern, modelType, jarPath);
        } catch (Exception e) {
            throw new PluginParseException("Failed to parse models from path pattern: " + pathPattern, e);
        }
    }

    private List<ModelDefinition> parseModelsFromJarPattern(String pathPattern, String modelType, Path jarPath) throws IOException {
        List<ModelDefinition> modelDefinitions = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (matchesPathPattern(entryName, pathPattern)) {
                    try (InputStream inputStream = jarFile.getInputStream(entry)) {
                        ModelDefinition modelDef = parseModelDefinitionFromStream(inputStream, entryName);
                        if (modelDef != null) {
                            modelDef.setModelType(modelType);
                            modelDefinitions.add(modelDef);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse model definition from JAR entry: {}", entryName, e);
                    }
                }
            }
        }
        return modelDefinitions;
    }

    private boolean matchesPathPattern(String path, String pattern) {
        if (pattern.endsWith("*.yaml") || pattern.endsWith("*.yml")) {
            String prefix = pattern.substring(0, pattern.lastIndexOf("*"));
            String suffix = pattern.substring(pattern.lastIndexOf("*") + 1);
            return path.startsWith(prefix) && path.endsWith(suffix);
        }
        return path.equals(pattern);
    }

    @SuppressWarnings("unchecked")
    private ModelDefinition parseModelDefinitionFromStream(InputStream inputStream, String fileName) {
        try {
            Map<String, Object> yamlData = yaml.load(inputStream);
            if (yamlData == null) {
                log.warn("Empty model definition file: {}", fileName);
                return null;
            }
            return ModelDefinition.fromYamlData(yamlData);
        } catch (Exception e) {
            log.error("Failed to parse model definition from stream: {}", fileName, e);
            return null;
        }
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return null;
    }

    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
}











