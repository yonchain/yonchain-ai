package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.typehandler.TypeHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 类型处理器注册中心
 * 
 * 负责管理所有类型处理器的注册和查找
 */
public class TypeHandlerRegistry {
    
    // Class -> TypeHandler
    private final Map<Class<?>, TypeHandler<?>> typeHandlers = new ConcurrentHashMap<>();
    
    /**
     * 注册类型处理器
     * 
     * @param typeHandler 类型处理器
     */
    public <T> void registerTypeHandler(TypeHandler<T> typeHandler) {
        typeHandlers.put(typeHandler.getType(), typeHandler);
    }
    
    /**
     * 注册类型处理器
     * 
     * @param type 类型
     * @param typeHandler 类型处理器
     */
    public <T> void registerTypeHandler(Class<T> type, TypeHandler<T> typeHandler) {
        typeHandlers.put(type, typeHandler);
    }
    
    /**
     * 获取类型处理器
     * 
     * @param type 类型
     * @return 类型处理器
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<TypeHandler<T>> getTypeHandler(Class<T> type) {
        return Optional.ofNullable((TypeHandler<T>) typeHandlers.get(type));
    }
    
    /**
     * 检查类型处理器是否存在
     * 
     * @param type 类型
     * @return 是否存在
     */
    public boolean hasTypeHandler(Class<?> type) {
        return typeHandlers.containsKey(type);
    }
    
    /**
     * 移除类型处理器
     * 
     * @param type 类型
     * @return 被移除的类型处理器
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<TypeHandler<T>> removeTypeHandler(Class<T> type) {
        return Optional.ofNullable((TypeHandler<T>) typeHandlers.remove(type));
    }
    
    /**
     * 清空所有类型处理器
     */
    public void clear() {
        typeHandlers.clear();
    }
    
    /**
     * 获取类型处理器数量
     * 
     * @return 类型处理器数量
     */
    public int size() {
        return typeHandlers.size();
    }
}
