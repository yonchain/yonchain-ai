package com.yonchain.ai.model.optionshandler;

import java.util.Map;

/**
 * 选项处理器接口
 * 
 * 负责将配置文件中的配置转换为Spring AI ModelOptions对象
 * 
 * @param <T> Spring AI ModelOptions类型 (如 OpenAiChatOptions, OpenAiImageOptions)
 */
public interface OptionsHandler<T> {
    
    /**
     * 从配置构建ModelOptions对象
     * 
     * @param config 配置Map
     * @return 构建的ModelOptions对象
     */
    T buildOptions(Map<String, Object> config);
    
    /**
     * 验证配置是否有效
     * 
     * @param config 配置Map
     * @return 是否有效
     */
    default boolean validateConfig(Map<String, Object> config) {
        return true;
    }
    
    /**
     * 获取默认配置
     * 
     * @return 默认配置Map
     */
    default Map<String, Object> getDefaultConfig() {
        return Map.of();
    }
}
