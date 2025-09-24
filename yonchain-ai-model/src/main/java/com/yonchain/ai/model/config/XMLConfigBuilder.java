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
            ModelConfiguration configuration = new ModelConfiguration();
            
            // 解析主配置文件
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            
            Element root = document.getDocumentElement();
            
            // 解析settings
            parseSettings(root, configuration);
            
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
        
        ModelDefinition modelDef = new ModelDefinition(id, namespace, type);
        
        // 解析baseUrl
        NodeList baseUrlNodes = modelElement.getElementsByTagName("baseUrl");
        if (baseUrlNodes.getLength() > 0) {
            modelDef.setBaseUrl(baseUrlNodes.item(0).getTextContent().trim());
        }
        
        // 解析completionsPath (仅对chat类型模型)
        if ("chat".equalsIgnoreCase(type)) {
            NodeList completionsPathNodes = modelElement.getElementsByTagName("completionsPath");
            if (completionsPathNodes.getLength() > 0) {
                modelDef.setCompletionsPath(completionsPathNodes.item(0).getTextContent().trim());
            }
            // 如果没有指定completionsPath，使用默认值（已在构造函数中设置）
        }
        
        // 解析auth
        NodeList authNodes = modelElement.getElementsByTagName("auth");
        if (authNodes.getLength() > 0) {
            Element authElement = (Element) authNodes.item(0);
            String authType = authElement.getAttribute("type");
            String authValue = authElement.getTextContent().trim();
            modelDef.setAuthType(authType);
            modelDef.setAuthValue(authValue);
        }
        
        // 解析options
        NodeList optionsNodes = modelElement.getElementsByTagName("options");
        if (optionsNodes.getLength() > 0) {
            Element optionsElement = (Element) optionsNodes.item(0);
            Map<String, Object> options = parseOptions(optionsElement);
            modelDef.setOptions(options);
        }
        
        return modelDef;
    }
    
    /**
     * 解析options配置
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
                
                // 尝试转换为合适的类型
                Object convertedValue = convertValue(value);
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
}
