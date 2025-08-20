package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.client.tool.ToolCallingManager;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型工厂
 * 用于创建和缓存不同模型的ChatModel实例
 */
@Component
public class ModelFactory {

    /**
     * 缓存已创建的ChatModel实例
     * key: 模型代码-提供商ID
     */
    private final Map<String, ChatModel> chatModelCache = new ConcurrentHashMap<>();

    /**
     * 获取OpenAI聊天模型
     * 如果缓存中不存在，则创建新的模型
     *
     * @param model 模型信息
     * @param provider 提供商信息
     * @param openAiApi OpenAI API客户端
     * @return OpenAI聊天模型
     */
    public OpenAiChatModel getOpenAiChatModel(AIModel model, ModelProvider provider, OpenAiApi openAiApi) {
        if (model == null || provider == null || openAiApi == null) {
            throw new IllegalArgumentException("模型、提供商或API客户端不能为空");
        }

        String cacheKey = model.getCode() + "-" + provider.getId();
        return (OpenAiChatModel) chatModelCache.computeIfAbsent(cacheKey, key -> {
            // 创建聊天选项
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(model.getCode())
                .withTemperature(0.7f) // 默认温度
                .withMaxTokens(2048) // 默认最大令牌数
                .build();
            
            // 创建并返回聊天模型
            // 使用Spring AI 1.0.0版本的OpenAiChatModel构造方法
            return new OpenAiChatModel(openAiApi, options, 
                ToolCallingManager.builder().build(), 
                RetryTemplate.defaultInstance(), 
                null);
        });
    }

    /**
     * 清除指定模型和提供商的聊天模型缓存
     *
     * @param modelCode 模型代码
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
}
