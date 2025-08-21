package com.yonchain.ai.model.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.api.model.enums.ProviderType;
import com.yonchain.ai.model.entity.AiModel;
import com.yonchain.ai.model.entity.ModelProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型工厂
 * 用于创建和缓存不同模型的ChatModel实例
 * 使用简单的条件判断替代策略模式，提供更好的性能和可维护性
 */
@Slf4j
@Component
public class ModelFactory {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 缓存已创建的ChatModel实例
     * key: 模型代码-提供商ID
     */
    private final Map<String, ChatModel> chatModelCache = new ConcurrentHashMap<>();

    public ModelFactory() {
        // 不再需要初始化策略映射
    }

    /**
     * 获取聊天模型
     * 根据提供商类型直接调用对应的创建方法
     *
     * @param model    模型信息
     * @param provider 提供商信息
     * @return 聊天模型
     */
    public ChatModel getChatModel(AiModel model, ModelProvider provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getCode() + "-" + provider.getId();
        return chatModelCache.computeIfAbsent(cacheKey, key -> {
            // 使用枚举类型获取提供商类型
            ProviderType providerType = ProviderType.fromCode(provider.getCode());
            
            if (providerType == null) {
                throw new UnsupportedOperationException("不支持的提供商: " + provider.getCode());
            }
            
            // 直接使用switch语句调用对应的创建方法
            switch (providerType) {
                case OPENAI:
                    return createOpenAiChatModel(model, provider);
                case DEEPSEEK:
                    return createDeepSeekChatModel(model, provider);
                case ANTHROPIC:
                    return createAnthropicChatModel(model, provider);
                case OLLAMA:
                    return createOllamaChatModel(model, provider);
                case GROK:
                    return createGrokChatModel(model, provider);
                case BAIDU:
                    return createBaiduChatModel(model, provider);
                case ALIBABA:
                    return createAlibabaChatModel(model, provider);
                case TENCENT:
                    return createTencentChatModel(model, provider);
                case ZHIPU:
                    return createZhipuChatModel(model, provider);
                case MOONSHOT:
                    return createMoonshotChatModel(model, provider);
                default:
                    throw new UnsupportedOperationException("提供商暂未实现: " + providerType.getDisplayName());
            }
        });
    }

    /**
     * 获取DeepSeek聊天模型
     * 如果缓存中不存在，则创建新的模型
     *
     * @param model     模型信息
     * @param provider  提供商信息
     * @return DeepSeek聊天模型
     */
    public ChatModel getDeepSeekChatModel(AiModel model, ModelProvider provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getCode() + "-" + provider.getId();
        return chatModelCache.computeIfAbsent(cacheKey, key -> {
            return createDeepSeekChatModel(model, provider);
        });
    }

    /**
     * 获取Anthropic聊天模型
     * 如果缓存中不存在，则创建新的模型
     *
     * @param model     模型信息
     * @param provider  提供商信息
     * @return Anthropic聊天模型
     */
    public ChatModel getAnthropicChatModel(AiModel model, ModelProvider provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getCode() + "-" + provider.getId();
        return chatModelCache.computeIfAbsent(cacheKey, key -> {
            return createAnthropicChatModel(model, provider);
        });
    }

    // ==================== 模型创建方法实现 ====================

    /**
     * 创建OpenAI聊天模型
     * OpenAI特有的参数配置
     */
    private ChatModel createOpenAiChatModel(AiModel model, ModelProvider provider) {
        try {
            // OpenAI可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, frequencyPenalty, presencePenalty等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取OpenAI特有的参数
            // 从provider和model的config中获取DeepSeek特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getConfig());
            
            // OpenAI特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Double frequencyPenalty = getConfigValue(modelConfig, providerConfig, "frequencyPenalty", 0.0);
            Double presencePenalty = getConfigValue(modelConfig, providerConfig, "presencePenalty", 0.0);
            
            // TODO: 实现OpenAI模型创建，需要引入OpenAI配置类
            // OpenAiChatConfiguration configuration = 
            //         OpenAiChatConfiguration.builder()
            //                 .apiKey(apiKey)
            //                 .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.openai.com")
            //                 .model(modelCode != null ? modelCode : "gpt-3.5-turbo")
            //                 .temperature(temperature)
            //                 .maxTokens(maxTokens)
            //                 .topP(topP)
            //                 .frequencyPenalty(frequencyPenalty)
            //                 .presencePenalty(presencePenalty)
            //                 .build();
            // 
            // return configuration.getOpenAiChatModel();
            throw new UnsupportedOperationException("OpenAI模型创建暂未实现，需要完善OpenAI配置类");
        } catch (Exception e) {
            throw new RuntimeException("创建OpenAI聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建DeepSeek聊天模型
     * DeepSeek特有的参数配置
     */
    private ChatModel createDeepSeekChatModel(AiModel model, ModelProvider provider) {
        try {
            // DeepSeek可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, topK等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取DeepSeek特有的参数
            // 从provider和model的config中获取Anthropic特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getConfig());
            
            // DeepSeek特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 50);
            
            // TODO: 需要引入DeepSeek配置类
            // DeepSeekChatConfiguration.Builder configBuilder = DeepSeekChatConfiguration.builder()
            //         .apiKey(apiKey)
            //         .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.deepseek.com")
            //         .model(modelCode != null ? modelCode : "deepseek-chat")
            //         .temperature(temperature);
            // 
            // // 根据配置添加可选参数
            // if (maxTokens != null) {
            //     configBuilder.maxTokens(maxTokens);
            // }
            // if (topP != null) {
            //     configBuilder.topP(topP);
            // }
            // 
            // return configBuilder.build().getDeepSeekChatModel();
            throw new UnsupportedOperationException("DeepSeek模型创建暂未实现，需要引入DeepSeek配置类");
        } catch (Exception e) {
            throw new RuntimeException("创建DeepSeek聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Anthropic聊天模型
     * Anthropic特有的参数配置
     */
    private ChatModel createAnthropicChatModel(AiModel model, ModelProvider provider) {
        try {
            // Anthropic可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, topK, stopSequences等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取Anthropic特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getConfig());
            
            // Anthropic特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 50);
            
            // TODO: 需要引入Anthropic配置类
            // AnthropicChatConfiguration.Builder configBuilder = AnthropicChatConfiguration.builder()
            //         .apiKey(apiKey)
            //         .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.anthropic.com")
            //         .model(modelCode != null ? modelCode : "claude-3-sonnet-20240229")
            //         .temperature(temperature);
            // 
            // // 根据配置添加可选参数
            // if (maxTokens != null) {
            //     configBuilder.maxTokens(maxTokens);
            // }
            // if (topP != null) {
            //     configBuilder.topP(topP);
            // }
            // 
            // return configBuilder.build().getAnthropicChatModel();
            throw new UnsupportedOperationException("Anthropic模型创建暂未实现，需要引入Anthropic配置类");
        } catch (Exception e) {
            throw new RuntimeException("创建Anthropic聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Ollama聊天模型
     * Ollama特有的参数配置
     */
    private ChatModel createOllamaChatModel(AiModel model, ModelProvider provider) {
        try {
            // Ollama可能需要的参数：baseUrl, model, temperature, numPredict, topP, topK, repeatPenalty等
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取Ollama特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getConfig());
            
            // Ollama特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer numPredict = getConfigValue(modelConfig, providerConfig, "numPredict", 128);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 0.9);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 40);
            Double repeatPenalty = getConfigValue(modelConfig, providerConfig, "repeatPenalty", 1.1);
            
            // TODO: 实现Ollama模型创建
            throw new UnsupportedOperationException("Ollama模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建Ollama聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Grok聊天模型
     * Grok特有的参数配置
     */
    private ChatModel createGrokChatModel(AiModel model, ModelProvider provider) {
        try {
            // Grok可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取Grok特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getConfig());
            
            // Grok特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            
            // TODO: 实现Grok模型创建
            throw new UnsupportedOperationException("Grok模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建Grok聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建百度文心一言聊天模型
     */
    private ChatModel createBaiduChatModel(AiModel model, ModelProvider provider) {
        try {
            // TODO: 实现百度文心一言模型创建
            throw new UnsupportedOperationException("百度文心一言模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建百度文心一言聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建阿里通义千问聊天模型
     */
    private ChatModel createAlibabaChatModel(AiModel model, ModelProvider provider) {
        try {
            // TODO: 实现阿里通义千问模型创建
            throw new UnsupportedOperationException("阿里通义千问模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建阿里通义千问聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建腾讯混元聊天模型
     */
    private ChatModel createTencentChatModel(AiModel model, ModelProvider provider) {
        try {
            // TODO: 实现腾讯混元模型创建
            throw new UnsupportedOperationException("腾讯混元模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建腾讯混元聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建智谱AI聊天模型
     */
    private ChatModel createZhipuChatModel(AiModel model, ModelProvider provider) {
        try {
            // TODO: 实现智谱AI模型创建
            throw new UnsupportedOperationException("智谱AI模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建智谱AI聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建月之暗面Kimi聊天模型
     */
    private ChatModel createMoonshotChatModel(AiModel model, ModelProvider provider) {
        try {
            // TODO: 实现月之暗面Kimi模型创建
            throw new UnsupportedOperationException("月之暗面Kimi模型创建暂未实现");
        } catch (Exception e) {
            throw new RuntimeException("创建月之暗面Kimi聊天模型失败: " + e.getMessage(), e);
        }
    }

    // ==================== 辅助方法 ====================
    // ==================== 辅助方法 ====================

    /**
     * 将JSON字符串配置解析为Map
     */
    private Map<String, Object> parseConfigToMap(String configJson) {
        if (!StringUtils.hasText(configJson)) {
            return new ConcurrentHashMap<>();
        }
        
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("解析配置JSON失败: {}, 配置内容: {}", e.getMessage(), configJson);
            return new ConcurrentHashMap<>();
        }
    }

    /**
     * 从配置中获取参数值，支持类型转换和默认值
     * 优先级：模型配置 > 提供商配置 > 默认值
     */
    @SuppressWarnings("unchecked")
    private <T> T getConfigValue(Map<String, Object> modelConfig, Map<String, Object> providerConfig, 
                                String key, T defaultValue) {
        try {
            // 优先从模型配置中获取
            if (modelConfig != null && modelConfig.containsKey(key)) {
                Object value = modelConfig.get(key);
                if (value != null) {
                    return convertValue(value, defaultValue);
                }
            }
            
            // 其次从提供商配置中获取
            if (providerConfig != null && providerConfig.containsKey(key)) {
                Object value = providerConfig.get(key);
                if (value != null) {
                    return convertValue(value, defaultValue);
                }
            }
            
            // 返回默认值
            return defaultValue;
        } catch (Exception e) {
            // 转换失败时返回默认值
            return defaultValue;
        }
    }

    /**
     * 类型转换辅助方法
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        
        Class<?> targetType = defaultValue.getClass();
        
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        
        // 字符串转换
        String strValue = value.toString();
        
        if (targetType == Double.class) {
            return (T) Double.valueOf(strValue);
        } else if (targetType == Integer.class) {
            return (T) Integer.valueOf(strValue);
        } else if (targetType == Float.class) {
            return (T) Float.valueOf(strValue);
        } else if (targetType == Boolean.class) {
            return (T) Boolean.valueOf(strValue);
        }
        
        return (T) value;
    }

    // ==================== 缓存管理方法 ====================

    /**
     * 清除指定模型和提供商的聊天模型缓存
     *
     * @param modelCode  模型代码
     * @param providerId 提供商ID
     */
    public void clearChatModelCache(String modelCode, Long providerId) {
        chatModelCache.remove(modelCode + "-" + providerId);
    }

    /**
     * 清除指定模型的所有聊天模型缓存
     *
     * @param modelCode 模型代码
     */
    public void clearModelChatModelCache(String modelCode) {
        chatModelCache.entrySet().removeIf(entry -> entry.getKey().startsWith(modelCode + "-"));
    }

    /**
     * 清除指定提供商的所有聊天模型缓存
     *
     * @param providerId 提供商ID
     */
    public void clearProviderChatModelCache(Long providerId) {
        chatModelCache.entrySet().removeIf(entry -> entry.getKey().endsWith("-" + providerId));
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        chatModelCache.clear();
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("cacheSize", chatModelCache.size());
        stats.put("cachedModels", chatModelCache.keySet());
        return stats;
    }
}