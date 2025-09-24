package com.yonchain.ai.model.config;

import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.model.core.ModelConfiguration;
import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.util.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * XML配置构建器
 * 
 * 负责解析XML配置文件并构建ModelConfiguration
 */
public class XMLConfigBuilder {
    
    private final InputStream inputStream;
    private ModelConfiguration configuration;
    
    public XMLConfigBuilder(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    /**
     * 解析配置文件
     * 
     * @return ModelConfiguration对象
     */
    public ModelConfiguration parse() {
        try {
            this.configuration = new ModelConfiguration();
            
            // 解析主配置文件
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            
            Element root = document.getDocumentElement();
            
            // 解析settings
            parseSettings(root, configuration);
            
            // 解析environments
            parseEnvironments(root, configuration);
            
            // 注意：不再解析全局typeHandlers，只在命名空间文件中配置defaultHandlers
            
            // 解析models资源文件
            parseModelResources(root, configuration);
            
            // 解析aliases
            parseAliases(root, configuration);
            
            return configuration;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML configuration", e);
        }
    }
    
    /**
     * 解析settings配置
     */
    private void parseSettings(Element root, ModelConfiguration configuration) {
        NodeList settingsNodes = root.getElementsByTagName("settings");
        if (settingsNodes.getLength() > 0) {
            Element settingsElement = (Element) settingsNodes.item(0);
            NodeList settingNodes = settingsElement.getElementsByTagName("setting");
            
            for (int i = 0; i < settingNodes.getLength(); i++) {
                Element settingElement = (Element) settingNodes.item(i);
                String name = settingElement.getAttribute("name");
                String value = settingElement.getAttribute("value");
                configuration.setProperty(name, value);
            }
        }
        
        // 设置默认值
        if (configuration.getProperty("cache.enabled") == null) {
            configuration.setProperty("cache.enabled", "true");
        }
        if (configuration.getProperty("default.timeout") == null) {
            configuration.setProperty("default.timeout", "30");
        }
    }
    
    /**
     * 解析environments配置
     */
    private void parseEnvironments(Element root, ModelConfiguration configuration) {
        NodeList environmentsNodes = root.getElementsByTagName("environments");
        if (environmentsNodes.getLength() > 0) {
            Element environmentsElement = (Element) environmentsNodes.item(0);
            String defaultEnv = environmentsElement.getAttribute("default");
            if (defaultEnv == null || defaultEnv.isEmpty()) {
                defaultEnv = "dev";
            }
            
            NodeList environmentNodes = environmentsElement.getElementsByTagName("environment");
            Map<String, Map<String, String>> environments = new HashMap<>();
            
            for (int i = 0; i < environmentNodes.getLength(); i++) {
                Element envElement = (Element) environmentNodes.item(i);
                String envId = envElement.getAttribute("id");
                
                Map<String, String> envProperties = new HashMap<>();
                NodeList propertiesNodes = envElement.getElementsByTagName("properties");
                if (propertiesNodes.getLength() > 0) {
                    Element propertiesElement = (Element) propertiesNodes.item(0);
                    NodeList propertyNodes = propertiesElement.getElementsByTagName("property");
                    
                    for (int j = 0; j < propertyNodes.getLength(); j++) {
                        Element propertyElement = (Element) propertyNodes.item(j);
                        String name = propertyElement.getAttribute("name");
                        String value = propertyElement.getAttribute("value");
                        envProperties.put(name, value);
                    }
                }
                
                environments.put(envId, envProperties);
            }
            
            // 设置环境配置到ModelConfiguration
            configuration.setEnvironments(environments);
            configuration.setDefaultEnvironment(defaultEnv);
            
            // 加载默认环境的属性
            if (environments.containsKey(defaultEnv)) {
                Map<String, String> defaultEnvProps = environments.get(defaultEnv);
                for (Map.Entry<String, String> entry : defaultEnvProps.entrySet()) {
                    configuration.setProperty(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    
    
    /**
     * 解析模型资源文件
     */
    private void parseModelResources(Element root, ModelConfiguration configuration) {
        NodeList modelsNodes = root.getElementsByTagName("models");
        if (modelsNodes.getLength() > 0) {
            Element modelsElement = (Element) modelsNodes.item(0);
            NodeList resourceNodes = modelsElement.getElementsByTagName("resource");
            
            for (int i = 0; i < resourceNodes.getLength(); i++) {
                Element resourceElement = (Element) resourceNodes.item(i);
                String path = resourceElement.getAttribute("path");
                
                // 解析每个模型资源文件
                parseModelResource(path, configuration);
            }
        }
    }
    
    /**
     * 解析命名空间级的默认Handler配置
     */
    private void parseDefaultHandlers(Element root, String namespace, ModelConfiguration configuration) {
        NodeList defaultHandlersNodes = root.getElementsByTagName("defaultHandlers");
        if (defaultHandlersNodes.getLength() > 0) {
            Element defaultHandlersElement = (Element) defaultHandlersNodes.item(0);
            NodeList handlerNodes = defaultHandlersElement.getElementsByTagName("handler");
            
            System.out.println("DEBUG: 解析命名空间 " + namespace + " 的默认Handler配置，找到 " + handlerNodes.getLength() + " 个Handler");
            
            for (int i = 0; i < handlerNodes.getLength(); i++) {
                Element handlerElement = (Element) handlerNodes.item(i);
                String type = handlerElement.getAttribute("type");
                String handlerClass = handlerElement.getAttribute("class");
                
                if (type != null && !type.isEmpty() && handlerClass != null && !handlerClass.isEmpty()) {
                    // 注册到选项处理器注册中心（命名空间级）
                    configuration.getOptionsHandlerRegistry()
                                .registerNamespaceHandlerByClass(namespace, type, handlerClass);
                    
                    System.out.println("DEBUG: 注册命名空间Handler: " + namespace + ":" + type + " -> " + handlerClass);
                } else {
                    System.err.println("WARNING: 无效的Handler配置，缺少type或class属性: " + 
                                     "type=" + type + ", class=" + handlerClass);
                }
            }
        } else {
            System.out.println("DEBUG: 命名空间 " + namespace + " 没有配置defaultHandlers");
        }
    }
    
    /**
     * 解析单个模型资源文件
     */
    private void parseModelResource(String resourcePath, ModelConfiguration configuration) {
        try {
            // 处理classpath:前缀
            String actualPath = resourcePath;
            if (resourcePath.startsWith("classpath:")) {
                actualPath = resourcePath.substring("classpath:".length());
            }
            
            InputStream resourceStream = Resources.getResourceAsStream(actualPath);
            if (resourceStream == null) {
                System.err.println("Warning: Could not find model resource: " + resourcePath);
                throw  new YonchainResourceNotFoundException("Warning: Could not find model resource: " + resourcePath);
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resourceStream);
            
            Element root = document.getDocumentElement();
            String namespace = root.getAttribute("namespace");
            
            // 解析命名空间级的默认Handler配置
            parseDefaultHandlers(root, namespace, configuration);
            
            // 使用直接子元素遍历，避免深度搜索
            NodeList childNodes = root.getChildNodes();
            int modelCount = 0;
            int processedCount = 0;
            
            System.out.println("DEBUG: Starting to parse resource " + resourcePath + " for namespace: " + namespace);
            
            // 先统计model元素数量
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNodes.item(i);
                    if ("model".equals(element.getTagName())) {
                        modelCount++;
                    }
                }
            }
            
            System.out.println("DEBUG: Found " + modelCount + " model elements in " + resourcePath);
            
            // 处理model元素
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) childNodes.item(i);
                    if ("model".equals(element.getTagName())) {
                        processedCount++;
                        String modelId = element.getAttribute("id");
                        System.out.println("DEBUG: Processing model " + processedCount + "/" + modelCount + ": " + namespace + ":" + modelId);
                        
                        ModelDefinition modelDef = parseModelDefinition(element, namespace);
                        configuration.getModelRegistry().registerModel(modelDef);
                        System.out.println("DEBUG: Successfully registered model: " + namespace + ":" + modelId);
                    }
                }
            }
            
            System.out.println("DEBUG: Finished parsing resource " + resourcePath + ", processed " + processedCount + " models");
            
        } catch (Exception e) {
            throw  new YonchainResourceNotFoundException("Warning: Failed to parse model resource: " + resourcePath + ", " + e.getMessage());
        }
    }
    
    /**
     * 解析模型定义
     */
    private ModelDefinition parseModelDefinition(Element modelElement, String namespace) {
        String id = modelElement.getAttribute("id");
        String type = modelElement.getAttribute("type");
        String optionsHandler = modelElement.getAttribute("optionsHandler");

        ModelDefinition modelDef = new ModelDefinition(id, namespace, type);
        if (optionsHandler != null && !optionsHandler.isEmpty()) {
            modelDef.setOptionsHandler(optionsHandler);
        }
        
        // 解析baseUrl (支持占位符)
        NodeList baseUrlNodes = modelElement.getElementsByTagName("baseUrl");
        if (baseUrlNodes.getLength() > 0) {
            String baseUrl = baseUrlNodes.item(0).getTextContent().trim();
            modelDef.setBaseUrl(resolvePlaceholders(baseUrl));
        }
        
        // 解析completionsPath (仅对chat类型模型)
        if ("chat".equalsIgnoreCase(type)) {
            NodeList completionsPathNodes = modelElement.getElementsByTagName("completionsPath");
            if (completionsPathNodes.getLength() > 0) {
                String completionsPath = completionsPathNodes.item(0).getTextContent().trim();
                modelDef.setCompletionsPath(resolvePlaceholders(completionsPath));
            }
            // 如果没有指定completionsPath，使用默认值（已在构造函数中设置）
        }
        
        // 解析auth (支持占位符)
        NodeList authNodes = modelElement.getElementsByTagName("auth");
        if (authNodes.getLength() > 0) {
            Element authElement = (Element) authNodes.item(0);
            String authType = authElement.getAttribute("type");
            String authValue = authElement.getTextContent().trim();
            modelDef.setAuthType(authType);
            modelDef.setAuthValue(resolvePlaceholders(authValue));
        }
        
        // 解析options (支持占位符)
        NodeList optionsNodes = modelElement.getElementsByTagName("options");
        if (optionsNodes.getLength() > 0) {
            Element optionsElement = (Element) optionsNodes.item(0);
            Map<String, Object> options = parseOptions(optionsElement);
            modelDef.setOptions(options);
        }
        
        return modelDef;
    }
    
    /**
     * 解析options配置 (支持占位符)
     */
    private Map<String, Object> parseOptions(Element optionsElement) {
        Map<String, Object> options = new HashMap<>();
        NodeList childNodes = optionsElement.getChildNodes();
        
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String key = element.getTagName();
                String value = element.getTextContent().trim();
                
                // 解析占位符
                String resolvedValue = resolvePlaceholders(value);
                
                // 尝试转换为合适的类型
                Object convertedValue = convertValue(resolvedValue);
                options.put(key, convertedValue);
            }
        }
        
        return options;
    }
    
    /**
     * 转换值为合适的类型
     */
    private Object convertValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        // 尝试转换为数字
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            // 不是数字，检查布尔值
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                return Boolean.parseBoolean(value);
            }
            // 返回字符串
            return value;
        }
    }
    
    /**
     * 解析别名配置
     */
    private void parseAliases(Element root, ModelConfiguration configuration) {
        NodeList aliasesNodes = root.getElementsByTagName("aliases");
        if (aliasesNodes.getLength() > 0) {
            Element aliasesElement = (Element) aliasesNodes.item(0);
            NodeList aliasNodes = aliasesElement.getElementsByTagName("alias");
            
            for (int i = 0; i < aliasNodes.getLength(); i++) {
                Element aliasElement = (Element) aliasNodes.item(i);
                String name = aliasElement.getAttribute("name");
                String target = aliasElement.getAttribute("target");
                configuration.setProperty("alias." + name, target);
            }
        }
    }
    
    /**
     * 解析占位符
     * 
     * 支持以下格式的占位符：
     * - ${property.name} - 从配置属性中获取值
     * - ${env:ENV_VAR_NAME} - 从环境变量中获取值
     * - ${deepseek.apiKey} - 从当前环境配置中获取值
     * 
     * @param text 包含占位符的文本
     * @return 解析后的文本
     */
    private String resolvePlaceholders(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String result = text;
        
        // 匹配 ${...} 格式的占位符
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            String placeholder = matcher.group(0); // 完整的占位符 ${...}
            String key = matcher.group(1); // 占位符内的键名
            
            String value = resolveProperty(key);
            if (value != null) {
                result = result.replace(placeholder, value);
                System.out.println("DEBUG: Resolved placeholder " + placeholder + " -> " + (value.contains("key") ? "***" : value));
            } else {
                System.err.println("WARNING: Could not resolve placeholder: " + placeholder);
            }
        }
        
        return result;
    }
    
    /**
     * 解析属性值
     * 
     * @param key 属性键
     * @return 属性值，如果未找到返回null
     */
    private String resolveProperty(String key) {
        // 1. 检查环境变量前缀
        if (key.startsWith("env:")) {
            String envVar = key.substring(4);
            String value = System.getenv(envVar);
            if (value != null) {
                return value;
            }
        }
        
        // 2. 从配置属性中查找
        if (configuration != null) {
            String value = configuration.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        
        // 3. 从系统属性中查找
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        
        // 4. 从环境变量中查找（将点号转换为下划线）
        String envKey = key.replace('.', '_').toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null) {
            return envValue;
        }
        
        // 5. 直接查找环境变量（保持原始键名）
        String directEnvValue = System.getenv(key);
        if (directEnvValue != null) {
            return directEnvValue;
        }
        
        return null;
    }
}
