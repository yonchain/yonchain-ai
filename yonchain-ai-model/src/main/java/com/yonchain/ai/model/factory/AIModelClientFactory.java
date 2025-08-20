package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * AI模型客户端工厂
 * 用于创建和缓存不同模型的聊天客户端实例
 */
@Component
public class AIModelClientFactory {

    @Autowired
    private ModelFactory modelFactory;

    /**
     * 缓存已创建的聊天客户端
     * key: 模型代码-提供商ID
     */
    private final Map<String, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    /**
     * 客户端创建策略映射
     */
    private final Map<String, Function<ChatModel, ChatClient>> clientCreationStrategies = new ConcurrentHashMap<>();

    public AIModelClientFactory() {
        initializeClientCreationStrategies();
    }

    /**
     * 初始化客户端创建策略
     */
    private void initializeClientCreationStrategies() {
        // 默认策略：直接创建ChatClient
        Function<ChatModel, ChatClient> defaultStrategy = ChatClient::create;
        
        // 为所有提供商设置默认策略
        clientCreationStrategies.put("openai", defaultStrategy);
        clientCreationStrategies.put("deepseek", defaultStrategy);
        clientCreationStrategies.put("anthropic", defaultStrategy);
        clientCreationStrategies.put("ollama", defaultStrategy);
        clientCreationStrategies.put("grok", defaultStrategy);
        
        // 可以为特定提供商设置自定义策略
        // clientCreationStrategies.put("custom_provider", this::createCustomChatClient);
    }

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
            // 使用模型工厂的通用方法获取ChatModel
            ChatModel chatModel = modelFactory.getChatModel(model, provider);
            
            // 根据提供商选择创建策略
            String providerCode = provider.getCode().toLowerCase();
            Function<ChatModel, ChatClient> strategy = clientCreationStrategies.get(providerCode);
            
            if (strategy == null) {
                // 使用默认策略
                strategy = ChatClient::create;
            }
            
            return strategy.apply(chatModel);
        });
    }

    /**
     * 获取特定提供商的聊天客户端
     * 
     * @param model 模型信息
     * @param provider 提供商信息
     * @param providerType 提供商类型
     * @return 聊天客户端
     */
    public ChatClient getChatClient(AIModel model, ModelProvider provider, String providerType) {
        if (model == null || provider == null) {
            throw new IllegalArgumentException("模型或提供商不能为空");
        }

        String cacheKey = model.getCode() + "-" + provider.getId() + "-" + providerType;
        return chatClientCache.computeIfAbsent(cacheKey, key -> {
            ChatModel chatModel;
            
            // 根据提供商类型获取特定的ChatModel
            switch (providerType.toLowerCase()) {
                case "deepseek":
                    chatModel = modelFactory.getDeepSeekChatModel(model, provider);
                    break;
                case "anthropic":
                    chatModel = modelFactory.getAnthropicChatModel(model, provider);
                    break;
                default:
                    chatModel = modelFactory.getChatModel(model, provider);
                    break;
            }
            
            return ChatClient.create(chatModel);
        });
    }

    /**
     * 创建聊天客户端（不缓存）
     * 
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelCode 模型代码
     * @param temperature 温度参数
     * @param providerType 提供商类型
     * @return 聊天客户端
     */
    public ChatClient createChatClient(String apiKey, String baseUrl, String modelCode, 
                                     Float temperature, String providerType) {
        // 创建临时的ModelProvider和AIModel对象
        ModelProvider tempProvider = new ModelProvider();
        tempProvider.setApiKey(apiKey);
        tempProvider.setBaseUrl(baseUrl);
        tempProvider.setCode(providerType);
        
        AIModel tempModel = new AIModel();
        tempModel.setCode(modelCode);
        
        // 使用getChatModel方法创建ChatModel
        ChatModel chatModel = modelFactory.getChatModel(tempModel, tempProvider);
        
        return ChatClient.create(chatModel);
    }

    /**
     * 创建聊天客户端（不缓存，使用默认参数）
     * 
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelCode 模型代码
     * @param temperature 温度参数
     * @return 聊天客户端
     */
    public ChatClient createChatClient(String apiKey, String baseUrl, String modelCode, Float temperature) {
        return createChatClient(apiKey, baseUrl, modelCode, temperature, "deepseek");
    }

    /**
     * 批量创建聊天客户端
     * 
     * @param models 模型列表
     * @param provider 提供商信息
     * @return 聊天客户端映射
     */
    public Map<String, ChatClient> batchCreateChatClients(java.util.List<AIModel> models, ModelProvider provider) {
        Map<String, ChatClient> clients = new ConcurrentHashMap<>();
        
        models.parallelStream().forEach(model -> {
            try {
                ChatClient client = getChatClient(model, provider);
                clients.put(model.getCode(), client);
            } catch (Exception e) {
                // 记录错误但不中断批量创建
                System.err.println("创建模型 " + model.getCode() + " 的客户端失败: " + e.getMessage());
            }
        });
        
        return clients;
    }

    /**
     * 预热聊天客户端缓存
     * 
     * @param models 模型列表
     * @param providers 提供商列表
     */
    public void warmUpCache(java.util.List<AIModel> models, java.util.List<ModelProvider> providers) {
        models.parallelStream().forEach(model -> 
            providers.parallelStream().forEach(provider -> {
                try {
                    getChatClient(model, provider);
                } catch (Exception e) {
                    // 忽略预热过程中的错误
                    System.err.println("预热缓存失败 - 模型: " + model.getCode() + 
                                     ", 提供商: " + provider.getCode() + ", 错误: " + e.getMessage());
                }
            })
        );
    }

    // ==================== 缓存管理方法 ====================

    /**
     * 清除指定模型和提供商的聊天客户端缓存
     *
     * @param modelCode 模型代码
     * @param providerId 提供商ID
     */
    public void clearChatClientCache(String modelCode, Long providerId) {
        chatClientCache.entrySet().removeIf(entry -> 
            entry.getKey().startsWith(modelCode + "-" + providerId));
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
        chatClientCache.entrySet().removeIf(entry -> entry.getKey().contains("-" + providerId + "-"));
    }

    /**
     * 清除所有聊天客户端缓存
     */
    public void clearAllCache() {
        chatClientCache.clear();
    }

    /**
     * 获取缓存统计信息
     * 
     * @return 缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("cacheSize", chatClientCache.size());
        stats.put("cachedKeys", chatClientCache.keySet());
        return stats;
    }

    /**
     * 注册自定义客户端创建策略
     *
     * @param providerCode 提供商代码
     * @param strategy 创建策略
     */
    public void registerClientCreationStrategy(String providerCode, Function<ChatModel, ChatClient> strategy) {
        clientCreationStrategies.put(providerCode.toLowerCase(), strategy);
    }

    /**
     * 检查客户端是否已缓存
     * 
     * @param modelCode 模型代码
     * @param providerId 提供商ID
     * @return 是否已缓存
     */
    public boolean isCached(String modelCode, Long providerId) {
        String cacheKey = modelCode + "-" + providerId;
        return chatClientCache.containsKey(cacheKey);
    }
}