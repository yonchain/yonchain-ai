package com.yonchain.ai.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模型名称工具类
 * 处理 provider:model 格式的模型名称
 */
public class ModelNameUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelNameUtils.class);
    
    private static final String SEPARATOR = ":";
    
    /**
     * 构建完整的模型名称（provider:model）
     * 
     * @param provider 提供商名称
     * @param modelName 模型名称
     * @return 完整的模型名称
     */
    public static String buildFullModelName(String provider, String modelName) {
        if (provider == null || provider.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider cannot be null or empty");
        }
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        
        return provider.trim() + SEPARATOR + modelName.trim();
    }
    
    /**
     * 解析模型名称，提取提供商和模型名称
     * 
     * @param fullModelName 完整的模型名称（provider:model 或 model）
     * @return 模型名称信息
     */
    public static ModelNameInfo parseModelName(String fullModelName) {
        if (fullModelName == null || fullModelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        
        String trimmedName = fullModelName.trim();
        int separatorIndex = trimmedName.indexOf(SEPARATOR);
        
        if (separatorIndex > 0 && separatorIndex < trimmedName.length() - 1) {
            // 包含提供商前缀的格式：provider:model
            String provider = trimmedName.substring(0, separatorIndex).trim();
            String modelName = trimmedName.substring(separatorIndex + 1).trim();
            return new ModelNameInfo(provider, modelName, trimmedName);
        } else {
            // 不包含提供商前缀，只是模型名称
            logger.debug("Model name '{}' does not contain provider prefix", trimmedName);
            return new ModelNameInfo(null, trimmedName, trimmedName);
        }
    }
    
    /**
     * 检查模型名称是否包含提供商前缀
     * 
     * @param modelName 模型名称
     * @return 是否包含提供商前缀
     */
    public static boolean hasProviderPrefix(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return false;
        }
        
        String trimmedName = modelName.trim();
        int separatorIndex = trimmedName.indexOf(SEPARATOR);
        return separatorIndex > 0 && separatorIndex < trimmedName.length() - 1;
    }
    
    /**
     * 提取提供商名称
     * 
     * @param fullModelName 完整的模型名称
     * @return 提供商名称，如果不包含提供商前缀则返回null
     */
    public static String extractProvider(String fullModelName) {
        ModelNameInfo info = parseModelName(fullModelName);
        return info.getProvider();
    }
    
    /**
     * 提取模型名称（不包含提供商前缀）
     * 
     * @param fullModelName 完整的模型名称
     * @return 模型名称
     */
    public static String extractModelName(String fullModelName) {
        ModelNameInfo info = parseModelName(fullModelName);
        return info.getModelName();
    }
    
    /**
     * 模型名称信息类
     */
    public static class ModelNameInfo {
        private final String provider;
        private final String modelName;
        private final String fullName;
        
        public ModelNameInfo(String provider, String modelName, String fullName) {
            this.provider = provider;
            this.modelName = modelName;
            this.fullName = fullName;
        }
        
        public String getProvider() {
            return provider;
        }
        
        public String getModelName() {
            return modelName;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public boolean hasProvider() {
            return provider != null && !provider.trim().isEmpty();
        }
        
        @Override
        public String toString() {
            return "ModelNameInfo{" +
                    "provider='" + provider + '\'' +
                    ", modelName='" + modelName + '\'' +
                    ", fullName='" + fullName + '\'' +
                    '}';
        }
    }
}
