package com.yonchain.ai.api.model;


/*import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;*/

public interface ChatService {

    /**
     * 聊天完成（Chat Completion）
     * 用于调用聊天模型进行对话
     *
     * @param tenantId     租户id
     * @param providerCode 服务商代码
     * @param modelCode    模型代码
     * @param request      请求参数
     * @return 聊天完成响应
     */
    ChatCompletionResponse chatCompletion(String tenantId, String providerCode, String modelCode, ChatCompletionRequest request);

    /*    *//**
     * 聊天完成（Chat Completion）- 流式输出
     * 用于调用聊天模型进行对话，支持流式输出
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return SSE发射器，用于流式输出
     *//*
    SseEmitter chatCompletionStream(String modelCode, ChatCompletionRequest request);*/

}
