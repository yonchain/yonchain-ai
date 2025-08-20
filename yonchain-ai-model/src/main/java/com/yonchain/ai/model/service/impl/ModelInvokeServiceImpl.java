package com.yonchain.ai.model.service.impl;

import com.yonchain.ai.model.dto.*;
import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.enums.ModelType;
import com.yonchain.ai.model.service.ModelInvokeService;
import com.yonchain.ai.model.service.ModelManagerService;
import com.yonchain.ai.model.spi.ModelProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型调用服务实现类
 */
@Slf4j
@Service
public class ModelInvokeServiceImpl implements ModelInvokeService {

    @Autowired
    private ModelManagerService modelManagerService;

    /**
     * 缓存已注册的模型提供商服务
     */
    private final Map<String, ModelProviderService> providerServiceMap = new ConcurrentHashMap<>();

    /**
     * 注册模型提供商服务
     *
     * @param providerCode 提供商代码
     * @param service      提供商服务
     */
    public void registerProviderService(String providerCode, ModelProviderService service) {
        providerServiceMap.put(providerCode, service);
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

 /*   @Override
    public ChatCompletionResponse chatCompletion(String modelCode, ChatCompletionRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持聊天
        if (!ModelType.TEXT.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持聊天: " + model.getModelType());
        }

        // 获取模型提供商服务
        ModelProviderService providerService = getProviderService(modelCode);
        
        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 调用具体的模型实现
        // 这里需要根据不同的模型提供商实现不同的调用逻辑
        // 由于我们没有实现具体的模型调用逻辑，这里只是一个示例
        // 在实际应用中，需要根据不同的模型提供商实现不同的调用逻辑
        
        // 示例：调用OpenAI的聊天模型
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的聊天模型
            // 这里需要使用Spring AI的OpenAI客户端
            // 由于依赖问题，这里只是一个示例
            log.info("调用OpenAI的聊天模型: {}", modelCode);
            
            // 创建响应对象
            ChatCompletionResponse response = new ChatCompletionResponse();
            response.setId("chat-" + System.currentTimeMillis());
            response.setObject("chat.completion");
            response.setCreated(System.currentTimeMillis() / 1000);
            response.setModel(modelCode);
            
            // 这里应该是实际调用OpenAI API的结果
            // 由于依赖问题，这里只是一个示例
            
            return response;
        } 
        // 示例：调用DeepSeek的聊天模型
        else if ("deepseek".equals(model.getProviderCode())) {
            // 调用DeepSeek的聊天模型
            log.info("调用DeepSeek的聊天模型: {}", modelCode);
            
            // 创建响应对象
            ChatCompletionResponse response = new ChatCompletionResponse();
            response.setId("chat-" + System.currentTimeMillis());
            response.setObject("chat.completion");
            response.setCreated(System.currentTimeMillis() / 1000);
            response.setModel(modelCode);
            
            // 这里应该是实际调用DeepSeek API的结果
            // 由于依赖问题，这里只是一个示例
            
            return response;
        }
        
        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
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
        
        // 获取模型提供商服务
        ModelProviderService providerService = getProviderService(modelCode);
        
        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 调用具体的模型实现
        // 这里需要根据不同的模型提供商实现不同的调用逻辑
        // 由于我们没有实现具体的模型调用逻辑，这里只是一个示例
        // 在实际应用中，需要根据不同的模型提供商实现不同的调用逻辑
        
        // 示例：调用OpenAI的聊天模型（流式输出）
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的聊天模型（流式输出）
            // 这里需要使用Spring AI的OpenAI客户端
            // 由于依赖问题，这里只是一个示例
            log.info("调用OpenAI的聊天模型（流式输出）: {}", modelCode);
            
            // 这里应该是实际调用OpenAI API的流式输出
            // 由于依赖问题，这里只是一个示例
            
            return emitter;
        } 
        // 示例：调用DeepSeek的聊天模型（流式输出）
        else if ("deepseek".equals(model.getProviderCode())) {
            // 调用DeepSeek的聊天模型（流式输出）
            log.info("调用DeepSeek的聊天模型（流式输出）: {}", modelCode);
            
            // 这里应该是实际调用DeepSeek API的流式输出
            // 由于依赖问题，这里只是一个示例
            
            return emitter;
        }
        
        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }*/

    @Override
    public CompletionResponse textCompletion(String modelCode, CompletionRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持文本生成
        if (!ModelType.TEXT.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持文本生成: " + model.getModelType());
        }

        // 获取模型提供商服务
        ModelProviderService providerService = getProviderService(modelCode);
        
        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 调用具体的模型实现
        // 这里需要根据不同的模型提供商实现不同的调用逻辑
        // 由于我们没有实现具体的模型调用逻辑，这里只是一个示例
        // 在实际应用中，需要根据不同的模型提供商实现不同的调用逻辑
        
        // 示例：调用OpenAI的文本生成模型
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的文本生成模型
            // 这里需要使用Spring AI的OpenAI客户端
            // 由于依赖问题，这里只是一个示例
            log.info("调用OpenAI的文本生成模型: {}", modelCode);
            
            // 创建响应对象
            CompletionResponse response = new CompletionResponse();
            response.setId("completion-" + System.currentTimeMillis());
            response.setObject("text.completion");
            response.setCreated(System.currentTimeMillis() / 1000);
            response.setModel(modelCode);
            
            // 这里应该是实际调用OpenAI API的结果
            // 由于依赖问题，这里只是一个示例
            
            return response;
        }
        
        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }

    @Override
    public SseEmitter textCompletionStream(String modelCode, CompletionRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持文本生成
        if (!ModelType.TEXT.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持文本生成: " + model.getModelType());
        }

        // 创建SSE发射器
        SseEmitter emitter = new SseEmitter();
        
        // 获取模型提供商服务
        ModelProviderService providerService = getProviderService(modelCode);
        
        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 调用具体的模型实现
        // 这里需要根据不同的模型提供商实现不同的调用逻辑
        // 由于我们没有实现具体的模型调用逻辑，这里只是一个示例
        // 在实际应用中，需要根据不同的模型提供商实现不同的调用逻辑
        
        // 示例：调用OpenAI的文本生成模型（流式输出）
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的文本生成模型（流式输出）
            // 这里需要使用Spring AI的OpenAI客户端
            // 由于依赖问题，这里只是一个示例
            log.info("调用OpenAI的文本生成模型（流式输出）: {}", modelCode);
            
            // 这里应该是实际调用OpenAI API的流式输出
            // 由于依赖问题，这里只是一个示例
            
            return emitter;
        }
        
        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }

    @Override
    public ImageGenerationResponse generateImage(String modelCode, ImageGenerationRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持图像生成
        if (!ModelType.IMAGE.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持图像生成: " + model.getModelType());
        }

        // 获取模型提供商服务
        ModelProviderService providerService = getProviderService(modelCode);
        
        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 调用具体的模型实现
        // 这里需要根据不同的模型提供商实现不同的调用逻辑
        // 由于我们没有实现具体的模型调用逻辑，这里只是一个示例
        // 在实际应用中，需要根据不同的模型提供商实现不同的调用逻辑
        
        // 示例：调用OpenAI的图像生成模型
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的图像生成模型
            // 这里需要使用Spring AI的OpenAI客户端
            // 由于依赖问题，这里只是一个示例
            log.info("调用OpenAI的图像生成模型: {}", modelCode);
            
            // 创建响应对象
            ImageGenerationResponse response = new ImageGenerationResponse();
            response.setId("image-" + System.currentTimeMillis());
            response.setCreated(System.currentTimeMillis() / 1000);
            
            // 这里应该是实际调用OpenAI API的结果
            // 由于依赖问题，这里只是一个示例
            
            return response;
        }
        
        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }

    @Override
    public EmbeddingResponse createEmbedding(String modelCode, EmbeddingRequest request) {
        AIModel model = modelManagerService.getModel(modelCode);
        if (model == null) {
            throw new IllegalArgumentException("模型不存在: " + modelCode);
        }

        // 检查模型类型是否支持嵌入
        if (!ModelType.EMBEDDING.getCode().equals(model.getModelType())) {
            throw new IllegalArgumentException("模型类型不支持嵌入: " + model.getModelType());
        }

        // 获取模型提供商服务
        ModelProviderService providerService = getProviderService(modelCode);
        
        // 获取模型配置
        Map<String, Object> config = getModelConfig(modelCode);
        
        // 调用具体的模型实现
        // 这里需要根据不同的模型提供商实现不同的调用逻辑
        // 由于我们没有实现具体的模型调用逻辑，这里只是一个示例
        // 在实际应用中，需要根据不同的模型提供商实现不同的调用逻辑
        
        // 示例：调用OpenAI的嵌入模型
        if ("openai".equals(model.getProviderCode())) {
            // 调用OpenAI的嵌入模型
            // 这里需要使用Spring AI的OpenAI客户端
            // 由于依赖问题，这里只是一个示例
            log.info("调用OpenAI的嵌入模型: {}", modelCode);
            
            // 创建响应对象
            EmbeddingResponse response = new EmbeddingResponse();
            response.setObject("embedding");
            response.setModel(modelCode);
            
            // 这里应该是实际调用OpenAI API的结果
            // 由于依赖问题，这里只是一个示例
            
            return response;
        }
        
        throw new UnsupportedOperationException("不支持的模型提供商: " + model.getProviderCode());
    }
}