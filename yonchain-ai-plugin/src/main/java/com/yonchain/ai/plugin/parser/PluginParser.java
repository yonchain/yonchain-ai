package com.yonchain.ai.plugin.parser;

import com.yonchain.ai.plugin.descriptor.PluginDescriptor;
import com.yonchain.ai.plugin.exception.PluginParseException;
import com.yonchain.ai.plugin.validation.ValidationResult;

import java.nio.file.Path;

/**
 * 插件解析器接口
 * 负责解析plugin.yaml文件，获取插件基本信息
 * 
 * @author yonchain
 */
public interface PluginParser {
    
    /**
     * 解析插件
     * 
     * @param pluginPath 插件路径（JAR文件）
     * @return 插件描述符
     * @throws PluginParseException 解析异常
     */
    PluginDescriptor parsePlugin(Path pluginPath) throws PluginParseException;
    
    /**
     * 验证插件
     * 
     * @param descriptor 插件描述符
     * @return 验证结果
     */
    ValidationResult validatePlugin(PluginDescriptor descriptor);
    
    /**
     * 检查插件路径是否有效
     * 
     * @param pluginPath 插件路径（JAR文件）
     * @return 是否有效
     */
    boolean isValidPluginPath(Path pluginPath);
}

