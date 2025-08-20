package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI模型客户端工厂
 * 用于创建和缓存不同模型的聊天客户端实例
 */
@Component
public class AIModelClientFactory {

    @Autowired
    private ModelApiFactory modelApiFactory;
    
    @Autowired
    private ModelFactory modelFactory;

    /**
     * 缓存已创建的聊天客户端
     * key: 模型代码-提供商ID
     */
    private final Map<String, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    /**
     * 获取聊天客户端
     * 如果缓存中不存在，则创建新的客户端
     *
     * @param model 模型信息
     * @param provider 提供商信息
     * @return 聊天客户端
     */
    public ChatClient getChatClient(AIModel model, ModelProvider provider) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getCode() + "-" + provider.getId();
        return chatClientCache.computeIfAbsent(cacheKey, key -> {
            // 根据提供商类型创建不同的聊天客户端
            if ("openai".equals(provider.getCode())) {
                // 从API工厂获取OpenAI API客户端
                OpenAiApi api = modelApiFactory.getOpenAiApi(provider);
                
                // 从模型工厂获取ChatModel
                ChatModel chatModel = modelFactory.getOpenAiChatModel(model, provider, api);
                
                // 创建聊天客户端
                return ChatClient.create(chatModel);
            }
            // 可以在这里添加其他提供商的支持
            
            throw new UnsupportedOperationException("不支持的提供商: " + provider.getCode());
        });
    }

    /**
     * 清除指定模型和提供商的聊天客户端缓存
     *
     * @param modelCode 模型代码
     * @param providerId 提供商ID
     */
    public void clearChatClientCache(String modelCode, Long providerId) {
        chatClientCache.remove(modelCode + "-" + providerId);
    }

    /**
     * 清除指定模型的所有聊天客户端缓存
     *
     * @param modelCode 模型代码
     */
    public void clearModelChatClientCache(String modelCode) {
        chatClientCache.entrySet().removeIf(entry -> entry.getKey().startsWith(modelCode + "-"));
    }

    /**
     * 清除指定提供商的所有聊天客户端缓存
     *
     * @param providerId 提供商ID
     */
    public void clearProviderChatClientCache(Long providerId) {
        chatClientCache.entrySet().removeIf(entry -> entry.getKey().endsWith("-" + providerId));
    }

    /**
     * 清除所有聊天客户端缓存
     */
    public void clearAllCache() {
        chatClientCache.clear();
    }
}
