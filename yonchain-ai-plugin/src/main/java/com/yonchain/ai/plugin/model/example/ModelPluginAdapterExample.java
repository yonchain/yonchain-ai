package com.yonchain.ai.plugin.model.example;

import com.yonchain.ai.model.ModelConfiguration;
import com.yonchain.ai.model.options.ModelOptionsHandler;
import com.yonchain.ai.plugin.model.ModelPluginAdapter;
import com.yonchain.ai.plugin.exception.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.deepseek.DeepSeekChatOptions;

import java.util.Optional;

/**
 * ModelPluginAdapter集成OptionsHandler示例
 * 
 * 演示插件适配器如何在插件启动时自动注册OptionsHandler
 * 
 * @author yonchain
 */
public class ModelPluginAdapterExample {
    
    private static final Logger log = LoggerFactory.getLogger(ModelPluginAdapterExample.class);
    
    /**
     * 演示插件启动和OptionsHandler注册的完整流程
     */
    public static void demonstratePluginLifecycle(ModelPluginAdapter pluginAdapter, 
                                                 ModelConfiguration modelConfiguration,
                                                 String pluginId) {
        try {
            log.info("=== 插件生命周期和OptionsHandler集成演示 ===");
            
            // 1. 启用插件 (这会自动注册OptionsHandlers)
            log.info("1. 启用插件: {}", pluginId);
            pluginAdapter.onPluginEnable(pluginId);
            
            // 2. 验证OptionsHandler注册
            log.info("2. 验证OptionsHandler注册状态");
            verifyOptionsHandlerRegistration(modelConfiguration, pluginId);
            
            // 3. 演示使用注册的OptionsHandler
            log.info("3. 演示使用注册的OptionsHandler");
            demonstrateOptionsHandlerUsage(modelConfiguration);
            
            // 4. 禁用插件 (这会自动注销OptionsHandlers)
            log.info("4. 禁用插件: {}", pluginId);
            pluginAdapter.onPluginDisable(pluginId);
            
            // 5. 验证OptionsHandler注销
            log.info("5. 验证OptionsHandler注销状态");
            verifyOptionsHandlerUnregistration(modelConfiguration);
            
            log.info("=== 演示完成 ===");
            
        } catch (PluginException e) {
            log.error("插件操作失败", e);
        } catch (Exception e) {
            log.error("演示执行失败", e);
        }
    }
    
    /**
     * 验证OptionsHandler注册状态
     */
    private static void verifyOptionsHandlerRegistration(ModelConfiguration modelConfiguration, String pluginId) {
        log.info("--- 验证OptionsHandler注册状态 ---");
        
        // 检查DeepSeek Chat Handler
        Optional<ModelOptionsHandler<DeepSeekChatOptions>> chatHandler = 
            modelConfiguration.getHandler("deepseek:chat");
        
        if (chatHandler.isPresent()) {
            log.info("✓ DeepSeek Chat Handler注册成功: deepseek:chat -> {}", 
                    chatHandler.get().getClass().getSimpleName());
        } else {
            log.warn("✗ DeepSeek Chat Handler未找到: deepseek:chat");
        }
        
        // 测试Handler解析
        ModelOptionsHandler<DeepSeekChatOptions> resolvedHandler = 
            modelConfiguration.resolveHandler("deepseek", "deepseek-chat", "chat", null);
        
        if (resolvedHandler != null) {
            log.info("✓ Handler解析成功: deepseek:deepseek-chat -> {}", 
                    resolvedHandler.getClass().getSimpleName());
        } else {
            log.warn("✗ Handler解析失败: deepseek:deepseek-chat");
        }
        
        // 测试配置驱动的Handler注册
        ModelOptionsHandler<DeepSeekChatOptions> configDrivenHandler = 
            modelConfiguration.resolveHandler("deepseek", "deepseek-chat", "chat", 
                "com.yonchain.ai.plugin.deepseek.options.DeepSeekChatOptionsHandler");
        
        if (configDrivenHandler != null) {
            log.info("✓ 配置驱动Handler解析成功: {}", 
                    configDrivenHandler.getClass().getSimpleName());
        } else {
            log.warn("✗ 配置驱动Handler解析失败");
        }
    }
    
    /**
     * 演示使用OptionsHandler
     */
    private static void demonstrateOptionsHandlerUsage(ModelConfiguration modelConfiguration) {
        log.info("--- 演示OptionsHandler使用 ---");
        
        try {
            // 获取Handler
            ModelOptionsHandler<DeepSeekChatOptions> handler = 
                modelConfiguration.resolveHandler("deepseek", "deepseek-chat", "chat", null);
            
            if (handler == null) {
                log.warn("Handler未找到，无法演示使用");
                return;
            }
            
            // 创建测试配置
            java.util.Map<String, Object> testConfig = java.util.Map.of(
                "model", "deepseek-chat",
                "temperature", 0.8,
                "maxTokens", 4096,
                "topP", 0.95,
                "frequencyPenalty", 0.1,
                "presencePenalty", 0.1,
                "stop", java.util.List.of("Human:", "Assistant:")
            );
            
            log.info("测试配置: {}", testConfig);
            
            // 验证配置
            boolean isValid = handler.validateConfig(testConfig);
            log.info("配置验证结果: {}", isValid ? "✓ 有效" : "✗ 无效");
            
            if (isValid) {
                // 构建选项
                DeepSeekChatOptions options = handler.buildOptions(testConfig);
                log.info("✓ 成功构建DeepSeekChatOptions: {}", options);
                
                // 展示构建结果
                displayBuiltOptions(options);
            }
            
        } catch (Exception e) {
            log.error("OptionsHandler使用演示失败", e);
        }
    }
    
    /**
     * 展示构建的选项详情
     */
    private static void displayBuiltOptions(DeepSeekChatOptions options) {
        log.info("构建的选项详情:");
        log.info("  - Model: {}", options.getModel());
        log.info("  - Temperature: {}", options.getTemperature());
        log.info("  - MaxTokens: {}", options.getMaxTokens());
        log.info("  - TopP: {}", options.getTopP());
        log.info("  - FrequencyPenalty: {}", options.getFrequencyPenalty());
        log.info("  - PresencePenalty: {}", options.getPresencePenalty());
        log.info("  - Stop: {}", options.getStop());
    }
    
    /**
     * 验证OptionsHandler注销状态
     */
    private static void verifyOptionsHandlerUnregistration(ModelConfiguration modelConfiguration) {
        log.info("--- 验证OptionsHandler注销状态 ---");
        
        // 检查Handler是否已被注销
        Optional<ModelOptionsHandler<DeepSeekChatOptions>> chatHandler = 
            modelConfiguration.getHandler("deepseek:chat");
        
        if (chatHandler.isPresent()) {
            log.warn("⚠ DeepSeek Chat Handler仍然存在: deepseek:chat");
            log.warn("注意: 当前ModelConfiguration可能没有提供注销Handler的API");
        } else {
            log.info("✓ DeepSeek Chat Handler已成功注销");
        }
        
        // 测试Handler解析
        ModelOptionsHandler<DeepSeekChatOptions> resolvedHandler = 
            modelConfiguration.resolveHandler("deepseek", "deepseek-chat", "chat", null);
        
        if (resolvedHandler != null) {
            log.warn("⚠ Handler仍可解析: deepseek:deepseek-chat");
            log.warn("这可能是因为Handler被缓存或通过约定规则动态创建");
        } else {
            log.info("✓ Handler解析已失效");
        }
    }
    
    /**
     * 演示插件重启场景
     */
    public static void demonstratePluginRestart(ModelPluginAdapter pluginAdapter, 
                                               ModelConfiguration modelConfiguration,
                                               String pluginId) {
        try {
            log.info("=== 插件重启场景演示 ===");
            
            // 1. 启用插件
            log.info("1. 首次启用插件");
            pluginAdapter.onPluginEnable(pluginId);
            verifyOptionsHandlerRegistration(modelConfiguration, pluginId);
            
            // 2. 禁用插件
            log.info("2. 禁用插件");
            pluginAdapter.onPluginDisable(pluginId);
            verifyOptionsHandlerUnregistration(modelConfiguration);
            
            // 3. 重新启用插件
            log.info("3. 重新启用插件");
            pluginAdapter.onPluginEnable(pluginId);
            verifyOptionsHandlerRegistration(modelConfiguration, pluginId);
            
            log.info("=== 插件重启演示完成 ===");
            
        } catch (Exception e) {
            log.error("插件重启演示失败", e);
        }
    }
}
