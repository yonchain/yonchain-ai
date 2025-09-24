package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.optionshandler.OptionsHandler;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 选项处理器注册中心
 * 
 * 负责管理和查找模型选项处理器
 */
public class OptionsHandlerRegistry {
    
    // 存储选项处理器 key: provider:type, value: OptionsHandler
    private final ConcurrentHashMap<String, OptionsHandler<?>> handlers;
    
    public OptionsHandlerRegistry() {
        this.handlers = new ConcurrentHashMap<>();
    }
    
    /**
     * 注册选项处理器
     * 
     * @param key 键 (格式: provider:type)
     * @param handler 选项处理器
     */
    public void registerHandler(String key, OptionsHandler<?> handler) {
        OptionsHandler<?> existing = handlers.put(key, handler);
        if (existing != null) {
            System.out.println("OptionsHandler replaced for key: " + key);
        }
    }
    
    /**
     * 根据键获取选项处理器
     * 
     * @param key 键 (格式: provider:type)
     * @return OptionsHandler
     */
    public <T> Optional<OptionsHandler<T>> getHandler(String key) {
        @SuppressWarnings("unchecked")
        OptionsHandler<T> handler = (OptionsHandler<T>) handlers.get(key);
        return Optional.ofNullable(handler);
    }
    
    /**
     * 根据提供商和类型获取处理器
     * 
     * @param provider 提供商
     * @param type 类型
     * @return OptionsHandler
     */
    public <T> Optional<OptionsHandler<T>> getHandler(String provider, String type) {
        return getHandler(provider + ":" + type);
    }
    
    /**
     * 检查是否存在指定的处理器
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean hasHandler(String key) {
        return handlers.containsKey(key);
    }
    
    /**
     * 获取所有注册的处理器键
     * 
     * @return 键集合
     */
    public java.util.Set<String> getAllHandlerKeys() {
        return handlers.keySet();
    }
    
    /**
     * 清空所有处理器
     */
    public void clear() {
        handlers.clear();
    }
    
    /**
     * 获取处理器数量
     * 
     * @return 处理器数量
     */
    public int size() {
        return handlers.size();
    }
}