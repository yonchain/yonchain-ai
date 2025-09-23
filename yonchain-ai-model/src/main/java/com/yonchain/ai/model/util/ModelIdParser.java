package com.yonchain.ai.model.util;

/**
 * 模型ID解析工具类
 */
public class ModelIdParser {
    
    private static final String SEPARATOR = ":";
    
    /**
     * 解析模型ID
     * 
     * @param modelId 模型ID，格式：namespace:modelName 或 modelName
     * @param defaultNamespace 默认命名空间
     * @return 解析结果
     */
    public static ParsedModelId parse(String modelId, String defaultNamespace) {
        if (modelId == null || modelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Model ID cannot be null or empty");
        }
        
        String trimmedModelId = modelId.trim();
        
        if (trimmedModelId.contains(SEPARATOR)) {
            String[] parts = trimmedModelId.split(SEPARATOR, 2);
            return new ParsedModelId(parts[0], parts[1]);
        } else {
            if (defaultNamespace == null || defaultNamespace.trim().isEmpty()) {
                throw new IllegalArgumentException("Default namespace is required for model ID without namespace: " + modelId);
            }
            return new ParsedModelId(defaultNamespace, trimmedModelId);
        }
    }
    
    /**
     * 构建完整的模型ID
     * 
     * @param namespace 命名空间
     * @param modelName 模型名称
     * @return 完整的模型ID
     */
    public static String buildFullId(String namespace, String modelName) {
        if (namespace == null || namespace.trim().isEmpty()) {
            throw new IllegalArgumentException("Namespace cannot be null or empty");
        }
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        
        return namespace.trim() + SEPARATOR + modelName.trim();
    }
    
    /**
     * 解析结果类
     */
    public static class ParsedModelId {
        private final String namespace;
        private final String modelName;
        
        public ParsedModelId(String namespace, String modelName) {
            this.namespace = namespace;
            this.modelName = modelName;
        }
        
        public String getNamespace() {
            return namespace;
        }
        
        public String getModelName() {
            return modelName;
        }
        
        public String getFullId() {
            return buildFullId(namespace, modelName);
        }
        
        @Override
        public String toString() {
            return "ParsedModelId{" +
                    "namespace='" + namespace + '\'' +
                    ", modelName='" + modelName + '\'' +
                    '}';
        }
    }
}
