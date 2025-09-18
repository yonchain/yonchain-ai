package com.yonchain.ai.plugin;

import com.yonchain.ai.model.registry.ModelRegistry;
import org.springframework.context.ApplicationContext;

/**
 * 插件上下文
 * 提供插件运行时需要的各种服务和组件
 * 
 * @author yonchain
 */
public interface PluginContext {
    
    /**
     * 获取Spring应用上下文
     * 
     * @return Spring应用上下文
     */
    ApplicationContext getApplicationContext();
    
    /**
     * 获取模型注册中心
     * 
     * @return 模型注册中心
     */
    ModelRegistry getModelRegistry();
    
    /**
     * 获取模型插件适配器
     * 
     * @return 模型插件适配器
     */
    Object getModelPluginAdapter();
    
    /**
     * 获取插件配置
     * 
     * @param key 配置键
     * @return 配置值
     */
    String getPluginConfig(String key);
    
    /**
     * 设置插件配置
     * 
     * @param key 配置键
     * @param value 配置值
     */
    void setPluginConfig(String key, String value);
    
    /**
     * 获取插件工作目录
     * 
     * @return 插件工作目录路径
     */
    String getPluginWorkDirectory();
    
    /**
     * 记录日志
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @param args 参数
     */
    void log(String level, String message, Object... args);
}
