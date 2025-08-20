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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yonchain.ai.app.ApiKey;
import com.yonchain.ai.app.NoopApiKey;
import com.yonchain.ai.app.SimpleApiKey;
import com.yonchain.ai.retry.RetryUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dify AI App API client implementation for application information and settings.
 * 
 * <p>Authentication:</p>
 * <p>Dify Service API uses API-Key for authentication. It is strongly recommended that developers
 * store the API-Key on the backend rather than sharing or storing it on the client side to prevent
 * API-Key leakage and financial loss.</p>
 * 
 * <p>All API requests should include your API-Key in the Authorization HTTP Header as follows:</p>
 * <pre>
 * Authorization: Bearer {API_KEY}
 * </pre>
 * 
 * <p>This class automatically adds the Authorization header with the API-Key provided during initialization.</p>
 */
public class DifyAppApi {

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

    // Store config fields for mutate/copy
    private final String baseUrl;
    private final ApiKey apiKey;
    private final MultiValueMap<String, String> headers;
    private final String infoPath;
    private final String parametersPath;
    private final String sitePath;
    private final ResponseErrorHandler responseErrorHandler;
    private final RestClient restClient;
    private final WebClient webClient;

    /**
     * Create a new Dify App API client.
     */
    public DifyAppApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers,
                      String infoPath, String parametersPath, String sitePath,
                      RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder,
                      ResponseErrorHandler responseErrorHandler) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.headers = headers;
        this.infoPath = infoPath;
        this.parametersPath = parametersPath;
        this.sitePath = sitePath;
        this.responseErrorHandler = responseErrorHandler;

        Assert.hasText(infoPath, "Info Path must not be null");
        Assert.hasText(parametersPath, "Parameters Path must not be null");
        Assert.hasText(sitePath, "Site Path must not be null");
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
     * Gets application basic information.
     *
     * @return ResponseEntity with AppInfoResponse.
     */
    public ResponseEntity<AppInfoResponse> getAppInfo() {
        return this.restClient.get()
                .uri(this.infoPath)
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(AppInfoResponse.class);
    }

    /**
     * Gets application parameters.
     *
     * @return ResponseEntity with AppParametersResponse.
     */
    public ResponseEntity<AppParametersResponse> getAppParameters() {
        return this.restClient.get()
                .uri(this.parametersPath)
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(AppParametersResponse.class);
    }

    /**
     * Gets application WebApp settings.
     *
     * @return ResponseEntity with AppSiteResponse.
     */
    public ResponseEntity<AppSiteResponse> getAppSite() {
        return this.restClient.get()
                .uri(this.sitePath)
                .headers(this::addDefaultHeadersIfMissing)
                .retrieve()
                .toEntity(AppSiteResponse.class);
    }

    // Builder class
    public static class Builder {
        private String baseUrl = DEFAULT_BASE_URL;
        private ApiKey apiKey;
        private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        private String infoPath = "/info";
        private String parametersPath = "/parameters";
        private String sitePath = "/site";
        private RestClient.Builder restClientBuilder = RestClient.builder();
        private WebClient.Builder webClientBuilder = WebClient.builder();
        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        public Builder() {
        }

        // Copy constructor for mutate()
        public Builder(DifyAppApi api) {
            this.baseUrl = api.baseUrl;
            this.apiKey = api.apiKey;
            this.headers = new LinkedMultiValueMap<>(api.headers);
            this.infoPath = api.infoPath;
            this.parametersPath = api.parametersPath;
            this.sitePath = api.sitePath;
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

        public Builder infoPath(String infoPath) {
            this.infoPath = infoPath;
            return this;
        }

        public Builder parametersPath(String parametersPath) {
            this.parametersPath = parametersPath;
            return this;
        }

        public Builder sitePath(String sitePath) {
            this.sitePath = sitePath;
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

        public DifyAppApi build() {
            Assert.notNull(this.apiKey, "API Key must be set");
            return new DifyAppApi(
                    this.baseUrl, this.apiKey, this.headers,
                    this.infoPath, this.parametersPath, this.sitePath,
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
     * Application information response.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AppInfoResponse(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("mode") String mode,
            @JsonProperty("author_name") String authorName
    ) {
    }

    /**
     * Application parameters response.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AppParametersResponse(
            @JsonProperty("opening_statement") String openingStatement,
            @JsonProperty("suggested_questions") List<String> suggestedQuestions,
            @JsonProperty("suggested_questions_after_answer") SuggestedQuestionsAfterAnswer suggestedQuestionsAfterAnswer,
            @JsonProperty("speech_to_text") SpeechToText speechToText,
            @JsonProperty("text_to_speech") TextToSpeech textToSpeech,
            @JsonProperty("retriever_resource") RetrieverResource retrieverResource,
            @JsonProperty("annotation_reply") AnnotationReply annotationReply,
            @JsonProperty("more_like_this") MoreLikeThis moreLikeThis,
            @JsonProperty("user_input_form") List<Map<String, Object>> userInputForm,
            @JsonProperty("sensitive_word_avoidance") SensitiveWordAvoidance sensitiveWordAvoidance,
            @JsonProperty("file_upload") FileUpload fileUpload,
            @JsonProperty("system_parameters") SystemParameters systemParameters
    ) {
        /**
         * Suggested questions after answer configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record SuggestedQuestionsAfterAnswer(
                @JsonProperty("enabled") Boolean enabled
        ) {
        }

        /**
         * Speech to text configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record SpeechToText(
                @JsonProperty("enabled") Boolean enabled
        ) {
        }

        /**
         * Text to speech configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TextToSpeech(
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("language") String language,
                @JsonProperty("voice") String voice
        ) {
        }

        /**
         * Retriever resource configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RetrieverResource(
                @JsonProperty("enabled") Boolean enabled
        ) {
        }

        /**
         * Annotation reply configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record AnnotationReply(
                @JsonProperty("enabled") Boolean enabled
        ) {
        }

        /**
         * More like this configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record MoreLikeThis(
                @JsonProperty("enabled") Boolean enabled
        ) {
        }

        /**
         * Sensitive word avoidance configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record SensitiveWordAvoidance(
                @JsonProperty("enabled") Boolean enabled
        ) {
        }

        /**
         * File upload configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record FileUpload(
                @JsonProperty("image") ImageUpload image,
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("allowed_file_types") List<String> allowedFileTypes,
                @JsonProperty("allowed_file_extensions") List<String> allowedFileExtensions,
                @JsonProperty("allowed_file_upload_methods") List<String> allowedFileUploadMethods,
                @JsonProperty("number_limits") Integer numberLimits,
                @JsonProperty("fileUploadConfig") FileUploadConfig fileUploadConfig
        ) {
        }

        /**
         * Image upload configuration.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ImageUpload(
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("number_limits") Integer numberLimits,
                @JsonProperty("detail") String detail,
                @JsonProperty("transfer_methods") List<String> transferMethods
        ) {
        }

        /**
         * File upload config.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record FileUploadConfig(
                @JsonProperty("file_size_limit") Integer fileSizeLimit,
                @JsonProperty("batch_count_limit") Integer batchCountLimit,
                @JsonProperty("image_file_size_limit") Integer imageFileSizeLimit,
                @JsonProperty("video_file_size_limit") Integer videoFileSizeLimit,
                @JsonProperty("audio_file_size_limit") Integer audioFileSizeLimit,
                @JsonProperty("workflow_file_upload_limit") Integer workflowFileUploadLimit
        ) {
        }

        /**
         * System parameters.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record SystemParameters(
                @JsonProperty("file_size_limit") Integer fileSizeLimit,
                @JsonProperty("image_file_size_limit") Integer imageFileSizeLimit,
                @JsonProperty("audio_file_size_limit") Integer audioFileSizeLimit,
                @JsonProperty("video_file_size_limit") Integer videoFileSizeLimit,
                @JsonProperty("workflow_file_upload_limit") Integer workflowFileUploadLimit
        ) {
        }
    }

    /**
     * Application WebApp settings response.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AppSiteResponse(
            @JsonProperty("title") String title,
            @JsonProperty("icon_type") String iconType,
            @JsonProperty("icon") String icon,
            @JsonProperty("icon_background") String iconBackground,
            @JsonProperty("description") String description,
            @JsonProperty("copyright") String copyright,
            @JsonProperty("privacy_policy") String privacyPolicy,
            @JsonProperty("custom_disclaimer") String customDisclaimer,
            @JsonProperty("default_language") String defaultLanguage,
            @JsonProperty("show_workflow_steps") Boolean showWorkflowSteps
    ) {
    }
}
