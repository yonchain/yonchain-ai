package com.yonchain.ai.model.definition;

import java.util.Map;
import java.util.HashMap;

/**
 * 模型定义类
 * 
 * 包含模型的所有配置信息
 */
public class ModelDefinition {
    
    private String id;
    private String namespace;
    private String type; // CHAT, IMAGE, EMBEDDING, AUDIO
    private String baseUrl;
    private String completionsPath; // 用于chat类型模型，默认 /v1/chat/completions
    private String authType; // bearer, apikey, basic
    private String authValue;
    private String optionsHandler; // 选项处理器标识，格式：provider:type
    private Map<String, Object> options;
    private Map<String, Object> metadata;
    
    public ModelDefinition() {
        this.options = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    public ModelDefinition(String id, String namespace, String type) {
        this();
        this.id = id;
        this.namespace = namespace;
        this.type = type;
        // 为chat类型模型设置默认的completionsPath
        if ("chat".equalsIgnoreCase(type)) {
            this.completionsPath = "/v1/chat/completions";
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getCompletionsPath() {
        return completionsPath;
    }
    
    public void setCompletionsPath(String completionsPath) {
        this.completionsPath = completionsPath;
    }
    
    /**
     * 获取完整的端点URL
     * 对于chat类型，拼接baseUrl和completionsPath
     * 对于其他类型，直接返回baseUrl
     */
    public String getFullEndpoint() {
        if ("chat".equalsIgnoreCase(this.type) && completionsPath != null) {
            return baseUrl + completionsPath;
        }
        return baseUrl;
    }
    
    
    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }
    
    public String getAuthValue() {
        return authValue;
    }
    
    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }
    
    public String getOptionsHandler() {
        return optionsHandler;
    }
    
    public void setOptionsHandler(String optionsHandler) {
        this.optionsHandler = optionsHandler;
    }
    
    public Map<String, Object> getOptions() {
        return options;
    }
    
    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
    
    public Object getOption(String key) {
        return options.get(key);
    }
    
    public void setOption(String key, Object value) {
        options.put(key, value);
    }
    
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    /**
     * 获取完整的模型ID（包含命名空间）
     * 
     * @return namespace:id 格式的完整ID
     */
    public String getFullId() {
        return namespace + ":" + id;
    }
    
    @Override
    public String toString() {
        return "ModelDefinition{" +
                "id='" + id + '\'' +
                ", namespace='" + namespace + '\'' +
                ", type='" + type + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", completionsPath='" + completionsPath + '\'' +
                ", authType='" + authType + '\'' +
                '}';
    }
}
