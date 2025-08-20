package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.entity.ModelProvider;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型API工厂
 * 用于创建和缓存不同模型提供商的API客户端
 */
@Component
public class ModelApiFactory {

    /**
     * 缓存已创建的OpenAI API客户端
     * key: 提供商代码-提供商ID
     */
    private final Map<String, OpenAiApi> openAiApiCache = new ConcurrentHashMap<>();

    /**
     * 获取OpenAI API客户端
     * 如果缓存中不存在，则创建新的客户端
     *
     * @param provider 提供商信息
     * @return OpenAI API客户端
     */
    public OpenAiApi getOpenAiApi(ModelProvider provider) {
        if (provider == null || !"openai".equals(provider.getCode())) {
            throw new IllegalArgumentException("不支持的提供商: " + (provider != null ? provider.getCode() : "null"));
        }

        String cacheKey = provider.getCode() + "-" + provider.getId();
        return openAiApiCache.computeIfAbsent(cacheKey, key -> {
            // 创建新的OpenAI API客户端
            // 使用Builder模式创建OpenAiApi
            return OpenAiApi.builder()
                .apiKey(provider.getApiKey())
                .baseUrl(provider.getBaseUrl() != null && !provider.getBaseUrl().isEmpty() ? provider.getBaseUrl() : "https://api.openai.com")
                .build();
        });
    }

    /**
     * 清除指定提供商的API客户端缓存
     *
     * @param providerCode 提供商代码
     * @param providerId 提供商ID
     */
    public void clearOpenAiApiCache(String providerCode, Long providerId) {
        openAiApiCache.remove(providerCode + "-" + providerId);
    }

    /**
     * 清除所有OpenAI API客户端缓存
     */
    public void clearAllOpenAiApiCache() {
        openAiApiCache.clear();
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        clearAllOpenAiApiCache();
        // 清除其他类型的API缓存
    }
}
