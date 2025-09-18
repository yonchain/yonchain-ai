package com.yonchain.ai.plugin;

/**
 * 插件基础接口
 * 所有插件都必须实现此接口
 * 
 * @author yonchain
 */
public interface Plugin {
    
    /**
     * 获取插件唯一标识符
     * 
     * @return 插件ID
     */
    String getId();
    
    /**
     * 获取插件名称
     * 
     * @return 插件名称
     */
    String getName();
    
    /**
     * 获取插件版本
     * 
     * @return 插件版本
     */
    String getVersion();
    
    /**
     * 获取插件描述
     * 
     * @return 插件描述
     */
    String getDescription();
    
    /**
     * 初始化插件
     * 插件加载后调用此方法进行初始化
     * 
     * @param context 插件上下文
     */
    void initialize(PluginContext context);
    
    /**
     * 销毁插件
     * 插件卸载前调用此方法进行清理
     */
    void dispose();
}

