package com.yonchain.ai.model.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.api.model.enums.ProviderType;
import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
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
    public ChatModel getChatModel(ModelEntity model, ModelProviderEntity provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getModelCode() + "-" + provider.getProviderCode();
        return chatModelCache.computeIfAbsent(cacheKey, key -> {
            // 使用枚举类型获取提供商类型
            ProviderType providerType = ProviderType.fromCode(provider.getProviderCode());
            
            if (providerType == null) {
                throw new UnsupportedOperationException("不支持的提供商: " + provider.getProviderCode());
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
    public ChatModel getDeepSeekChatModel(ModelEntity model, ModelProviderEntity provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getModelCode() + "-" + provider.getId();
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
    public ChatModel getAnthropicChatModel(ModelEntity model, ModelProviderEntity provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getModelCode() + "-" + provider.getId();
        return chatModelCache.computeIfAbsent(cacheKey, key -> {
            return createAnthropicChatModel(model, provider);
        });
    }

    // ==================== 模型创建方法实现 ====================

    /**
     * 创建OpenAI聊天模型
     * OpenAI特有的参数配置
     */
    private ChatModel createOpenAiChatModel(ModelEntity model, ModelProviderEntity provider) {
        try {
            // OpenAI可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, frequencyPenalty, presencePenalty等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getModelCode();
            
            // 从provider和model的config中获取OpenAI特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getCustomConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // OpenAI特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Double frequencyPenalty = getConfigValue(modelConfig, providerConfig, "frequencyPenalty", 0.0);
            Double presencePenalty = getConfigValue(modelConfig, providerConfig, "presencePenalty", 0.0);
            
            // 创建OpenAI配置
            log.info("创建OpenAI聊天模型: model={}, apiKey={}, baseUrl={}", 
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);
            
            // 使用自定义的OpenAiChatConfiguration
            com.yonchain.ai.openai.OpenAiChatConfiguration.Builder configBuilder = 
                    com.yonchain.ai.openai.OpenAiChatConfiguration.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.openai.com/v1")
                    .model(modelCode != null ? modelCode : "gpt-3.5-turbo")
                    .temperature(temperature);
            
           /* // 根据配置添加可选参数
            if (maxTokens != null) {
                configBuilder.maxTokens(maxTokens);
            }
            if (topP != null) {
                configBuilder.topP(topP);
            }
            if (frequencyPenalty != null) {
                configBuilder.frequencyPenalty(frequencyPenalty);
            }
            if (presencePenalty != null) {
                configBuilder.presencePenalty(presencePenalty);
            }*/
            
            // 创建OpenAI聊天模型
            com.yonchain.ai.openai.OpenAiChatConfiguration configuration = configBuilder.build();
            return configuration.getOpenAiChatModel();
        } catch (Exception e) {
            log.error("创建OpenAI聊天模型失败", e);
            throw new RuntimeException("创建OpenAI聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建DeepSeek聊天模型
     * DeepSeek特有的参数配置
     */
    private ChatModel createDeepSeekChatModel(ModelEntity model, ModelProviderEntity provider) {
        try {
            // DeepSeek可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, topK等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getModelCode();
            
            // 从provider和model的config中获取DeepSeek特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getCustomConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // DeepSeek特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 50);
            
            // 创建DeepSeek配置
            log.info("创建DeepSeek聊天模型: model={}, apiKey={}, baseUrl={}", 
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);
            baseUrl = providerConfig.containsKey("baseUrl") ? providerConfig.get("baseUrl").toString() : "https://api.deepseek.com";
            apiKey = (String) providerConfig.get("apiKey");
            // 使用DeepSeekChatConfiguration创建DeepSeek聊天模型
            com.yonchain.ai.deepseek.DeepSeekChatConfiguration.Builder configBuilder = 
                    com.yonchain.ai.deepseek.DeepSeekChatConfiguration.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .model(modelCode != null ? modelCode : "deepseek-chat")
                    .temperature(temperature);
            
            // 创建DeepSeek聊天模型
            com.yonchain.ai.deepseek.DeepSeekChatConfiguration configuration = configBuilder.build();
            return configuration.getDeepSeekChatModel();
        } catch (Exception e) {
            log.error("创建DeepSeek聊天模型失败", e);
            throw new RuntimeException("创建DeepSeek聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Anthropic聊天模型
     * Anthropic特有的参数配置
     */
    private ChatModel createAnthropicChatModel(ModelEntity model, ModelProviderEntity provider) {
        try {
            // Anthropic可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, topK, stopSequences等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getModelCode();
            
            // 从provider和model的config中获取Anthropic特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getCustomConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // Anthropic特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 4096);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            
            // 创建Anthropic配置
            log.info("创建Anthropic聊天模型: model={}, apiKey={}, baseUrl={}", 
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);
            
            // 使用自定义的AnthropicChatConfiguration
            com.yonchain.ai.anthropic.AnthropicChatConfiguration.Builder configBuilder = 
                    com.yonchain.ai.anthropic.AnthropicChatConfiguration.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.anthropic.com")
                    .model(modelCode != null ? modelCode : "claude-3-sonnet-20240229")
                    .temperature(temperature);
            
           /* // 根据配置添加可选参数
            if (maxTokens != null) {
                configBuilder.maxTokens(maxTokens);
            }
            if (topP != null) {
                configBuilder.topP(topP);
            }
            */
            // 创建Anthropic聊天模型
            com.yonchain.ai.anthropic.AnthropicChatConfiguration configuration = configBuilder.build();
            return configuration.getAnthropicChatModel();
        } catch (Exception e) {
            log.error("创建Anthropic聊天模型失败", e);
            throw new RuntimeException("创建Anthropic聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Ollama聊天模型
     * Ollama特有的参数配置
     */
    private ChatModel createOllamaChatModel(ModelEntity model, ModelProviderEntity provider) {
        try {
            // Ollama可能需要的参数：baseUrl, model, temperature, numPredict, topP, topK, repeatPenalty等
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getModelCode();
            
            // 从provider和model的config中获取Ollama特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getCustomConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // Ollama特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer numPredict = getConfigValue(modelConfig, providerConfig, "numPredict", 128);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 0.9);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 40);
            Double repeatPenalty = getConfigValue(modelConfig, providerConfig, "repeatPenalty", 1.1);
            
            // 创建Ollama配置
            log.info("创建Ollama聊天模型: model={}, baseUrl={}", modelCode, baseUrl);
            
            // 使用自定义的Ollama客户端实现
            // 由于项目中没有OllamaChatConfiguration类，我们需要创建一个简单的实现
            // 这里使用一个临时的适配器模式实现
            /*class OllamaChatAdapter implements ChatModel {
                private final String baseUrl;
                private final String modelName;
                private final Map<String, Object> parameters;
                
                public OllamaChatAdapter(String baseUrl, String modelName, Map<String, Object> parameters) {
                    this.baseUrl = baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "http://localhost:11434";
                    this.modelName = modelName != null ? modelName : "llama2";
                    this.parameters = parameters;
                }
                
                @Override
                public org.springframework.ai.chat.ChatResponse call(org.springframework.ai.chat.prompt.ChatPromptTemplate promptTemplate) {
                    // 这里需要实现实际的调用逻辑
                    log.warn("Ollama模型调用尚未实现，请创建OllamaChatConfiguration类");
                    throw new UnsupportedOperationException("Ollama模型调用尚未实现，请创建OllamaChatConfiguration类");
                }
                
                @Override
                public org.springframework.ai.chat.ChatResponse call(org.springframework.ai.chat.prompt.Prompt prompt) {
                    // 这里需要实现实际的调用逻辑
                    log.warn("Ollama模型调用尚未实现，请创建OllamaChatConfiguration类");
                    throw new UnsupportedOperationException("Ollama模型调用尚未实现，请创建OllamaChatConfiguration类");
                }
            }*/
            
            // 创建参数Map
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", temperature);
            parameters.put("num_predict", numPredict);
            parameters.put("top_p", topP);
            parameters.put("top_k", topK);
            parameters.put("repeat_penalty", repeatPenalty);
            
            return null;//new OllamaChatAdapter(baseUrl, modelCode, parameters);
        } catch (Exception e) {
            log.error("创建Ollama聊天模型失败", e);
            throw new RuntimeException("创建Ollama聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Grok聊天模型
     * Grok特有的参数配置
     */
    private ChatModel createGrokChatModel(ModelEntity model, ModelProviderEntity provider) {
        try {
            // Grok可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens等
            String apiKey = provider.getApiKey();
            String baseUrl = provider.getBaseUrl();
            String modelCode = model.getModelCode();
            
            // 从provider和model的config中获取Grok特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getCustomConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // Grok特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            
            // 创建Grok配置
            log.info("创建Grok聊天模型: model={}, apiKey={}, baseUrl={}", 
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);
            
            // 使用自定义的Grok客户端实现
            // 由于项目中没有GrokChatConfiguration类，我们需要创建一个简单的实现
            // 这里使用一个临时的适配器模式实现
        /*    class GrokChatAdapter implements ChatModel {
                private final String apiKey;
                private final String baseUrl;
                private final String modelName;
                private final Map<String, Object> parameters;
                
                public GrokChatAdapter(String apiKey, String baseUrl, String modelName, Map<String, Object> parameters) {
                    this.apiKey = apiKey;
                    this.baseUrl = baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.grok.ai/v1";
                    this.modelName = modelName != null ? modelName : "grok-1";
                    this.parameters = parameters;
                }
                
                @Override
                public org.springframework.ai.chat.ChatResponse call(org.springframework.ai.chat.prompt.ChatPromptTemplate promptTemplate) {
                    // 这里需要实现实际的调用逻辑
                    log.warn("Grok模型调用尚未实现，请创建GrokChatConfiguration类");
                    throw new UnsupportedOperationException("Grok模型调用尚未实现，请创建GrokChatConfiguration类");
                }
                
                @Override
                public org.springframework.ai.chat.ChatResponse call(org.springframework.ai.chat.prompt.Prompt prompt) {
                    // 这里需要实现实际的调用逻辑
                    log.warn("Grok模型调用尚未实现，请创建GrokChatConfiguration类");
                    throw new UnsupportedOperationException("Grok模型调用尚未实现，请创建GrokChatConfiguration类");
                }
            }*/
            
            // 创建参数Map
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", temperature);
            parameters.put("max_tokens", maxTokens);
            
            return null;//new GrokChatAdapter(apiKey, baseUrl, modelCode, parameters);
        } catch (Exception e) {
            log.error("创建Grok聊天模型失败", e);
            throw new RuntimeException("创建Grok聊天模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建百度文心一言聊天模型
     */
    private ChatModel createBaiduChatModel(ModelEntity model, ModelProviderEntity provider) {
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
    private ChatModel createAlibabaChatModel(ModelEntity model, ModelProviderEntity provider) {
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
    private ChatModel createTencentChatModel(ModelEntity model, ModelProviderEntity provider) {
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
    private ChatModel createZhipuChatModel(ModelEntity model, ModelProviderEntity provider) {
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
    private ChatModel createMoonshotChatModel(ModelEntity model, ModelProviderEntity provider) {
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