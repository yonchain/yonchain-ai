package com.yonchain.ai.api.model;



/**
 * 模型调用服务接口
 * 提供统一的模型调用方法，支持不同类型的模型调用
 */
public interface ModelInvokeService {
/*
*//*    *//**//**
     * 聊天完成（Chat Completion）
     * 用于调用聊天模型进行对话
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return 聊天完成响应
     *//**//*
    ChatCompletionResponse chatCompletion(String modelCode, ChatCompletionRequest request);

    *//**//**
     * 聊天完成（Chat Completion）- 流式输出
     * 用于调用聊天模型进行对话，支持流式输出
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return SSE发射器，用于流式输出
     *//**//*
    SseEmitter chatCompletionStream(String modelCode, ChatCompletionRequest request);*//*

    *//**
     * 文本完成（Text Completion）
     * 用于调用文本生成模型生成文本
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return 文本完成响应
     *//*
    CompletionResponse textCompletion(String modelCode, CompletionRequest request);

    *//**
     * 文本完成（Text Completion）- 流式输出
     * 用于调用文本生成模型生成文本，支持流式输出
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return SSE发射器，用于流式输出
     *//*
    SseEmitter textCompletionStream(String modelCode, CompletionRequest request);

    *//**
     * 图像生成（Image Generation）
     * 用于调用图像生成模型生成图像
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return 图像生成响应
     *//*
    ImageGenerationResponse generateImage(String modelCode, ImageGenerationRequest request);

    *//**
     * 文本嵌入（Text Embedding）
     * 用于调用嵌入模型生成文本的向量表示
     *
     * @param modelCode 模型代码
     * @param request   请求参数
     * @return 文本嵌入响应
     *//*
    EmbeddingResponse createEmbedding(String modelCode, EmbeddingRequest request);*/
}