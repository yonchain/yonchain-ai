/*
package com.yonchain.ai.model.options.deepseek;

import com.yonchain.ai.model.options.ChatOptionsHandler;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.Map;

*/
/**
 * DeepSeek聊天模型选项处理器
 * 
 * DeepSeek使用OpenAI兼容的API，因此使用OpenAiChatOptions
 *//*

public class DeepSeekChatOptionsHandler implements ChatOptionsHandler<OpenAiChatOptions> {


    @Override
    public OpenAiChatOptions buildOptions(Map<String, Object> config) {
        var builder = OpenAiChatOptions.builder();

        // 设置模型 (必需)
*/
/*        if (config.containsKey("model")) {
            builder.model((String) config.get("model"));
        }*//*


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
                builder.stop(java.util.List.of((String) stop));
            } else if (stop instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> stopList = (java.util.List<String>) stop;
                builder.stop(stopList);
            }
        }

        // 设置用户ID
        if (config.containsKey("user")) {
            builder.user((String) config.get("user"));
        }

        // 设置种子值
        if (config.containsKey("seed")) {
            Object seed = config.get("seed");
            if (seed instanceof Number) {
                builder.seed(((Number) seed).intValue());
            }
        }

        // DeepSeek不支持的高级功能会被忽略，但保持API兼容性
        // 例如：logprobs, topLogprobs, logitBias, parallelToolCalls, store等

        return builder.build();
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        // 验证必需的模型参数
        if (!config.containsKey("model")) {
            return false;
        }

        // 验证DeepSeek支持的模型
        String model = (String) config.get("model");
        if (!isValidDeepSeekModel(model)) {
            return false;
        }

        // 验证温度范围 [0, 2]
        if (config.containsKey("temperature")) {
            Object temp = config.get("temperature");
            if (temp instanceof Number) {
                double temperature = ((Number) temp).doubleValue();
                if (temperature < 0 || temperature > 2) {
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
                    return false;
                }
            }
        }

        return true;
    }

    */
/**
     * 验证是否为有效的DeepSeek模型
     *//*

    private boolean isValidDeepSeekModel(String model) {
        return model.equals("deepseek-chat") || 
               model.equals("deepseek-coder") ||
               model.startsWith("deepseek-");
    }
}
*/
