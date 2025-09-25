package com.yonchain.ai.model.util;

import com.yonchain.ai.model.optionshandler.OptionsHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OptionsHandler工具类
 * 
 * 提供动态创建和缓存OptionsHandler实例的功能
 */
public class OptionsHandlerUtils {
    
    // 缓存OptionsHandler实例，避免重复创建
    private static final Map<String, OptionsHandler<?>> HANDLER_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 根据类路径创建OptionsHandler实例
     * 
     * @param handlerClassName OptionsHandler的完整类路径
     * @return OptionsHandler实例
     * @throws RuntimeException 如果创建失败
     */
    @SuppressWarnings("unchecked")
    public static <T> OptionsHandler<T> createHandler(String handlerClassName) {
        if (handlerClassName == null || handlerClassName.trim().isEmpty()) {
            return null;
        }
        
        return (OptionsHandler<T>) HANDLER_CACHE.computeIfAbsent(handlerClassName, className -> {
            try {
                Class<?> clazz = Class.forName(className);
                
                if (!OptionsHandler.class.isAssignableFrom(clazz)) {
                    throw new IllegalArgumentException("Class " + className + " does not implement OptionsHandler interface");
                }
                
                Object instance = clazz.getDeclaredConstructor().newInstance();
                return (OptionsHandler<?>) instance;
                
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("OptionsHandler class not found: " + className, e);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create OptionsHandler instance: " + className, e);
            }
        });
    }
    
    /**
     * 检查指定的类是否是有效的OptionsHandler
     * 
     * @param handlerClassName 类路径
     * @return 是否有效
     */
    public static boolean isValidHandler(String handlerClassName) {
        if (handlerClassName == null || handlerClassName.trim().isEmpty()) {
            return false;
        }
        
        try {
            Class<?> clazz = Class.forName(handlerClassName);
            return OptionsHandler.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * 清空缓存
     */
    public static void clearCache() {
        HANDLER_CACHE.clear();
    }
    
    /**
     * 获取缓存的Handler数量
     * 
     * @return 缓存数量
     */
    public static int getCacheSize() {
        return HANDLER_CACHE.size();
    }
}

