package com.yonchain.ai.model.options.openai;

import com.yonchain.ai.model.options.ModelOptionsHandler;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.Map;

/**
 * OpenAI聊天模型选项处理器
 */
public class OpenAiChatOptionsHandler implements ModelOptionsHandler<OpenAiChatOptions> {


    @Override
    public OpenAiChatOptions buildOptions(Map<String, Object> config) {
        var builder = OpenAiChatOptions.builder();

        // 设置模型 (必需)
        if (config.containsKey("model")) {
            builder.model((String) config.get("model"));
        }

        // 设置温度 (0-2.0)
        if (config.containsKey("temperature")) {
            Object temp = config.get("temperature");
            if (temp instanceof Number) {
                builder.temperature(((Number) temp).doubleValue());
            }
        }

        // 设置最大令牌数
        if (config.containsKey("maxTokens")) {
            Object maxTokens = config.get("maxTokens");
            if (maxTokens instanceof Number) {
                builder.maxTokens(((Number) maxTokens).intValue());
            }
        }

        // 设置最大完成令牌数
        if (config.containsKey("maxCompletionTokens")) {
            Object maxCompletionTokens = config.get("maxCompletionTokens");
            if (maxCompletionTokens instanceof Number) {
                builder.maxCompletionTokens(((Number) maxCompletionTokens).intValue());
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

        // 设置生成数量
        if (config.containsKey("n")) {
            Object n = config.get("n");
            if (n instanceof Number) {
                builder.N(((Number) n).intValue());
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

        // 设置logprobs
        if (config.containsKey("logprobs")) {
            Object logprobs = config.get("logprobs");
            if (logprobs instanceof Boolean) {
                builder.logprobs((Boolean) logprobs);
            }
        }

        // 设置topLogprobs
        if (config.containsKey("topLogprobs")) {
            Object topLogprobs = config.get("topLogprobs");
            if (topLogprobs instanceof Number) {
                builder.topLogprobs(((Number) topLogprobs).intValue());
            }
        }

        // 设置logit偏置
        if (config.containsKey("logitBias")) {
            Object logitBias = config.get("logitBias");
            if (logitBias instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Integer> biasMap = (Map<String, Integer>) logitBias;
                builder.logitBias(biasMap);
            }
        }

        // 设置并行工具调用
        if (config.containsKey("parallelToolCalls")) {
            Object parallelToolCalls = config.get("parallelToolCalls");
            if (parallelToolCalls instanceof Boolean) {
                builder.parallelToolCalls((Boolean) parallelToolCalls);
            }
        }

        // 设置store选项
        if (config.containsKey("store")) {
            Object store = config.get("store");
            if (store instanceof Boolean) {
                builder.store((Boolean) store);
            }
        }

        // 设置推理努力程度（仅适用于o1模型）
        if (config.containsKey("reasoningEffort")) {
            builder.reasoningEffort((String) config.get("reasoningEffort"));
        }

        // 设置元数据
        if (config.containsKey("metadata")) {
            Object metadata = config.get("metadata");
            if (metadata instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> metadataMap = (Map<String, String>) metadata;
                builder.metadata(metadataMap);
            }
        }

        // 设置输出模态（仅适用于支持的模型如gpt-4o-audio-preview）
        if (config.containsKey("outputModalities")) {
            Object modalities = config.get("outputModalities");
            if (modalities instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> modalitiesList = (java.util.List<String>) modalities;
                builder.outputModalities(modalitiesList);
            }
        }

        return builder.build();
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        // 验证必需的模型参数
        if (!config.containsKey("model")) {
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

        return true;
    }

}
