package com.yonchain.ai.plugin.parser;

import com.yonchain.ai.plugin.descriptor.ProviderDescriptor;

import java.nio.file.Path;
import java.util.List;

/**
 * 模型插件解析器接口
 * 专门处理模型插件的提供商和模型定义解析
 * 
 * @author yonchain
 */
public interface ModelPluginParser {

    /**
     * 解析提供商配置
     *
     * @param providerYamlPath 提供商YAML文件路径（如deepseek.yaml）
     * @param jarPath          JAR文件路径
     * @return 提供商描述符
     * @throws PluginParseException 解析异常
     */
    ProviderDescriptor parseProvider(String providerYamlPath, Path jarPath) throws PluginParseException;

    /**
     * 解析模型定义
     *
     * @param providerDescriptor 提供商描述符
     * @param jarPath            JAR文件路径
     * @return 模型定义列表
     * @throws PluginParseException 解析异常
     */
    List<ModelDefinition> parseModelDefinitions(ProviderDescriptor providerDescriptor, Path jarPath) throws PluginParseException;

    /**
     * 验证提供商配置
     *
     * @param providerDescriptor 提供商描述符
     * @return 是否有效
     */
    boolean validateProvider(ProviderDescriptor providerDescriptor);
}









