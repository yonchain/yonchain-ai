/*
 * Copyright 2023-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yonchain.ai.dify.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yonchain.ai.app.ApiKey;
import com.yonchain.ai.app.AppOptionsUtils;
import com.yonchain.ai.app.NoopApiKey;
import com.yonchain.ai.app.SimpleApiKey;
import com.yonchain.ai.retry.RetryUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Dify AI API client implementation for conversational applications.
 */
public class DifyApi {

    /**
     * Returns a builder pre-populated with the current configuration for mutation.
     */
    public Builder mutate() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final String DEFAULT_BASE_URL = "http://42.194.189.63/v1";

    private static final Predicate<String> SSE_DONE_PREDICATE = "[DONE]"::equals;

    // Store config fields for mutate/copy
    private final String baseUrl;
    private final ApiKey apiKey;
    private final MultiValueMap<String, String> headers;
    private final String chatMessagesPath;
    private final String filesUploadPath;
    private final String stopResponsePath;
    private final ResponseErrorHandler responseErrorHandler;
    private final RestClient restClient;
    private final WebClient webClient;

    private DifyStreamFunctionCallingHelper chunkMerger = new DifyStreamFunctionCallingHelper();

    /**
     * Create a new Dify API client.
     */
    public DifyApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers,
                   String chatMessagesPath, String filesUploadPath, String stopResponsePath,
                   RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder,
                   ResponseErrorHandler responseErrorHandler) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.headers = headers;
        this.chatMessagesPath = chatMessagesPath;
        this.filesUploadPath = filesUploadPath;
        this.stopResponsePath = stopResponsePath;
        this.responseErrorHandler = responseErrorHandler;

        Assert.hasText(chatMessagesPath, "Chat Messages Path must not be null");
        Assert.hasText(filesUploadPath, "Files Upload Path must not be null");
        Assert.hasText(stopResponsePath, "Stop Response Path must not be null");
        Assert.notNull(headers, "Headers must not be null");

        // Configure clients
        Consumer<HttpHeaders> finalHeaders = h -> {
            h.setContentType(MediaType.APPLICATION_JSON);
            h.addAll(headers);
        };

        this.restClient = restClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(finalHeaders)
                .defaultStatusHandler(responseErrorHandler)
                .build();

        this.webClient = webClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(finalHeaders)
                .build();
    }


    /**
     * Creates a chat message with the given request in blocking mode.
     *
     * @param request The chat message request. Must have responseMode set to BLOCKING.
     * @return ResponseEntity with ChatCompletion.
     */
    public ResponseEntity<ChatCompletionResponse> createChatMessageEntity(ChatCompletionRequest request) {
        Assert.notNull(request, "Request must not be null");
        Assert.isTrue(request.responseMode() == ResponseMode.BLOCKING,
                "Request must have responseMode set to BLOCKING");

        return this.restClient.post()
                .uri(this.chatMessagesPath)
                .headers(this::addDefaultHeadersIfMissing)
                .body(request)
                .retrieve()
                .toEntity(ChatCompletionResponse.class);
    }

    /**
     * Creates a streaming chat message with the given request.
     *
     * @param request The chat message request. Must have responseMode set to STREAMING.
     * @return Flux stream of ChunkChatCompletion.
     */
    public Flux<ChatCompletionResponse> createChatMessageStream(ChatCompletionRequest request) {
        Assert.notNull(request, "Request must not be null");
        Assert.isTrue(request.responseMode() == ResponseMode.STREAMING,
                "Request must have responseMode set to STREAMING");

        AtomicBoolean isInsideTool = new AtomicBoolean(false);

        return this.webClient.post()
                .uri(this.chatMessagesPath)
                .headers(this::addDefaultHeadersIfMissing)
                .body(Mono.just(request), ChatCompletionRequest.class)
                .retrieve()
                .bodyToFlux(String.class)
                .takeUntil(SSE_DONE_PREDICATE)
                .filter(SSE_DONE_PREDICATE.negate())
                .map(content -> AppOptionsUtils.jsonToObject(content, ChatCompletionResponse.class))
                /*// 可选：添加与OpenAiApi类似的工具调用处理逻辑
                .map(chunk -> {
                    if (this.chunkMerger.isStreamingToolFunctionCall(chunk)) {
                        isInsideTool.set(true);
                    }
                    return chunk;
                })
                .windowUntil(chunk -> {
                    if (isInsideTool.get() && this.chunkMerger.isStreamingToolFunctionCallFinish(chunk)) {
                        isInsideTool.set(false);
                        return true;
                    }
                    return !isInsideTool.get();
                })*/
                .concatMapIterable(window -> {
                    Mono<ChatCompletionResponse> monoChunk = null;
                   /*   window.reduce(
                            new ChunkChatCompletion(null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                            (previous, current) -> this.chunkMerger.merge(previous, current));*/
                    return List.of(monoChunk);
                })
                .flatMap(mono -> mono);
    }


    /**
     * Uploads a file for use in chat messages.
     *
     * @param file The file to upload.
     * @param user The user identifier.
     * @return ResponseEntity with FileUploadResponse.
     */
    public ResponseEntity<FileUploadResponse> uploadFile(MultipartFile file, String user) {
        Assert.notNull(file, "File must not be null");
        Assert.hasText(user, "User must not be empty");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("user", user);

        return this.restClient.post()
                .uri(this.filesUploadPath)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toEntity(FileUploadResponse.class);
    }

    /**
     * Stops a streaming response.
     *
     * @param taskId The task ID to stop.
     * @param user   The user identifier.
     * @return ResponseEntity with the stop result.
     */
    public ResponseEntity<Map<String, String>> stopResponse(String taskId, String user) {
        Assert.hasText(taskId, "Task ID must not be empty");
        Assert.hasText(user, "User must not be empty");

        return this.restClient.post()
                .uri(this.stopResponsePath, taskId)
                .headers(this::addDefaultHeadersIfMissing)
                .body(new StopResponseRequest(user))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Submits feedback for a message.
     *
     * @param messageId The message ID to provide feedback for.
     * @param request   The feedback request.
     * @return ResponseEntity with FeedbackResponse.
     */
    public ResponseEntity<FeedbackResponse> submitFeedback(String messageId, FeedbackRequest request) {
        Assert.hasText(messageId, "Message ID must not be empty");
        Assert.notNull(request, "Request must not be null");

        return this.restClient.post()
                .uri("/messages/{message_id}/feedbacks", messageId)
                .headers(this::addDefaultHeadersIfMissing)
                .body(request)
                .retrieve()
                .toEntity(FeedbackResponse.class);
    }

    /**
     * Gets conversation messages.
     *
     * @param conversationId The conversation ID.
     * @param user           The user identifier.
     * @param firstId        The first message ID for pagination.
     * @param limit          The maximum number of messages to return.
     * @return ResponseEntity with the list of messages.
     */
    public ResponseEntity<Map<String, Object>> getMessages(String conversationId, String user,
                                                           String firstId, Integer limit) {
        Assert.hasText(conversationId, "Conversation ID must not be empty");
        Assert.hasText(user, "User must not be empty");

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("conversation_id", conversationId);
        queryParams.add("user", user);
        if (firstId != null) queryParams.add("first_id", firstId);
        if (limit != null) queryParams.add("limit", limit.toString());

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/messages")
                        .queryParams(queryParams)
                        .build())
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    // 获取建议问题列表
    public ResponseEntity<SuggestedQuestionsResponse> getSuggestedQuestions(String messageId, String user) {
        Assert.hasText(messageId, "Message ID must not be empty");
        Assert.hasText(user, "User must not be empty");

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/messages/{message_id}/suggested")
                        .queryParam("user", user)
                        .build(messageId))
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(SuggestedQuestionsResponse.class);
    }


    // 获取会话列表
    public ResponseEntity<ConversationsResponse> getConversations(String user, String lastId,
                                                                  Integer limit, String sortBy) {
        Assert.hasText(user, "User must not be empty");

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("user", user);
        if (lastId != null) queryParams.add("last_id", lastId);
        if (limit != null) queryParams.add("limit", limit.toString());
        if (sortBy != null) queryParams.add("sort_by", sortBy);

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/conversations")
                        .queryParams(queryParams)
                        .build())
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(ConversationsResponse.class);
    }

    // 删除会话 - 修改为使用body方法而不是bodyValue
    public ResponseEntity<Void> deleteConversation(String conversationId, String user) {
        Assert.hasText(conversationId, "Conversation ID must not be empty");
        Assert.hasText(user, "User must not be empty");

        Map<String, String> requestBody = Map.of("user", user);

        return this.restClient.method(HttpMethod.DELETE)
                .uri("/conversations/{conversation_id}", conversationId)
                .headers(this::addDefaultHeadersIfMissing)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }

    // 会话重命名 - 修改为使用body方法而不是bodyValue
    public ResponseEntity<ConversationsResponse.Conversation> renameConversation(
            String conversationId, String name, Boolean autoGenerate, String user) {
        Assert.hasText(conversationId, "Conversation ID must not be empty");
        Assert.hasText(user, "User must not be empty");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user", user);
        if (name != null) requestBody.put("name", name);
        if (autoGenerate != null) requestBody.put("auto_generate", autoGenerate);

        return this.restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/conversations/{conversation_id}/name")
                        .build(conversationId))
                .headers(this::addDefaultHeadersIfMissing)
                .body(requestBody)  // 使用body方法而不是bodyValue
                .retrieve()
                .toEntity(ConversationsResponse.Conversation.class);
    }

    // 获取对话变量
    public ResponseEntity<VariablesResponse> getConversationVariables(
            String conversationId, String user, String lastId, Integer limit) {
        Assert.hasText(conversationId, "Conversation ID must not be empty");
        Assert.hasText(user, "User must not be empty");

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("user", user);
        if (lastId != null) queryParams.add("last_id", lastId);
        if (limit != null) queryParams.add("limit", limit.toString());

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/conversations/{conversation_id}/variables")
                        .queryParams(queryParams)
                        .build(conversationId))
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(VariablesResponse.class);
    }


    // Builder class
    public static class Builder {
        private String baseUrl = DEFAULT_BASE_URL;
        private ApiKey apiKey;
        private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        private String chatMessagesPath = "/chat-messages";
        private String filesUploadPath = "/files/upload";
        private String stopResponsePath = "/chat-messages/{task_id}/stop";
        private RestClient.Builder restClientBuilder = RestClient.builder();
        private WebClient.Builder webClientBuilder = WebClient.builder();
        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        public Builder() {
        }

        // Copy constructor for mutate()
        public Builder(DifyApi api) {
            this.baseUrl = api.baseUrl;
            this.apiKey = api.apiKey;
            this.headers = new LinkedMultiValueMap<>(api.headers);
            this.chatMessagesPath = api.chatMessagesPath;
            this.filesUploadPath = api.filesUploadPath;
            this.stopResponsePath = api.stopResponsePath;
            this.restClientBuilder = api.restClient != null ? api.restClient.mutate() : RestClient.builder();
            this.webClientBuilder = api.webClient != null ? api.webClient.mutate() : WebClient.builder();
            this.responseErrorHandler = api.responseErrorHandler;
        }

        // Builder methods...
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(ApiKey apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = new SimpleApiKey(apiKey);
            return this;
        }

        public Builder headers(MultiValueMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder chatMessagesPath(String chatMessagesPath) {
            this.chatMessagesPath = chatMessagesPath;
            return this;
        }

        public Builder filesUploadPath(String filesUploadPath) {
            this.filesUploadPath = filesUploadPath;
            return this;
        }

        public Builder stopResponsePath(String stopResponsePath) {
            this.stopResponsePath = stopResponsePath;
            return this;
        }

        public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder webClientBuilder(WebClient.Builder webClientBuilder) {
            this.webClientBuilder = webClientBuilder;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        public DifyApi build() {
            Assert.notNull(this.apiKey, "API Key must be set");
            return new DifyApi(
                    this.baseUrl, this.apiKey, this.headers,
                    this.chatMessagesPath, this.filesUploadPath, this.stopResponsePath,
                    this.restClientBuilder, this.webClientBuilder, this.responseErrorHandler
            );
        }
    }

    private void addDefaultHeadersIfMissing(HttpHeaders headers) {
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION) && !(this.apiKey instanceof NoopApiKey)) {
            headers.setBearerAuth(this.apiKey.getValue());
        }
    }


    /**
     * Response mode for chat messages.
     */
    public enum ResponseMode {
        @JsonProperty("streaming") STREAMING,
        @JsonProperty("blocking") BLOCKING
    }

    /**
     * File transfer method for uploaded files.
     */
    public enum TransferMethod {
        @JsonProperty("remote_url") REMOTE_URL,
        @JsonProperty("local_file") LOCAL_FILE
    }

    /**
     * Chat message request.
     */
    @JsonInclude(Include.NON_NULL)
    public record ChatCompletionRequest(
            @JsonProperty("query") String query,
            @JsonProperty("inputs") Map<String, Object> inputs,
            @JsonProperty("response_mode") ResponseMode responseMode,
            @JsonProperty("user") String user,
            @JsonProperty("conversation_id") String conversationId,
            @JsonProperty("files") List<ChatMessageFile> files,
            @JsonProperty("auto_generate_name") Boolean autoGenerateName
    ) {
        public ChatCompletionRequest(String query, ResponseMode responseMode, String user) {
            this(query, Map.of(), responseMode, user, null, null, true);
        }
    }

    /**
     * File information for chat messages.
     */
    @JsonInclude(Include.NON_NULL)
    public record ChatMessageFile(
            @JsonProperty("type") String type,
            @JsonProperty("transfer_method") TransferMethod transferMethod,
            @JsonProperty("url") String url,
            @JsonProperty("upload_file_id") String uploadFileId
    ) {
    }

    /**
     * Chat completion response for blocking mode.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChatCompletionResponse(
            @JsonProperty("event") String event,
            @JsonProperty("task_id") String taskId,
            @JsonProperty("id") String id,
            @JsonProperty("message_id") String messageId,
            @JsonProperty("conversation_id") String conversationId,
            @JsonProperty("mode") String mode,
            @JsonProperty("answer") String answer,
            @JsonProperty("metadata") Metadata metadata,
            @JsonProperty("created_at") Long createdAt
    ) {
    }

    /**
     * Chunk chat completion response for streaming mode.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChunkChatCompletionResponse(
            @JsonProperty("event") String event,
            @JsonProperty("task_id") String taskId,
            @JsonProperty("message_id") String messageId,
            @JsonProperty("conversation_id") String conversationId,
            @JsonProperty("answer") String answer,
            @JsonProperty("created_at") Long createdAt,
            // Additional fields for different event types...
            @JsonProperty("id") String id,
            @JsonProperty("position") Integer position,
            @JsonProperty("thought") String thought,
            @JsonProperty("observation") String observation,
            @JsonProperty("tool") String tool,
            @JsonProperty("tool_input") String toolInput,
            @JsonProperty("message_files") List<String> messageFiles,
            @JsonProperty("audio") String audio,
            @JsonProperty("status") Integer status,
            @JsonProperty("code") String code,
            @JsonProperty("message") String errorMessage
    ) {
    }

    /**
     * Metadata for chat responses.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            @JsonProperty("usage") Usage usage,
            @JsonProperty("retriever_resources") List<RetrieverResource> retrieverResources
    ) {
    }

    /**
     * Usage information.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("prompt_unit_price") String promptUnitPrice,
            @JsonProperty("prompt_price_unit") String promptPriceUnit,
            @JsonProperty("prompt_price") String promptPrice,
            @JsonProperty("completion_tokens") Integer completionTokens,
            @JsonProperty("completion_unit_price") String completionUnitPrice,
            @JsonProperty("completion_price_unit") String completionPriceUnit,
            @JsonProperty("completion_price") String completionPrice,
            @JsonProperty("total_tokens") Integer totalTokens,
            @JsonProperty("total_price") String totalPrice,
            @JsonProperty("currency") String currency,
            @JsonProperty("latency") Double latency
    ) {
    }

    /**
     * Retriever resource information.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RetrieverResource(
            @JsonProperty("position") Integer position,
            @JsonProperty("dataset_id") String datasetId,
            @JsonProperty("dataset_name") String datasetName,
            @JsonProperty("document_id") String documentId,
            @JsonProperty("document_name") String documentName,
            @JsonProperty("segment_id") String segmentId,
            @JsonProperty("score") Double score,
            @JsonProperty("content") String content
    ) {
    }

    /**
     * File upload response.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FileUploadResponse(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("size") Integer size,
            @JsonProperty("extension") String extension,
            @JsonProperty("mime_type") String mimeType,
            @JsonProperty("created_by") String createdBy,
            @JsonProperty("created_at") Long createdAt
    ) {
    }

    /**
     * Stop response request.
     */
    @JsonInclude(Include.NON_NULL)
    public record StopResponseRequest(
            @JsonProperty("user") String user
    ) {
    }

    /**
     * Feedback request.
     */
    @JsonInclude(Include.NON_NULL)
    public record FeedbackRequest(
            @JsonProperty("rating") String rating,
            @JsonProperty("user") String user,
            @JsonProperty("content") String content
    ) {
    }

    /**
     * Feedback response.
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FeedbackResponse(
            @JsonProperty("result") String result
    ) {
    }

    /**
     * 建议问题列表响应
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SuggestedQuestionsResponse(
            @JsonProperty("result") String result,
            @JsonProperty("data") List<String> data
    ) {
    }

    /**
     * 消息列表响应
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MessagesResponse(
            @JsonProperty("limit") Integer limit,
            @JsonProperty("has_more") Boolean hasMore,
            @JsonProperty("data") List<Message> data
    ) {
        /**
         * 消息详情
         */
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Message(
                @JsonProperty("id") String id,
                @JsonProperty("conversation_id") String conversationId,
                @JsonProperty("inputs") Map<String, Object> inputs,
                @JsonProperty("query") String query,
                @JsonProperty("answer") String answer,
                @JsonProperty("message_files") List<MessageFile> messageFiles,
                @JsonProperty("feedback") Feedback feedback,
                @JsonProperty("retriever_resources") List<RetrieverResource> retrieverResources,
                @JsonProperty("agent_thoughts") List<AgentThought> agentThoughts,
                @JsonProperty("created_at") Long createdAt
        ) {
        }

        /**
         * 消息文件
         */
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record MessageFile(
                @JsonProperty("id") String id,
                @JsonProperty("type") String type,
                @JsonProperty("url") String url,
                @JsonProperty("belongs_to") String belongsTo
        ) {
        }

        /**
         * 反馈信息
         */
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Feedback(
                @JsonProperty("rating") String rating
        ) {
        }

        /**
         * Agent思考内容
         */
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record AgentThought(
                @JsonProperty("id") String id,
                @JsonProperty("message_id") String messageId,
                @JsonProperty("position") Integer position,
                @JsonProperty("thought") String thought,
                @JsonProperty("observation") String observation,
                @JsonProperty("tool") String tool,
                @JsonProperty("tool_input") String toolInput,
                @JsonProperty("created_at") Long createdAt,
                @JsonProperty("files") List<String> files
        ) {
        }
    }

    /**
     * 会话列表响应
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ConversationsResponse(
            @JsonProperty("limit") Integer limit,
            @JsonProperty("has_more") Boolean hasMore,
            @JsonProperty("data") List<Conversation> data
    ) {
        /**
         * 会话详情
         */
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Conversation(
                @JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("inputs") Map<String, Object> inputs,
                @JsonProperty("status") String status,
                @JsonProperty("introduction") String introduction,
                @JsonProperty("created_at") Long createdAt,
                @JsonProperty("updated_at") Long updatedAt
        ) {
        }
    }

    /**
     * 变量列表响应
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VariablesResponse(
            @JsonProperty("limit") Integer limit,
            @JsonProperty("has_more") Boolean hasMore,
            @JsonProperty("data") List<Variable> data
    ) {
        /**
         * 变量详情
         */
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Variable(
                @JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("value_type") String valueType,
                @JsonProperty("value") String value,
                @JsonProperty("description") String description,
                @JsonProperty("created_at") Long createdAt,
                @JsonProperty("updated_at") Long updatedAt
        ) {
        }
    }


}