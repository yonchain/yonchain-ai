package com.yonchain.ai.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.filter.BaseModelFilter;
import com.yonchain.ai.model.ModelClient;
import com.yonchain.ai.model.request.ChatRequest;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * 聊天模型专用过滤器
 * 处理聊天完成请求 /chat/completions
 */
public class ChatModelFilter extends BaseModelFilter {

    private static final Pattern ENDPOINT_PATTERN = Pattern.compile(".*/chat/completions$");

    public ChatModelFilter(ModelClient modelClient, ObjectMapper objectMapper) {
        super(modelClient, objectMapper);
    }

    @Override
    protected Pattern getEndpointPattern() {
        return ENDPOINT_PATTERN;
    }

    @Override
    protected String getModelType() {
        return "chat";
    }

    @Override
    protected void handleModelRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 1. 解析请求
        Map<String, Object> requestParams = parseRequestBody(request);
        String modelName = extractModelName(requestParams);

        logger.debug("Processing chat request for model: {}", modelName);

        // 2. 转换为ChatRequest
        ChatRequest chatRequest = convertToChatRequest(requestParams);

        // 3. 使用ModelClient调用
        if (isStreamRequest(requestParams)) {
            handleStreamRequest(modelName, chatRequest, request, response);
        } else {
            handleSyncRequest(modelName, chatRequest, response);
        }
    }

    /**
     * 处理同步聊天请求
     */
    private void handleSyncRequest(String modelName, ChatRequest chatRequest,
                                   HttpServletResponse response) throws IOException {

        try {
            // 调用ModelClient
            ChatResponse chatResponse = modelClient.chat(modelName, chatRequest);

            // 转换为OpenAI格式
            Map<String, Object> responseData = convertChatResponse(chatResponse, false);

            // 发送响应
            sendSuccessResponse(response, responseData);

            logger.debug("Chat request completed successfully");

        } catch (Exception e) {
            logger.error("Error in sync chat request", e);
            sendErrorResponse(response, "Failed to process chat request: " + e.getMessage(), 500);
        }
    }

    /**
     * 处理流式聊天请求
     */
    private void handleStreamRequest(String modelName, ChatRequest chatRequest,
                                     HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 启动异步上下文
        AsyncContext asyncContext = request.startAsync(request, response);
        asyncContext.setTimeout(300000); // 5分钟超时

        // 设置SSE响应头
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // 异步处理流式响应
        CompletableFuture.runAsync(() -> {
            try {
                PrintWriter writer = response.getWriter();

                // 调用ModelClient流式接口
                Flux<ChatResponse> responseStream = modelClient.chatStream(modelName, chatRequest);

                responseStream.subscribe(
                        chatResponse -> {
                            try {
                                Map<String, Object> responseData = convertChatResponse(chatResponse, true);
                                String jsonResponse = objectMapper.writeValueAsString(responseData);
                                writer.write("data: " + jsonResponse + "\n\n");
                                writer.flush();
                            } catch (Exception e) {
                                logger.error("Error writing stream response", e);
                            }
                        },
                        error -> {
                            logger.error("Error in stream chat request", error);
                            try {
                                writer.write("data: [DONE]\n\n");
                                writer.flush();
                            } catch (Exception e) {
                                logger.error("Error closing stream on error", e);
                            } finally {
                                asyncContext.complete();
                            }
                        },
                        () -> {
                            try {
                                writer.write("data: [DONE]\n\n");
                                writer.flush();
                                logger.debug("Stream chat request completed");
                            } catch (Exception e) {
                                logger.error("Error closing stream on completion", e);
                            } finally {
                                asyncContext.complete();
                            }
                        }
                );

            } catch (Exception e) {
                logger.error("Error in async stream processing", e);
                completeAsyncWithError(asyncContext,e);
            }
        });
    }

    /**
     * 转换为ChatRequest
     * 
     * 注意：不再在这里构建ChatOptions，而是传递原始参数给ModelClient处理
     * ModelClient会在运行时根据具体模型使用相应的OptionsHandler构建运行时选项
     */
    private ChatRequest convertToChatRequest(Map<String, Object> requestParams) {
        ChatRequest.Builder builder = ChatRequest.builder();

        // 处理messages
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messages = (List<Map<String, Object>>) requestParams.get("messages");
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter: messages");
        }

        for (Map<String, Object> msg : messages) {
            String role = (String) msg.get("role");
            String content = (String) msg.get("content");

            if (role == null || content == null) {
                continue;
            }

            switch (role.toLowerCase()) {
                case "user":
                    builder.message(new UserMessage(content));
                    break;
                case "assistant":
                    builder.message(new AssistantMessage(content));
                    break;
                case "system":
                    builder.message(new SystemMessage(content));
                    break;
            }
        }

        // 传递原始参数，让ModelClient根据具体模型处理选项
        builder.rawParameters(requestParams);

        return builder.build();
    }

    /**
     * 转换ChatResponse为OpenAI格式
     */
    private Map<String, Object> convertChatResponse(ChatResponse chatResponse, boolean isStream) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "chatcmpl-" + UUID.randomUUID().toString());
        response.put("object", isStream ? "chat.completion.chunk" : "chat.completion");
        response.put("created", System.currentTimeMillis() / 1000);
        response.put("model", "unknown");

        List<Map<String, Object>> choices = new ArrayList<>();
        if (chatResponse.getResult() != null) {
            Map<String, Object> choice = new HashMap<>();
            choice.put("index", 0);
            choice.put("finish_reason", isStream ? null : "stop");

            String content = "";
            try {
                if (chatResponse.getResult().getOutput() != null) {
                    content = chatResponse.getResult().getOutput().getText();
                }
            } catch (Exception e) {
                logger.warn("Failed to get content from chat response", e);
                content = "";
            }

            if (isStream) {
                Map<String, Object> delta = new HashMap<>();
                delta.put("content", content);
                choice.put("delta", delta);
            } else {
                Map<String, Object> message = new HashMap<>();
                message.put("role", "assistant");
                message.put("content", content);
                choice.put("message", message);
            }

            choices.add(choice);
        }
        response.put("choices", choices);

        if (!isStream) {
            Map<String, Object> usage = new HashMap<>();
            usage.put("prompt_tokens", 0);
            usage.put("completion_tokens", 0);
            usage.put("total_tokens", 0);
            response.put("usage", usage);
        }

        return response;
    }

    /**
     * Complete async context and send error response
     */
    private void completeAsyncWithError(AsyncContext asyncContext, Throwable error) {
        try {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            if (!response.isCommitted()) {
                sendErrorResponse(response, error.getMessage(), 500);
            }
        } catch (Exception e) {
            logger.error("Error sending error response", e);
        } finally {
            try {
                asyncContext.complete();
            } catch (Exception e) {
                logger.error("Error completing async context", e);
            }
        }
    }
}
