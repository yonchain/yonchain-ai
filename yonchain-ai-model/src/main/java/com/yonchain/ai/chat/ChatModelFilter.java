/*package com.yonchain.ai.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.filter.BaseModelFilter;
import com.yonchain.ai.model.ModelService;
import com.yonchain.ai.plugin.spi.ModelNameUtils;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

*//**
 * 聊天模型过滤器
 * 处理聊天完成请求 /chat/completions
 *//*
@Component
public class ChatModelFilter extends BaseModelFilter<ChatModel> {*/

  //  private static final Pattern ENDPOINT_PATTERN = Pattern.compile(".*/chat/completions$");

  /*  private final ChatModelService chatModelService;

    public ChatModelFilter(ChatModelService chatModelService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.chatModelService = chatModelService;
    }


    @Override
    protected Pattern getEndpointPattern() {
        return ENDPOINT_PATTERN;
    }
    
    @Override
    protected ModelService<ChatModel> getModelService() {
        return chatModelService;
    }*/
    
/*    @Override
    protected void handleModelRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // 1. 解析聊天请求
            Map<String, Object> requestParams = parseRequestBody(request);
            String modelName = extractModelName(requestParams);
            
            logger.debug("Processing chat request for model: {}", modelName);
            
            // 2. 获取模型实例
            ChatModel chatModel = chatModelService.getModel(modelName);
            if (chatModel == null) {
                sendErrorResponse(response, "Model not found: " + modelName, 404);
                return;
            }
            
            // 3. 构建Spring AI提示
            Prompt prompt = buildPrompt(requestParams);
            
            // 4. 处理请求 - 流式或同步
            if (isStreamRequest(requestParams)) {
                handleStreamRequest(chatModel, prompt, request, response);
            } else {
                handleSyncRequest(chatModel, prompt, response);
            }
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            sendErrorResponse(response, e.getMessage(), 400);
        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            sendErrorResponse(response, "Failed to process chat request: " + e.getMessage(), 500);
        }
    }*/
/*
    *//**
     * Handle stream request using Servlet async processing
     * This approach prevents IllegalStateException: The response object has been recycled
     *//*
    private void handleStreamRequest(ChatModel chatModel, Prompt prompt,
                                     HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Start async context - this keeps the response object alive
        AsyncContext asyncContext = request.startAsync(request, response);
        asyncContext.setTimeout(300000); // 5 minutes timeout

        // Set response headers
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // Process stream response asynchronously
        CompletableFuture.runAsync(() -> {
            PrintWriter responseWriter = null;
            try {
                responseWriter = response.getWriter();
                final PrintWriter writer = responseWriter; // Final reference for lambda

                // Call Spring AI stream interface
                chatModel.stream(prompt).subscribe(
                        chatResponse -> {
                            try {
                                // Convert response format
                                Map<String, Object> responseMap = convertChatResponse(chatResponse);
                                String jsonResponse = objectMapper.writeValueAsString(responseMap);

                                // Write SSE format - using final writer reference
                                writer.write("data: " + jsonResponse + "\n\n");
                                writer.flush();

                            } catch (Exception e) {
                                logger.error("Error writing stream response", e);
                                completeAsyncWithError(asyncContext, e);
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
                completeAsyncWithError(asyncContext, e);
            }
        }).exceptionally(throwable -> {
            logger.error("Async processing failed", throwable);
            completeAsyncWithError(asyncContext, throwable);
            return null;
        });
    }

    *//**
     * Handle sync request
     *//*
    private void handleSyncRequest(ChatModel chatModel, Prompt prompt, HttpServletResponse response)
            throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try {
            // Call Spring AI
            ChatResponse chatResponse = chatModel.call(prompt);

            // Convert response format
            Map<String, Object> responseMap = convertChatResponse(chatResponse);

            // Write response
            String jsonResponse = objectMapper.writeValueAsString(responseMap);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            logger.debug("Chat request completed successfully");

        } catch (Exception e) {
            logger.error("Error in sync chat request", e);
            throw e;
        }
    }

    *//**
     * 构建Spring AI提示
     * 
     * @param requestParams 请求参数
     * @return Spring AI提示对象
     *//*
    private Prompt buildPrompt(Map<String, Object> requestParams) {
        // 1. 解析消息
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messagesData = (List<Map<String, Object>>) requestParams.get("messages");
        if (messagesData == null || messagesData.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter: messages");
        }
        
        List<Message> springAiMessages = messagesData.stream()
                .map(this::convertToSpringAiMessage)
                .collect(Collectors.toList());
        
        // 2. 构建ChatOptions
        ChatOptions.Builder optionsBuilder = ChatOptions.builder();
        
        // 设置温度参数
        Object temperature = requestParams.get("temperature");
        if (temperature instanceof Number) {
            optionsBuilder.temperature(((Number) temperature).doubleValue());
        }
        
        // 设置最大令牌数
        Object maxTokens = requestParams.get("max_tokens");
        if (maxTokens instanceof Number) {
            optionsBuilder.maxTokens(((Number) maxTokens).intValue());
        }
        
        // 设置top_p
        Object topP = requestParams.get("top_p");
        if (topP instanceof Number) {
            optionsBuilder.topP(((Number) topP).doubleValue());
        }
        
        // 设置频率惩罚
        Object frequencyPenalty = requestParams.get("frequency_penalty");
        if (frequencyPenalty instanceof Number) {
            optionsBuilder.frequencyPenalty(((Number) frequencyPenalty).doubleValue());
        }
        
        // 设置存在惩罚
        Object presencePenalty = requestParams.get("presence_penalty");
        if (presencePenalty instanceof Number) {
            optionsBuilder.presencePenalty(((Number) presencePenalty).doubleValue());
        }
        
        ChatOptions chatOptions = optionsBuilder.build();
        
        // 3. 创建提示
        return new Prompt(springAiMessages, chatOptions);
    }*/
/*

    */
/**
     * 转换为Spring AI消息格式
     * 
     * @param messageData 消息数据
     * @return Spring AI消息对象
     *//*

    private Message convertToSpringAiMessage(Map<String, Object> messageData) {
        String role = (String) messageData.get("role");
        String content = (String) messageData.get("content");
        
        if (role == null || content == null) {
            throw new IllegalArgumentException("Message must have both 'role' and 'content' fields");
        }
        
        switch (role.toLowerCase()) {
            case "user":
                return new UserMessage(content);
            case "assistant":
                return new AssistantMessage(content);
            case "system":
                return new SystemMessage(content);
            default:
                logger.warn("Unknown message role: {}, treating as user message", role);
                return new UserMessage(content);
        }
    }

    */
/**
     * Convert ChatResponse to standard format
     *//*

    private Map<String, Object> convertChatResponse(ChatResponse chatResponse) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", "chatcmpl-" + UUID.randomUUID().toString());
        responseMap.put("object", "chat.completion");
        responseMap.put("created", System.currentTimeMillis() / 1000);
        responseMap.put("model", "unknown"); // Can get model info from chatResponse here

        // Convert choices
        List<Map<String, Object>> choices = new ArrayList<>();
        if (chatResponse.getResult() != null) {
            Map<String, Object> choice = new HashMap<>();
            choice.put("index", 0);
            choice.put("finish_reason", "stop");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "assistant");
            // Safely get content
            String content = "";
            try {
                if (chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                    content = chatResponse.getResult().getOutput().getText();
                }
            } catch (Exception e) {
                logger.warn("Failed to get content from chat response", e);
                content = "Response content unavailable";
            }
            message.put("content", content);
            choice.put("message", message);

            choices.add(choice);
        }
        responseMap.put("choices", choices);

        // Usage info
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", 0);
        usage.put("completion_tokens", 0);
        usage.put("total_tokens", 0);
        responseMap.put("usage", usage);

        return responseMap;
    }


    */
/**
     * Complete async context and send error response
     *//*

    private void completeAsyncWithError(AsyncContext asyncContext, Throwable error) {
        try {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            if (!response.isCommitted()) {
                sendErrorResponse(response, "Stream processing error: " + error.getMessage(), 500);
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
*/

//}
