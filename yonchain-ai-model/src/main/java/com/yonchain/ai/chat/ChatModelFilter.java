package com.yonchain.ai.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChatModelFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ChatModelFilter.class);

    private static final String DEFAULT_MODEL_ENDPOINT_URI = "/chat/completions";

    private final ChatModelService chatModelService;

    private final ObjectMapper objectMapper;

    public ChatModelFilter(ChatModelService chatModelService, ObjectMapper objectMapper) {
        this.chatModelService = chatModelService;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Only handle chat completions requests
        if (!requestURI.contains(DEFAULT_MODEL_ENDPOINT_URI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only handle POST requests
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            // 1. Parse request
            ChatRequest chatRequest = parseChatRequest(request);
            log.debug("Parsed chat request for model: {}", chatRequest.getModel());

            // 2. Get model instance
            ChatModel chatModel = chatModelService.getModel(chatRequest.getModel());
            if (chatModel == null) {
                sendErrorResponse(response, "Model not found: " + chatRequest.getModel(), 404);
                return;
            }

            // 3. Build Spring AI Prompt
            Prompt prompt = buildPrompt(chatRequest);

            // 4. Call model - Use async processing for streaming to avoid IllegalStateException
            if (chatRequest.isStream()) {
                handleStreamRequest(chatModel, prompt, request, response);
            } else {
                handleSyncRequest(chatModel, prompt, response);
            }

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            sendErrorResponse(response, "Internal server error: " + e.getMessage(), 500);
        }

    }

    /**
     * Handle stream request using Servlet async processing
     * This approach prevents IllegalStateException: The response object has been recycled
     */
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
                                log.error("Error writing stream response", e);
                                completeAsyncWithError(asyncContext, e);
                            }
                        },
                        error -> {
                            log.error("Error in stream chat request", error);
                            try {
                                writer.write("data: [DONE]\n\n");
                                writer.flush();
                            } catch (Exception e) {
                                log.error("Error closing stream on error", e);
                            } finally {
                                asyncContext.complete();
                            }
                        },
                        () -> {
                            try {
                                writer.write("data: [DONE]\n\n");
                                writer.flush();
                                log.debug("Stream chat request completed");
                            } catch (Exception e) {
                                log.error("Error closing stream on completion", e);
                            } finally {
                                asyncContext.complete();
                            }
                        }
                );

            } catch (Exception e) {
                log.error("Error in async stream processing", e);
                completeAsyncWithError(asyncContext, e);
            }
        }).exceptionally(throwable -> {
            log.error("Async processing failed", throwable);
            completeAsyncWithError(asyncContext, throwable);
            return null;
        });
    }

    /**
     * Handle sync request
     */
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

            log.debug("Chat request completed successfully");

        } catch (Exception e) {
            log.error("Error in sync chat request", e);
            throw e;
        }
    }

    /**
     * Parse chat request
     */
    private ChatRequest parseChatRequest(HttpServletRequest request) throws IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining("\n"));

        @SuppressWarnings("unchecked")
        Map<String, Object> requestMap = objectMapper.readValue(requestBody, Map.class);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel((String) requestMap.get("model"));
        chatRequest.setStream(Boolean.TRUE.equals(requestMap.get("stream")));

        // Parse messages
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messagesData = (List<Map<String, Object>>) requestMap.get("messages");
        if (messagesData != null) {
            List<ChatMessage> messages = new ArrayList<>();
            for (Map<String, Object> messageData : messagesData) {
                ChatMessage message = new ChatMessage();
                message.setRole((String) messageData.get("role"));
                message.setContent((String) messageData.get("content"));
                messages.add(message);
            }
            chatRequest.setMessages(messages);
        }

        // Extract runtime parameters
        Map<String, Object> runtimeParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
            String key = entry.getKey();
            if (!Arrays.asList("model", "messages", "stream").contains(key)) {
                runtimeParams.put(key, entry.getValue());
            }
        }
        chatRequest.setRuntimeParameters(runtimeParams);

        return chatRequest;
    }

    /**
     * Build Spring AI Prompt
     */
    private Prompt buildPrompt(ChatRequest chatRequest) {
        // 1. Convert message format
        List<Message> springAiMessages = chatRequest.getMessages().stream()
                .map(this::convertToSpringAiMessage)
                .collect(Collectors.toList());

        // 2. Build ChatOptions intelligently
        //TODO
        ChatOptions runtimeChatOptions = ChatOptions.builder().build();

        // 3. Create Prompt
        return new Prompt(springAiMessages, runtimeChatOptions);
    }

    /**
     * Convert to Spring AI Message
     */
    private Message convertToSpringAiMessage(ChatMessage message) {
        String role = message.getRole().toLowerCase();
        String content = message.getContent();

        switch (role) {
            case "user":
                return new UserMessage(content);
            case "assistant":
                return new AssistantMessage(content);
            case "system":
                return new SystemMessage(content);
            default:
                log.warn("Unknown message role: {}, treating as user message", role);
                return new UserMessage(content);
        }
    }

    /**
     * Convert ChatResponse to standard format
     */
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
                log.warn("Failed to get content from chat response", e);
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


    /**
     * Complete async context and send error response
     */
    private void completeAsyncWithError(AsyncContext asyncContext, Throwable error) {
        try {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            if (!response.isCommitted()) {
                sendErrorResponse(response, "Stream processing error: " + error.getMessage(), 500);
            }
        } catch (Exception e) {
            log.error("Error sending error response", e);
        } finally {
            try {
                asyncContext.complete();
            } catch (Exception e) {
                log.error("Error completing async context", e);
            }
        }
    }

    /**
     * Send error response
     */
    private void sendErrorResponse(HttpServletResponse response, String errorMessage, int statusCode)
            throws IOException {

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        error.put("message", errorMessage);
        error.put("type", "invalid_request_error");
        errorResponse.put("error", error);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * Chat Request Class
     */
    public static class ChatRequest {
        private String model;
        private List<ChatMessage> messages;
        private boolean stream;
        private Map<String, Object> runtimeParameters = new HashMap<>();

        // Getter and Setter methods
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public List<ChatMessage> getMessages() { return messages; }
        public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

        public boolean isStream() { return stream; }
        public void setStream(boolean stream) { this.stream = stream; }

        public Map<String, Object> getRuntimeParameters() { return runtimeParameters; }
        public void setRuntimeParameters(Map<String, Object> runtimeParameters) { this.runtimeParameters = runtimeParameters; }
    }

    /**
     * Chat Message Class
     */
    public static class ChatMessage {
        private String role;
        private String content;

        // Getter and Setter methods
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
