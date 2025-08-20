package com.yonchain.ai.model.service.impl;

import com.yonchain.ai.model.dto.ChatCompletionRequest;
import com.yonchain.ai.model.dto.ChatCompletionResponse;
import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.model.factory.AIModelClientFactory;
import com.yonchain.ai.model.service.ChatService;
import com.yonchain.ai.model.service.ModelManagerService;
import com.yonchain.ai.model.spi.ModelProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
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

/**
 * 聊天服务实现类
 * 提供聊天完成（Chat Completion）和聊天完成（Chat Completion）- 流式输出两种方法
 *
 * @author Cgy
 */
/**
 * 模型调用服务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {


    @Autowired
    private ModelManagerService modelManagerService;

    @Autowired(required = false)
    private ChatClient configFileChatClient;

    @Autowired
    private AIModelClientFactory clientFactory;

    /**
     * 缓存已注册的模型提供商服务
     */
    private final Map<String, ModelProviderService> providerServiceMap = new ConcurrentHashMap<>();


    @Override
    public ChatCompletionResponse chatCompletion(String modelCode, ChatCompletionRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持聊天
        if (!ModelType.TEXT.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持聊天: " + model.getModelType());
        }

        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 获取模型提供商
        ModelProvider provider = modelManagerService.getProvider(model.getProviderCode());
        if (provider == null) {
            throw new IllegalArgumentException("模型提供商不存在: " + model.getProviderCode());
        }

        // 调用具体的模型实现
        // 根据不同的模型提供商实现不同的调用逻辑
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的聊天模型
            log.info("调用OpenAI的聊天模型: {}", modelCode);
            
            // 获取或创建聊天客户端
            ChatClient chatClient;
            try {
                // 尝试使用动态创建的客户端
                chatClient = clientFactory.getChatClient(model, provider);
            } catch (Exception e) {
                log.warn("无法创建动态聊天客户端，尝试使用配置文件客户端: {}", e.getMessage());
                // 如果动态创建失败，尝试使用配置文件创建的客户端
                if (configFileChatClient == null) {
                    throw new IllegalStateException("聊天客户端未配置");
                }
                chatClient = configFileChatClient;
            }
            
            // 转换请求消息格式
            List<Message> messages = convertToSpringAIMessages(request.getMessages());
            
            // 创建Prompt对象
            Prompt prompt = new Prompt(messages);
            
            // 调用聊天客户端
            ChatResponse chatResponse = chatClient.prompt(prompt)
                    .call()
                    .chatResponse();
            
            // 转换响应格式
            return convertToChatCompletionResponse(chatResponse, modelCode);
        }
        // 其他模型提供商的实现
        else if ("anthropic".equals(model.getProviderCode())) {
            // 调用Anthropic的聊天模型
            log.info("调用Anthropic的聊天模型: {}", modelCode);
            
            // 创建响应对象
            ChatCompletionResponse response = new ChatCompletionResponse();
            response.setId("chat-" + System.currentTimeMillis());
            response.setObject("chat.completion");
            response.setCreated(System.currentTimeMillis() / 1000);
            response.setModel(modelCode);
            
            // TODO: 实现Anthropic模型的调用
            
            return response;
        }

        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }
    
    /**
     * 将请求消息转换为Spring AI消息格式
     * 
     * @param messages 请求消息列表
     * @return Spring AI消息列表
     */
    private List<Message> convertToSpringAIMessages(List<ChatCompletionRequest.ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("消息列表不能为空");
        }
        
        List<Message> aiMessages = new ArrayList<>();
        
        for (ChatCompletionRequest.ChatMessage message : messages) {
            switch (message.getRole()) {
                case "system":
                    aiMessages.add(new SystemMessage(message.getContent()));
                    break;
                case "user":
                    aiMessages.add(new UserMessage(message.getContent()));
                    break;
                case "assistant":
                    aiMessages.add(new org.springframework.ai.chat.messages.AssistantMessage(message.getContent()));
                    break;
                default:
                    log.warn("未知的消息角色: {}", message.getRole());
                    // 默认作为用户消息处理
                    aiMessages.add(new UserMessage(message.getContent()));
            }
        }
        
        return aiMessages;
    }
    
    /**
     * 将Spring AI响应转换为聊天完成响应
     * 
     * @param chatResponse Spring AI聊天响应
     * @param modelCode 模型代码
     * @return 聊天完成响应
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

    @Override
    public SseEmitter chatCompletionStream(String modelCode, ChatCompletionRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持聊天
        if (!ModelType.TEXT.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持聊天: " + model.getModelType());
        }

        // 创建SSE发射器
        SseEmitter emitter = new SseEmitter();

        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 获取模型提供商
        ModelProvider provider = modelManagerService.getProvider(model.getProviderCode());
        if (provider == null) {
            throw new IllegalArgumentException("模型提供商不存在: " + model.getProviderCode());
        }

        // 调用具体的模型实现
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的聊天模型（流式输出）
            log.info("调用OpenAI的聊天模型（流式输出）: {}", modelCode);
            
            // 获取或创建聊天客户端
            ChatClient chatClient;
            try {
                // 尝试使用动态创建的客户端
                chatClient = clientFactory.getChatClient(model, provider);
            } catch (Exception e) {
                log.warn("无法创建动态聊天客户端，尝试使用配置文件客户端: {}", e.getMessage());
                // 如果动态创建失败，尝试使用配置文件创建的客户端
                if (configFileChatClient == null) {
                    throw new IllegalStateException("聊天客户端未配置");
                }
                chatClient = configFileChatClient;
            }
            
            // 转换请求消息格式
            List<Message> messages = convertToSpringAIMessages(request.getMessages());
            
            // 创建Prompt对象
            Prompt prompt = new Prompt(messages);
            
            // 在新线程中处理流式响应
            new Thread(() -> {
                try {
                    // 使用Spring AI 1.0.0的流式API
                    chatClient.prompt(prompt)
                        .stream()
                        .forEach(chunk -> {
                            try {
                                // 将每个响应片段发送到客户端
                                String content = chunk.getContent();
                                if (content != null && !content.isEmpty()) {
                                    emitter.send(content);
                                }
                            } catch (Exception e) {
                                log.error("发送流式响应失败", e);
                                emitter.completeWithError(e);
                            }
                        });
                    
                    // 完成流式输出
                    emitter.complete();
                } catch (Exception e) {
                    log.error("流式聊天失败", e);
                    emitter.completeWithError(e);
                }
            }).start();
            
            return emitter;
        }
        // 其他模型提供商的实现
        else if ("anthropic".equals(model.getProviderCode())) {
            // 调用Anthropic的聊天模型（流式输出）
            log.info("调用Anthropic的聊天模型（流式输出）: {}", modelCode);
            
            // TODO: 实现Anthropic模型的流式调用
            
            // 模拟流式输出
            new Thread(() -> {
                try {
                    emitter.send("暂不支持Anthropic模型的流式输出");
                    emitter.complete();
                } catch (Exception e) {
                    log.error("发送流式响应失败", e);
                    emitter.completeWithError(e);
                }
            }).start();
            
            return emitter;
        }

        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }


    /**
     * 获取模型提供商服务
     *
     * @param modelCode 模型代码
     * @return 提供商服务
     */
    private ModelProviderService getProviderService(String modelCode) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        ModelProviderService service = providerServiceMap.get(model.getProviderCode());
        if (service == null) {
            throw new IllegalArgumentException("模型提供商服务不可用: " + model.getProviderCode());
        }

        return service;
    }

    /**
     * 获取模型配置
     *
     * @param modelCode 模型代码
     * @return 模型配置
     */
    private Map<String, Object> getModelConfig(String modelCode) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        ModelProvider provider = modelManagerService.getProvider(model.getProviderCode());
        if (provider == null) {
            throw new IllegalArgumentException("模型提供商不存在: " + model.getProviderCode());
        }

        // 合并提供商配置和模型配置
        Map<String, Object> config = new ConcurrentHashMap<>();
        if (provider.getConfig() != null) {
            config.putAll(provider.getConfig());
        }

        // 添加API密钥、基础URL等配置
        if (provider.getApiKey() != null) {
            config.put("apiKey", provider.getApiKey());
        }
        if (provider.getBaseUrl() != null) {
            config.put("baseUrl", provider.getBaseUrl());
        }
        if (provider.getProxyUrl() != null) {
            config.put("proxyUrl", provider.getProxyUrl());
        }

        if (model.getConfig() != null) {
            config.putAll(model.getConfig());
        }

        return config;
    }
}
