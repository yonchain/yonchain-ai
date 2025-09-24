package com.yonchain.ai.model.registry;

import com.yonchain.ai.model.optionshandler.OptionsHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 选项处理器注册中心
 * 
 * 统一管理所有级别的OptionsHandler：
 * - 命名空间级：namespace:type (如 "openai:chat")
 * - 模型级：namespace:modelId (如 "openai:gpt-4")  
 * - 类名级：完整类名 (如 "com.yonchain.ai.model.optionshandler.openai.OpenAiChatOptionsHandler")
 * - 别名级：自定义别名 (如 "openai-chat-v1")
 */
public class OptionsHandlerRegistry {
    
    // 存储选项处理器，支持多种键格式
    private final ConcurrentHashMap<String, OptionsHandler<?>> handlers;
    
    public OptionsHandlerRegistry() {
        this.handlers = new ConcurrentHashMap<>();
    }
    
    // === 通用注册方法 ===
    
    /**
     * 注册选项处理器
     * 
     * @param key 键（支持多种格式：namespace:type, namespace:modelId, 完整类名等）
     * @param handler 选项处理器
     */
    public void registerHandler(String key, OptionsHandler<?> handler) {
        OptionsHandler<?> existing = handlers.put(key, handler);
        if (existing != null) {
            System.out.println("OptionsHandler replaced for key: " + key);
        } else {
            System.out.println("OptionsHandler registered for key: " + key);
        }
    }
    
    // === 便捷注册方法 ===
    
    /**
     * 注册命名空间级Handler
     * 
     * @param namespace 命名空间
     * @param type 模型类型
     * @param handler Handler实例
     */
    public void registerNamespaceHandler(String namespace, String type, OptionsHandler<?> handler) {
        String key = namespace + ":" + type;
        registerHandler(key, handler);
        System.out.println("注册命名空间Handler: " + key + " -> " + handler.getClass().getSimpleName());
    }
    
    /**
     * 注册模型级Handler
     * 
     * @param namespace 命名空间
     * @param modelId 模型ID
     * @param handler Handler实例
     */
    public void registerModelHandler(String namespace, String modelId, OptionsHandler<?> handler) {
        String key = namespace + ":" + modelId;
        registerHandler(key, handler);
        System.out.println("注册模型Handler: " + key + " -> " + handler.getClass().getSimpleName());
    }
    
    /**
     * 通过类名注册命名空间级Handler
     * 
     * @param namespace 命名空间
     * @param type 模型类型
     * @param handlerClass Handler类名
     */
    public void registerNamespaceHandlerByClass(String namespace, String type, String handlerClass) {
        try {
            OptionsHandler<?> handler = createHandlerByClass(handlerClass);
            registerNamespaceHandler(namespace, type, handler);
        } catch (Exception e) {
            System.err.println("Failed to register namespace handler: " + namespace + ":" + type + " -> " + handlerClass + ", error: " + e.getMessage());
        }
    }
    
    /**
     * 通过类名注册模型级Handler
     * 
     * @param namespace 命名空间
     * @param modelId 模型ID
     * @param handlerClass Handler类名
     */
    public void registerModelHandlerByClass(String namespace, String modelId, String handlerClass) {
        try {
            OptionsHandler<?> handler = createHandlerByClass(handlerClass);
            registerModelHandler(namespace, modelId, handler);
        } catch (Exception e) {
            System.err.println("Failed to register model handler: " + namespace + ":" + modelId + " -> " + handlerClass + ", error: " + e.getMessage());
        }
    }
    
    // === 查找方法 ===
    
    /**
     * 根据键获取选项处理器
     * 
     * @param key 键（支持多种格式）
     * @return OptionsHandler实例
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<OptionsHandler<T>> getHandler(String key) {
        OptionsHandler<T> handler = (OptionsHandler<T>) handlers.get(key);
        return Optional.ofNullable(handler);
    }
    
    /**
     * 解析OptionsHandler（带优先级查找和约定兜底）
     * 
     * 查找优先级：
     * 1. 模型级：namespace:modelId
     * 2. 显式类名：完整类名
     * 3. 命名空间级：namespace:type
     * 4. 约定规则：动态创建
     * 
     * @param namespace 命名空间
     * @param modelId 模型ID
     * @param type 模型类型
     * @param explicitHandlerClass 显式指定的Handler类名（可为null）
     * @return OptionsHandler实例，可能为null
     */
    @SuppressWarnings("unchecked")
    public <T> OptionsHandler<T> resolveHandler(String namespace, String modelId, String type, String explicitHandlerClass) {
        // 1. 模型级优先：namespace:modelId
        String modelKey = namespace + ":" + modelId;
        Optional<OptionsHandler<T>> modelHandler = getHandler(modelKey);
        if (modelHandler.isPresent()) {
            System.out.println("DEBUG: 使用模型级Handler: " + modelKey);
            return modelHandler.get();
        }
        
        // 2. 显式类名：如果模型定义中指定了optionsHandler
        if (explicitHandlerClass != null && !explicitHandlerClass.isEmpty()) {
            Optional<OptionsHandler<T>> explicitHandler = getHandler(explicitHandlerClass);
            if (explicitHandler.isPresent()) {
                System.out.println("DEBUG: 使用显式Handler: " + explicitHandlerClass);
                return explicitHandler.get();
            }
            // 如果注册表中没有，尝试动态创建并缓存
            try {
                OptionsHandler<T> handler = (OptionsHandler<T>) createHandlerByClass(explicitHandlerClass);
                registerHandler(explicitHandlerClass, handler); // 缓存到注册表
                System.out.println("DEBUG: 动态创建显式Handler: " + explicitHandlerClass);
                return handler;
            } catch (Exception e) {
                System.err.println("ERROR: Failed to create explicit handler: " + explicitHandlerClass + ", error: " + e.getMessage());
            }
        }
        
        // 3. 命名空间级默认：namespace:type
        String namespaceKey = namespace + ":" + type;
        Optional<OptionsHandler<T>> namespaceHandler = getHandler(namespaceKey);
        if (namespaceHandler.isPresent()) {
            System.out.println("DEBUG: 使用命名空间Handler: " + namespaceKey);
            return namespaceHandler.get();
        }
        
        // 4. 约定规则：动态创建
        String conventionClass = generateConventionHandlerClass(namespace, type);
        if (conventionClass != null) {
            try {
                OptionsHandler<T> handler = (OptionsHandler<T>) createHandlerByClass(conventionClass);
                // 缓存到命名空间级，提高后续查找性能
                registerNamespaceHandler(namespace, type, handler);
                System.out.println("DEBUG: 使用约定Handler: " + conventionClass);
                return handler;
            } catch (Exception e) {
                System.err.println("ERROR: Failed to create convention handler: " + conventionClass + ", error: " + e.getMessage());
            }
        }
        
        System.out.println("DEBUG: 未找到OptionsHandler: " + namespace + ":" + modelId + " (type: " + type + ")");
        return null;
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
    
    // === 辅助方法 ===
    
    /**
     * 通过类名创建Handler实例
     */
    private OptionsHandler<?> createHandlerByClass(String handlerClass) throws Exception {
        Class<?> clazz = Class.forName(handlerClass);
        return (OptionsHandler<?>) clazz.getDeclaredConstructor().newInstance();
    }
    
    /**
     * 根据约定规则生成Handler类名
     * 
     * 约定规则：com.yonchain.ai.model.optionshandler.{namespace}.{Namespace}{Type}OptionsHandler
     * 
     * @param namespace 命名空间
     * @param type 模型类型
     * @return 按约定生成的Handler类名
     */
    private String generateConventionHandlerClass(String namespace, String type) {
        if (namespace == null || type == null) {
            return null;
        }
        
        String capitalizedNamespace = capitalize(namespace);
        String capitalizedType = capitalize(type);
        
        return String.format("com.yonchain.ai.model.optionshandler.%s.%s%sOptionsHandler", 
                           namespace.toLowerCase(), 
                           capitalizedNamespace, 
                           capitalizedType);
    }
    
    /**
     * 首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * 获取所有注册的Handler键
     * 
     * @return Handler键集合
     */
    public Set<String> getAllHandlerKeys() {
        return handlers.keySet();
    }
    
    /**
     * 获取所有命名空间级Handler
     * 
     * @return 命名空间级Handler的键集合
     */
    public Set<String> getNamespaceHandlerKeys() {
        return handlers.keySet().stream()
                      .filter(key -> key.contains(":") && !key.contains("."))
                      .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * 获取所有模型级Handler
     * 
     * @return 模型级Handler的键集合
     */
    public Set<String> getModelHandlerKeys() {
        return handlers.keySet().stream()
                      .filter(key -> key.contains(":") && !key.contains(".") && 
                             !isNamespaceTypeKey(key))
                      .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * 判断是否为命名空间:类型格式的键
     */
    private boolean isNamespaceTypeKey(String key) {
        if (!key.contains(":")) return false;
        String[] parts = key.split(":");
        if (parts.length != 2) return false;
        String type = parts[1];
        return "chat".equals(type) || "image".equals(type) || "embedding".equals(type) || "audio".equals(type);
    }
}