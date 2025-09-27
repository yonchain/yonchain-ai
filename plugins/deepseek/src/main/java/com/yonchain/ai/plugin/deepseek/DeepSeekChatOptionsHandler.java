package com.yonchain.ai.plugin.deepseek;

import com.yonchain.ai.model.options.ModelOptionsHandler;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * DeepSeek聊天模型选项处理器
 * 
 * 负责将配置Map转换为DeepSeekChatOptions对象
 * 
 * @author yonchain
 */
public class DeepSeekChatOptionsHandler implements ModelOptionsHandler<DeepSeekChatOptions> {
    
    private static final Logger log = LoggerFactory.getLogger(DeepSeekChatOptionsHandler.class);
    
    @Override
    public DeepSeekChatOptions buildOptions(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return DeepSeekChatOptions.builder().build();
        }
        
        log.debug("Building DeepSeekChatOptions from config: {}", config);
        
        var builder = DeepSeekChatOptions.builder();
        
        // 设置模型 (必需)
        /*if (config.containsKey("model")) {
            builder.model((String) config.get("model"));
        }
        */
        // 设置温度 (0-2.0)
        if (config.containsKey("temperature")) {
            Object temp = config.get("temperature");
            if (temp instanceof Number) {
                builder.temperature(((Number) temp).doubleValue());
            }
        }
        
        // 设置最大令牌数 (DeepSeek-Chat最大支持32K)
        if (config.containsKey("maxTokens")) {
            Object maxTokens = config.get("maxTokens");
            if (maxTokens instanceof Number) {
                builder.maxTokens(((Number) maxTokens).intValue());
            }
        }
        
        // 设置top_p (0-1.0)
        if (config.containsKey("topP")) {
            Object topP = config.get("topP");
            if (topP instanceof Number) {
                builder.topP(((Number) topP).doubleValue());
            }
        }
        
        // 设置频率惩罚 (-2.0 to 2.0)
        if (config.containsKey("frequencyPenalty")) {
            Object penalty = config.get("frequencyPenalty");
            if (penalty instanceof Number) {
                builder.frequencyPenalty(((Number) penalty).doubleValue());
            }
        }
        
        // 设置存在惩罚 (-2.0 to 2.0)
        if (config.containsKey("presencePenalty")) {
            Object penalty = config.get("presencePenalty");
            if (penalty instanceof Number) {
                builder.presencePenalty(((Number) penalty).doubleValue());
            }
        }
        
        // 设置停止词
        if (config.containsKey("stop")) {
            Object stop = config.get("stop");
            if (stop instanceof String) {
                builder.stop(List.of((String) stop));
            } else if (stop instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> stopList = (List<String>) stop;
                builder.stop(stopList);
            }
        }
        
        // 设置用户ID (DeepSeek可能不支持，作为扩展属性保存)
        if (config.containsKey("user")) {
            // DeepSeekChatOptions可能没有user方法，这里记录日志
            log.debug("User setting: {}", config.get("user"));
        }
        
        // 设置种子值 (DeepSeek可能不支持，作为扩展属性保存)
        if (config.containsKey("seed")) {
            Object seed = config.get("seed");
            if (seed instanceof Number) {
                // DeepSeekChatOptions可能没有seed方法，这里记录日志
                log.debug("Seed setting: {}", seed);
            }
        }
        
        // 设置流式响应
        if (config.containsKey("stream")) {
            Object stream = config.get("stream");
            if (stream instanceof Boolean) {
                // DeepSeekChatOptions可能没有直接的stream方法，这里作为示例
                log.debug("Stream setting: {}", stream);
            }
        }
        
        DeepSeekChatOptions options = builder.build();
        log.debug("Built DeepSeekChatOptions: {}", options);
        
        return options;
    }
    
    @Override
    public boolean validateConfig(Map<String, Object> config) {
        if (config == null) {
            log.warn("Config is null");
            return false;
        }
        
        // 验证DeepSeek支持的模型
        if (config.containsKey("model")) {
            String model = (String) config.get("model");
            if (!isValidDeepSeekModel(model)) {
                log.warn("Invalid DeepSeek model: {}", model);
                return false;
            }
        }
        
        // 验证温度范围 [0, 2]
        if (config.containsKey("temperature")) {
            Object temp = config.get("temperature");
            if (temp instanceof Number) {
                double temperature = ((Number) temp).doubleValue();
                if (temperature < 0 || temperature > 2) {
                    log.warn("Invalid temperature value: {} (should be 0-2)", temperature);
                    return false;
                }
            }
        }
        
        // 验证top_p范围 [0, 1]
        if (config.containsKey("topP")) {
            Object topP = config.get("topP");
            if (topP instanceof Number) {
                double topPValue = ((Number) topP).doubleValue();
                if (topPValue < 0 || topPValue > 1) {
                    log.warn("Invalid topP value: {} (should be 0-1)", topPValue);
                    return false;
                }
            }
        }
        
        // 验证最大令牌数 (DeepSeek-Chat最大32K)
        if (config.containsKey("maxTokens")) {
            Object maxTokens = config.get("maxTokens");
            if (maxTokens instanceof Number) {
                int maxTokensValue = ((Number) maxTokens).intValue();
                if (maxTokensValue < 1 || maxTokensValue > 32768) {
                    log.warn("Invalid maxTokens value: {} (should be 1-32768)", maxTokensValue);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 验证是否为有效的DeepSeek模型
     */
    private boolean isValidDeepSeekModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            return false;
        }
        
        return model.equals("deepseek-chat") || 
               model.equals("deepseek-coder") ||
               model.equals("deepseek-reasoner") ||
               model.startsWith("deepseek-");
    }
}
