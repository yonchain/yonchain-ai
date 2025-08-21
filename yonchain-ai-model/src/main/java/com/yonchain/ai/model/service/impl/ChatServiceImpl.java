package com.yonchain.ai.model.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.api.model.ChatCompletionRequest;
import com.yonchain.ai.api.model.ChatCompletionResponse;
import com.yonchain.ai.model.entity.AiModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.factory.ModelClientFactory;
import com.yonchain.ai.api.model.ChatService;
import com.yonchain.ai.model.spi.ModelProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 * 提供聊天完成（Chat Completion）和流式聊天完成功能
 *
 * @author Cgy
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
/*
    @Autowired
    private ModelManagerService modelManagerService;*/

    @Autowired(required = false)
    private ChatClient configFileChatClient;

    @Autowired
    private ModelClientFactory clientFactory;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 缓存已注册的模型提供商服务
     */
    private final Map<String, ModelProviderService> providerServiceMap = new ConcurrentHashMap<>();

    @Override
    public ChatCompletionResponse chatCompletion(String modelCode, ChatCompletionRequest request) {
        // 参数验证
        validateChatCompletionRequest(modelCode, request);
        
        // 获取模型和提供商信息
        AiModel model = null;//getValidatedModel(modelCode);
        ModelProvider provider = null;//getValidatedProvider(model.getProviderCode());
        
        long startTime = System.currentTimeMillis();
        log.info("开始聊天完成请求 - 模型: {}, 提供商: {}, 消息数量: {}", 
                modelCode, provider.getCode(), request.getMessages().size());
        
        try {
            // 使用优化后的客户端工厂获取聊天客户端
            ChatClient chatClient = getChatClientWithFallback(model, provider);
            
            // 转换请求消息格式
            List<Message> messages = convertToSpringAIMessages(request.getMessages());
            
            // 创建Prompt对象，支持更多配置选项
            Prompt prompt = createEnhancedPrompt(messages, request, model);
            
            // 执行聊天请求
            ChatResponse chatResponse = executeChatRequest(chatClient, prompt, model);
            
            // 转换并返回响应
            ChatCompletionResponse response = convertToChatCompletionResponse(chatResponse, modelCode);
            
            long duration = System.currentTimeMillis() - startTime;
            logChatStatistics(modelCode, request, response, duration);
            
            log.info("聊天完成请求成功 - 模型: {}, 响应长度: {}, 耗时: {}ms", 
                    modelCode, response.getChoices().get(0).getMessage().getContent().length(), duration);
            
            return response;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("聊天完成请求失败 - 模型: {}, 耗时: {}ms, 错误: {}", modelCode, duration, e.getMessage(), e);
            throw new RuntimeException("聊天完成请求失败: " + e.getMessage(), e);
        }
    }

/*    @Override
    public SseEmitter chatCompletionStream(String modelCode, ChatCompletionRequest request) {
        // 参数验证
        validateChatCompletionRequest(modelCode, request);
        
        // 获取模型和提供商信息
        AiModel model = getValidatedModel(modelCode);
        ModelProvider provider = getValidatedProvider(model.getProviderCode());
        
        log.info("开始流式聊天完成请求 - 模型: {}, 提供商: {}, 消息数量: {}", 
                modelCode, provider.getCode(), request.getMessages().size());
        
        // 创建SSE发射器，设置超时时间
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
        
        // 设置完成和错误回调
        emitter.onCompletion(() -> log.info("流式聊天完成 - 模型: {}", modelCode));
        emitter.onError(throwable -> log.error("流式聊天错误 - 模型: {}, 错误: {}", modelCode, throwable.getMessage()));
        emitter.onTimeout(() -> log.warn("流式聊天超时 - 模型: {}", modelCode));
        
        try {
            // 使用优化后的客户端工厂获取聊天客户端
            ChatClient chatClient = getChatClientWithFallback(model, provider);
            
            // 转换请求消息格式
            List<Message> messages = convertToSpringAIMessages(request.getMessages());
            
            // 创建Prompt对象
            Prompt prompt = createEnhancedPrompt(messages, request, model);
            
            // 在新线程中处理流式响应
            executeStreamingChatRequest(emitter, chatClient, prompt, model, provider);
            
        } catch (Exception e) {
            log.error("创建流式聊天请求失败 - 模型: {}, 错误: {}", modelCode, e.getMessage(), e);
            emitter.completeWithError(new RuntimeException("流式聊天请求失败: " + e.getMessage(), e));
        }
        
        return emitter;
    }
    */
    /**
     * 验证聊天完成请求参数
     */
    private void validateChatCompletionRequest(String modelCode, ChatCompletionRequest request) {
        if (modelCode == null || modelCode.trim().isEmpty()) {
            throw new IllegalArgumentException("模型代码不能为空");
        }
        
        if (request == null) {
            throw new IllegalArgumentException("请求对象不能为空");
        }
        
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new IllegalArgumentException("消息列表不能为空");
        }
        
        // 验证消息内容
        for (ChatCompletionRequest.ChatMessage message : request.getMessages()) {
            if (message == null) {
                throw new IllegalArgumentException("消息对象不能为空");
            }
            if (message.getRole() == null || message.getRole().trim().isEmpty()) {
                throw new IllegalArgumentException("消息角色不能为空");
            }
        }
    }
    
   /* *//**
     * 获取并验证模型
     *//*
    private AiModel getValidatedModel(String modelCode) {
        AiModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }
        
        // 检查模型类型是否支持聊天
        if (!ModelType.TEXT.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持聊天: " + model.getModelType());
        }
        
        return model;
    }
    
    *//**
     * 获取并验证模型提供商
     *//*
    private ModelProvider getValidatedProvider(String providerCode) {
        ModelProvider provider = modelManagerService.getProvider(providerCode);
        if (provider == null) {
            throw new IllegalArgumentException("模型提供商不存在: " + providerCode);
        }
        
        // 验证提供商配置
        if (provider.getApiKey() == null || provider.getApiKey().trim().isEmpty()) {
            log.warn("模型提供商API密钥未配置: {}", providerCode);
        }
        
        return provider;
    }*/
    
    /**
     * 获取聊天客户端，支持降级处理
     */
    private ChatClient getChatClientWithFallback(AiModel model, ModelProvider provider) {
        try {
            // 优先使用动态创建的客户端
            ChatClient dynamicClient = clientFactory.getChatClient(model, provider);
            log.debug("成功创建动态聊天客户端 - 模型: {}", model.getCode());
            return dynamicClient;
        } catch (Exception e) {
            log.warn("无法创建动态聊天客户端，尝试使用配置文件客户端 - 模型: {}, 错误: {}", 
                    model.getCode(), e.getMessage());
            
            // 降级使用配置文件创建的客户端
            if (configFileChatClient == null) {
                throw new IllegalStateException("聊天客户端未配置，且无法动态创建客户端: " + e.getMessage());
            }
            
            log.debug("使用配置文件聊天客户端 - 模型: {}", model.getCode());
            return configFileChatClient;
        }
    }
    
    /**
     * 转换为Spring AI消息格式
     */
    private List<Message> convertToSpringAIMessages(List<ChatCompletionRequest.ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("消息列表不能为空");
        }
        
        return messages.stream()
                .filter(Objects::nonNull)
                .map(msg -> {
                    String content = msg.getContent();
                    if (content == null || content.trim().isEmpty()) {
                        log.warn("发现空消息内容，角色: {}", msg.getRole());
                        content = "";
                    }
                    
                    switch (msg.getRole().toLowerCase()) {
                        case "system":
                            return new SystemMessage(content);
                        case "user":
                            return new UserMessage(content);
                        case "assistant":
                            return new AssistantMessage(content);
                        default:
                            log.warn("未知消息角色: {}，默认作为用户消息处理", msg.getRole());
                            return new UserMessage(content);
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 创建增强的Prompt对象
     */
    private Prompt createEnhancedPrompt(List<Message> messages, ChatCompletionRequest request, AiModel model) {
        // 基础Prompt
        Prompt prompt = new Prompt(messages);
        
        // 如果有额外的配置参数，可以在这里添加
        // 例如：温度、最大令牌数等
        if (request.getTemperature() != null || request.getMaxTokens() != null) {
            // 创建带选项的Prompt
            // 注意：这里需要根据具体的Spring AI版本和模型提供商来调整
            log.debug("应用聊天选项 - 模型: {}, 温度: {}, 最大令牌: {}", 
                    model.getCode(), request.getTemperature(), request.getMaxTokens());
        }
        
        return prompt;
    }
    
    /**
     * 执行聊天请求，支持重试和错误处理
     */
    private ChatResponse executeChatRequest(ChatClient chatClient, Prompt prompt, AiModel model) {
        try {
            return chatClient.prompt(prompt)
                    .call()
                    .chatResponse();
        } catch (Exception e) {
            log.error("执行聊天请求失败 - 模型: {}, 错误: {}", model.getCode(), e.getMessage());
            throw new RuntimeException("模型调用失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将Spring AI响应转换为聊天完成响应
     */
    private ChatCompletionResponse convertToChatCompletionResponse(ChatResponse chatResponse, String modelCode) {
        ChatCompletionResponse response = new ChatCompletionResponse();
        response.setId("chat-" + System.currentTimeMillis());
        response.setObject("chat.completion");
        response.setCreated(System.currentTimeMillis() / 1000);
        response.setModel(modelCode);
        
        // 转换选项
        List<ChatCompletionResponse.Choice> choices = new ArrayList<>();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        choice.setIndex(0);
        
        // 创建消息
        ChatCompletionRequest.ChatMessage message = new ChatCompletionRequest.ChatMessage();
        message.setRole("assistant");
        message.setContent(chatResponse.getResult().getOutput().getText());
        choice.setMessage(message);
        
        // 设置结束原因
        choice.setFinishReason("stop");
        
        choices.add(choice);
        response.setChoices(choices);
        
        // 设置使用情况统计
        ChatCompletionResponse.Usage usage = new ChatCompletionResponse.Usage();
        // Spring AI目前不提供令牌计数，所以这里设置为0
        usage.setPromptTokens(0);
        usage.setCompletionTokens(0);
        usage.setTotalTokens(0);
        response.setUsage(usage);
        
        return response;
    }
    
    /**
     * 执行流式聊天请求
     */
    private void executeStreamingChatRequest(SseEmitter emitter, ChatClient chatClient, 
                                           Prompt prompt, AiModel model, ModelProvider provider) {
        // 使用线程池执行异步任务
        new Thread(() -> {
            try {
                String providerCode = provider.getCode().toLowerCase();
                
                switch (providerCode) {
                    case "openai":
                    case "deepseek":
                        executeOpenAIStyleStreaming(emitter, chatClient, prompt, model);
                        break;
                    case "anthropic":
                        executeAnthropicStyleStreaming(emitter, chatClient, prompt, model);
                        break;
                    default:
                        executeDefaultStreaming(emitter, chatClient, prompt, model);
                        break;
                }
                
            } catch (Exception e) {
                log.error("执行流式聊天请求失败 - 模型: {}, 错误: {}", model.getCode(), e.getMessage(), e);
                emitter.completeWithError(e);
            }
        }).start();
    }
    
    /**
     * 执行OpenAI风格的流式请求
     */
    private void executeOpenAIStyleStreaming(SseEmitter emitter, ChatClient chatClient, 
                                           Prompt prompt, AiModel model) {
        try {
            log.debug("开始OpenAI风格流式请求 - 模型: {}", model.getCode());
            
            // 使用Spring AI的流式API，返回Flux<ChatResponse>
            chatClient.prompt(prompt)
                .stream()
                .chatResponse()
                .doOnNext(chunk -> {
                    try {
                        if (chunk != null && chunk.getResults() != null && !chunk.getResults().isEmpty()) {
                            String content = chunk.getResults().get(0).getOutput().getText();
                            if (content != null && !content.isEmpty()) {
                                // 发送格式化的SSE数据
                                String sseData = formatSseData(content, model.getCode());
                                emitter.send(sseData);
                            }
                        }
                    } catch (Exception e) {
                        log.error("发送流式响应片段失败 - 模型: {}", model.getCode(), e);
                        emitter.completeWithError(e);
                    }
                })
                .doOnComplete(() -> {
                    try {
                        // 发送结束标记
                        emitter.send("[DONE]");
                        emitter.complete();
                        log.debug("OpenAI风格流式请求完成 - 模型: {}", model.getCode());
                    } catch (Exception e) {
                        log.error("完成流式请求失败 - 模型: {}", model.getCode(), e);
                        emitter.completeWithError(e);
                    }
                })
                .doOnError(error -> {
                    log.error("OpenAI风格流式请求失败 - 模型: {}", model.getCode(), error);
                    emitter.completeWithError(error);
                })
                .subscribe(); // 订阅Flux以开始流式处理
            
        } catch (Exception e) {
            log.error("创建OpenAI风格流式请求失败 - 模型: {}", model.getCode(), e);
            emitter.completeWithError(e);
        }
    }
    
    /**
     * 执行Anthropic风格的流式请求
     */
    private void executeAnthropicStyleStreaming(SseEmitter emitter, ChatClient chatClient, 
                                              Prompt prompt, AiModel model) {
        try {
            log.debug("开始Anthropic风格流式请求 - 模型: {}", model.getCode());
            
            // TODO: 实现Anthropic特定的流式处理逻辑
            // 目前使用默认实现
            executeDefaultStreaming(emitter, chatClient, prompt, model);
            
        } catch (Exception e) {
            log.error("Anthropic风格流式请求失败 - 模型: {}", model.getCode(), e);
            emitter.completeWithError(e);
        }
    }
    
    /**
     * 执行默认的流式请求
     */
    private void executeDefaultStreaming(SseEmitter emitter, ChatClient chatClient, 
                                       Prompt prompt, AiModel model) {
        try {
            log.debug("开始默认流式请求 - 模型: {}", model.getCode());
            
            // 使用通用的流式处理，返回Flux<String>
            chatClient.prompt(prompt)
                .stream()
                .content()
                .doOnNext(content -> {
                    try {
                        if (content != null && !content.isEmpty()) {
                            emitter.send(content);
                        }
                    } catch (Exception e) {
                        log.error("发送流式内容失败 - 模型: {}", model.getCode(), e);
                        emitter.completeWithError(e);
                    }
                })
                .doOnComplete(() -> {
                    try {
                        emitter.complete();
                        log.debug("默认流式请求完成 - 模型: {}", model.getCode());
                    } catch (Exception e) {
                        log.error("完成默认流式请求失败 - 模型: {}", model.getCode(), e);
                        emitter.completeWithError(e);
                    }
                })
                .doOnError(error -> {
                    log.error("默认流式请求失败 - 模型: {}", model.getCode(), error);
                    emitter.completeWithError(error);
                })
                .subscribe(); // 订阅Flux以开始流式处理
            
        } catch (Exception e) {
            log.error("创建默认流式请求失败 - 模型: {}", model.getCode(), e);
            emitter.completeWithError(e);
        }
    }
    
    /**
     * 格式化SSE数据
     */
    private String formatSseData(String content, String modelCode) {
        // 创建符合OpenAI格式的流式响应
        Map<String, Object> response = new HashMap<>();
        response.put("id", "chatcmpl-" + System.currentTimeMillis());
        response.put("object", "chat.completion.chunk");
        response.put("created", System.currentTimeMillis() / 1000);
        response.put("model", modelCode);
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        
        Map<String, Object> delta = new HashMap<>();
        delta.put("content", content);
        choice.put("delta", delta);
        
        response.put("choices", Arrays.asList(choice));
        
        // 转换为JSON字符串（这里简化处理，实际应该使用JSON库）
        return "data: " + response.toString() + "\n\n";
    }
    
    /**
     * 记录聊天请求和响应的统计信息
     */
    private void logChatStatistics(String modelCode, ChatCompletionRequest request, 
                                 ChatCompletionResponse response, long duration) {
        try {
            int requestTokens = estimateTokens(request.getMessages());
            int responseTokens = response.getChoices() != null && !response.getChoices().isEmpty() 
                ? estimateTokens(response.getChoices().get(0).getMessage().getContent()) 
                : 0;
            
            log.info("聊天完成统计 - 模型: {}, 请求令牌: {}, 响应令牌: {}, 耗时: {}ms", 
                    modelCode, requestTokens, responseTokens, duration);
        } catch (Exception e) {
            log.debug("记录聊天统计失败: {}", e.getMessage());
        }
    }
    
    /**
     * 估算令牌数量（简单实现）
     */
    private int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 简单估算：平均每4个字符约等于1个令牌
        return (int) Math.ceil(text.length() / 4.0);
    }
    
    /**
     * 估算消息列表的令牌数量
     */
    private int estimateTokens(List<ChatCompletionRequest.ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        
        return messages.stream()
                .filter(Objects::nonNull)
                .mapToInt(msg -> estimateTokens(msg.getContent()))
                .sum();
    }

  /*  *//**
     * 获取模型提供商服务
     *//*
    private ModelProviderService getProviderService(String modelCode) {
        AiModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        ModelProviderService service = providerServiceMap.get(model.getProviderCode());
        if (service == null) {
            throw new IllegalArgumentException("模型提供商服务不可用: " + model.getProviderCode());
        }

        return service;
    }*/
}