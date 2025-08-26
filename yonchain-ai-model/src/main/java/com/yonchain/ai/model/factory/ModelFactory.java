package com.yonchain.ai.model.factory;

import com.yonchain.ai.api.model.ModelConfigItem;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;
import com.yonchain.ai.deepseek.DeepSeekChatConfiguration;
import com.yonchain.ai.model.registry.ModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 模型工厂类
 * 负责创建和管理模型实例，整合了 ModelConnectionPool 的功能
 * 所有配置信息从 ModelRegistry 获取
 */
public class ModelFactory {

    private static final Logger log = LoggerFactory.getLogger(ModelFactory.class);

    private final ModelRegistry modelRegistry;

    // 模型实例缓存
    private final Map<String, ChatModel> modelCache = new ConcurrentHashMap<>();

    // 模型提供器函数缓存
    private final Map<String, Function<String, ChatModel>> providerCache = new ConcurrentHashMap<>();

    // 提供商信息缓存
    private final Map<String, ModelProvider> providerInfoCache = new ConcurrentHashMap<>();

    public ModelFactory(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
    }

    /**
     * 获取或创建模型实例
     */
    public ChatModel getModel(String modelId) {
        return modelCache.computeIfAbsent(modelId, this::createModel);
    }

    /**
     * 获取模型提供器函数
     */
    public Function<String, ChatModel> getModelProvider(String providerId) {
        return providerCache.computeIfAbsent(providerId, this::createModelProvider);
    }

    /**
     * 清除指定模型的缓存
     */
    public void invalidateModel(String modelId) {
        modelCache.remove(modelId);
        log.info("已清除模型缓存: {}", modelId);
    }

    /**
     * 清除指定提供商的缓存
     */
    public void invalidateProvider(String providerCode) {
        providerInfoCache.remove(providerCode);
        log.info("已清除提供商缓存: {}", providerCode);
    }

    /**
     * 清除所有模型缓存
     */
    public void clearAllModels() {
        modelCache.clear();
        providerCache.clear();
        providerInfoCache.clear();
        log.info("已清除所有模型缓存");
    }

    /**
     * 获取已缓存模型的数量
     */
    public int getCachedModelCount() {
        return modelCache.size();
    }

    /**
     * 创建模型实例
     */
    private ChatModel createModel(String modelId) {
        try {
            log.info("开始创建模型实例: {}", modelId);

            // 从注册表获取模型信息
            ModelInfo model = modelRegistry.getModel(modelId);
            if (model == null) {
                throw new IllegalArgumentException("未找到模型配置: " + modelId);
            }

            // 获取提供商信息
            String providerCode = model.getProvider();
            if (providerCode == null) {
                throw new IllegalArgumentException("模型未指定提供商: " + modelId);
            }

            // 从注册表获取提供商信息
            ModelProvider provider = getProviderFromRegistry(providerCode);
            if (provider == null) {
                throw new IllegalArgumentException("未找到提供商配置: " + providerCode);
            }

            // 根据提供商类型创建对应的模型
            ChatModel chatModel = createChatModelByProvider(providerCode, model, provider);

            log.info("成功创建模型实例: {}", modelId);
            return chatModel;

        } catch (Exception e) {
            log.error("创建模型实例失败: {}", modelId, e);
            throw new RuntimeException("创建模型实例失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从注册表获取提供商信息（带缓存优化）
     */
    private ModelProvider getProviderFromRegistry(String providerCode) {
        return providerInfoCache.computeIfAbsent(providerCode, code -> {
            List<ModelProvider> providers = modelRegistry.getProviders();
            for (ModelProvider provider : providers) {
                if (code.equals(provider.getCode())) {
                    return provider;
                }
            }
            return null;
        });
    }

    /**
     * 根据提供商类型创建聊天模型
     */
    private ChatModel createChatModelByProvider(String providerCode, ModelInfo model, ModelProvider provider) {
        switch (providerCode.toLowerCase()) {
            case "openai":
                return createOpenAiChatModel(model, provider);
            case "deepseek":
                return createDeepSeekChatModel(model, provider);
            case "anthropic":
                return createAnthropicChatModel(model, provider);
            case "ollama":
                return createOllamaChatModel(model, provider);
            case "grok":
                return createGrokChatModel(model, provider);
            default:
                throw new IllegalArgumentException("不支持的模型提供商: " + providerCode);
        }
    }

    /**
     * 创建模型提供器函数
     */
    private Function<String, ChatModel> createModelProvider(String providerId) {
        return modelId -> {
            try {
                return createModel(modelId);
            } catch (Exception e) {
                log.error("通过提供器创建模型失败: providerId={}, modelId={}", providerId, modelId, e);
                throw new RuntimeException("通过提供器创建模型失败: " + e.getMessage(), e);
            }
        };
    }

    /**
     * 创建OpenAI聊天模型
     * OpenAI特有的参数配置
     */
    private ChatModel createOpenAiChatModel(ModelInfo model, ModelProvider provider) {
      /*  try {
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取OpenAI特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getProviderConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // OpenAI可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, frequencyPenalty, presencePenalty等
            String apiKey = getConfigValue(providerConfig, modelConfig, "apiKey", (String) null);
            String baseUrl = getConfigValue(providerConfig, modelConfig, "baseUrl", (String) null);
            
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
            OpenAiChatConfiguration.Builder configBuilder = 
                    OpenAiChatConfiguration.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.openai.com/v1")
                    .model(modelCode != null ? modelCode : "gpt-3.5-turbo")
                    .temperature(temperature);
            
            // 创建OpenAI聊天模型
            OpenAiChatConfiguration configuration = configBuilder.build();
            return configuration.getOpenAiChatModel();
            
        } catch (Exception e) {
            log.error("创建OpenAI聊天模型失败", e);
            throw new RuntimeException("创建OpenAI聊天模型失败: " + e.getMessage(), e);
        }*/
        return null;
    }

    /**
     * 创建DeepSeek聊天模型
     * DeepSeek特有的参数配置
     */
    private ChatModel createDeepSeekChatModel(ModelInfo model, ModelProvider provider) {
        try {
            String modelCode = model.getCode();

            // 从provider和model的config中获取DeepSeek特有的参数
            List<ModelConfigItem> providerConfigSchemas = provider.getConfigSchemas();
            List<ModelConfigItem> modelConfigItems = model.getConfigSchemas();

            // DeepSeek可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, topK等
            String apiKey = "";
            String baseUrl = "";

            for (ModelConfigItem item : providerConfigSchemas) {
                if (item.getName().equals("apiKey")) {
                    apiKey = item.getValue().toString();
                }
                if (item.getName().equals("baseUrl")) {
                    baseUrl = item.getValue().toString();
                }
            }

            // DeepSeek特有参数
          /*  Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 50);*/

            // 创建DeepSeek配置
            log.info("创建DeepSeek聊天模型: model={}, apiKey={}, baseUrl={}",
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);

            // 使用自定义的DeepSeekChatConfiguration
            DeepSeekChatConfiguration.Builder configBuilder =
                    DeepSeekChatConfiguration.builder()
                            .apiKey(apiKey)
                            .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.deepseek.com/v1")
                            .model(modelCode != null ? modelCode : "deepseek-chat");
            //  .temperature(temperature);

            // 创建DeepSeek聊天模型
            DeepSeekChatConfiguration configuration = configBuilder.build();
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
    private ChatModel createAnthropicChatModel(ModelInfo model, ModelProvider provider) {
       /* try {
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取Anthropic特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getProviderConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // Anthropic可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens, topP, topK, stopSequences等
            String apiKey = getConfigValue(providerConfig, modelConfig, "apiKey", (String) null);
            String baseUrl = getConfigValue(providerConfig, modelConfig, "baseUrl", (String) null);
            
            // Anthropic特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 50);
            
            // 创建Anthropic配置
            log.info("创建Anthropic聊天模型: model={}, apiKey={}, baseUrl={}", 
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);
            
            // 使用自定义的AnthropicChatConfiguration
            com.yonchain.ai.anthropic.AnthropicChatConfiguration.Builder configBuilder = 
                    com.yonchain.ai.anthropic.AnthropicChatConfiguration.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.anthropic.com/v1")
                    .model(modelCode != null ? modelCode : "claude-3-sonnet-20240229")
                    .temperature(temperature);
            
            // 创建Anthropic聊天模型
            com.yonchain.ai.anthropic.AnthropicChatConfiguration configuration = configBuilder.build();
            return configuration.getAnthropicChatModel();
            
        } catch (Exception e) {
            log.error("创建Anthropic聊天模型失败", e);
            throw new RuntimeException("创建Anthropic聊天模型失败: " + e.getMessage(), e);
        }*/
        return null;
    }

    /**
     * 创建Ollama聊天模型
     * Ollama特有的参数配置
     */
    private ChatModel createOllamaChatModel(ModelInfo model, ModelProvider provider) {
        /*try {
            String modelCode = model.getCode();

            // Ollama可能需要的参数：baseUrl, model, temperature, numPredict, topP, topK, repeatPenalty等
            String baseUrl = getConfigValue(providerConfig, modelConfig, "baseUrl", (String) null);
            
            // Ollama特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer numPredict = getConfigValue(modelConfig, providerConfig, "numPredict", 2048);
            Double topP = getConfigValue(modelConfig, providerConfig, "topP", 1.0);
            Integer topK = getConfigValue(modelConfig, providerConfig, "topK", 50);
            Double repeatPenalty = getConfigValue(modelConfig, providerConfig, "repeatPenalty", 1.0);
            
            // 创建Ollama配置
            log.info("创建Ollama聊天模型: model={}, baseUrl={}", modelCode, baseUrl);
            
            // 使用自定义的OllamaChatConfiguration
            com.yonchain.ai.ollama.OllamaChatConfiguration.Builder configBuilder = 
                    com.yonchain.ai.ollama.OllamaChatConfiguration.builder()
                    .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "http://localhost:11434")
                    .model(modelCode != null ? modelCode : "llama2")
                    .temperature(temperature);
            
            // 创建Ollama聊天模型
            com.yonchain.ai.ollama.OllamaChatConfiguration configuration = configBuilder.build();
            return configuration.getOllamaChatModel();
            
        } catch (Exception e) {
            log.error("创建Ollama聊天模型失败", e);
            throw new RuntimeException("创建Ollama聊天模型失败: " + e.getMessage(), e);
        }*/
        return null;
    }

    /**
     * 创建Grok聊天模型
     * Grok特有的参数配置
     */
    private ChatModel createGrokChatModel(ModelInfo model, ModelProvider provider) {
        /*try {
            String modelCode = model.getCode();
            
            // 从provider和model的config中获取Grok特有的参数
            Map<String, Object> providerConfig = parseConfigToMap(provider.getProviderConfig());
            Map<String, Object> modelConfig = parseConfigToMap(model.getModelConfig());
            
            // Grok可能需要的参数：apiKey, baseUrl, model, temperature, maxTokens等
            String apiKey = getConfigValue(providerConfig, modelConfig, "apiKey", (String) null);
            String baseUrl = getConfigValue(providerConfig, modelConfig, "baseUrl", (String) null);
            
            // Grok特有参数
            Double temperature = getConfigValue(modelConfig, providerConfig, "temperature", 0.7);
            Integer maxTokens = getConfigValue(modelConfig, providerConfig, "maxTokens", 2048);
            
            // 创建Grok配置
            log.info("创建Grok聊天模型: model={}, apiKey={}, baseUrl={}", 
                    modelCode, apiKey != null ? "已配置" : "未配置", baseUrl);
            
            // 使用自定义的GrokChatConfiguration
            com.yonchain.ai.grok.GrokChatConfiguration.Builder configBuilder = 
                    com.yonchain.ai.grok.GrokChatConfiguration.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl != null && !baseUrl.isEmpty() ? baseUrl : "https://api.x.ai/v1")
                    .model(modelCode != null ? modelCode : "grok-beta")
                    .temperature(temperature);
            
            // 创建Grok聊天模型
            com.yonchain.ai.grok.GrokChatConfiguration configuration = configBuilder.build();
            return configuration.getGrokChatModel();
            
        } catch (Exception e) {
            log.error("创建Grok聊天模型失败", e);
            throw new RuntimeException("创建Grok聊天模型失败: " + e.getMessage(), e);
        }*/
        return null;
    }


/*    *//**
     * 从配置中获取值，支持多个配置源的优先级
     *//*
    @SuppressWarnings("unchecked")
    private <T> T getConfigValue(Map<String, Object> primaryConfig, Map<String, Object> secondaryConfig, String key, T defaultValue) {
        Object value = primaryConfig.get(key);
        if (value == null && secondaryConfig != null) {
            value = secondaryConfig.get(key);
        }
        return value != null ? (T) value : defaultValue;
    }*/
}