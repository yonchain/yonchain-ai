package com.yonchain.ai.model.typehandler;

import java.util.Map;

/**
 * 类型处理器接口
 * 
 * 负责将配置文件中的配置转换为特定类型的对象
 * 
 * @param <T> 目标类型
 */
public interface TypeHandler<T> {
    
    /**
     * 获取处理的类型
     * 
     * @return 目标类型的Class对象
     */
    Class<T> getType();
    
    /**
     * 从配置构建对象
     * 
     * @param config 配置Map
     * @return 构建的对象
     */
    T buildFromConfig(Map<String, Object> config);
    
    /**
     * 验证配置是否有效
     * 
     * @param config 配置Map
     * @return 是否有效
     */
    default boolean validateConfig(Map<String, Object> config) {
        return true;
    }
}
