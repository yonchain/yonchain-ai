package com.yonchain.ai.openai;

import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.observation.ObservationRegistry;

import java.util.HashMap;

/**
 * OpenAI聊天配置类
 * 使用构建者模式提供OpenAiChatModel的创建和获取
 */
public class OpenAiChatConfiguration {

    // 服务依赖
    protected ToolCallingManager toolCallingManager;
    protected RetryTemplate retryTemplate;
    protected ResponseErrorHandler responseErrorHandler;
    protected RestClient.Builder restClientBuilder;
    protected WebClient.Builder webClientBuilder;
    protected ObservationRegistry observationRegistry;
    protected ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate;
    protected ChatModelObservationConvention observationConvention;

    // 配置参数
    protected String apiKey;
    protected String baseUrl;
    protected String model = "gpt-4o-mini";
    protected Double temperature;
    protected String completionsPath;
    protected String embeddingsPath;
    protected OpenAiChatOptions defaultOptions;

    // 创建的实例
    protected OpenAiApi openAiApi;
    protected OpenAiChatModel openAiChatModel;

    /**
     * 受保护的构造函数，通过Builder创建实例
     */
    protected OpenAiChatConfiguration(Builder builder) {
        this.toolCallingManager = builder.toolCallingManager;
        this.retryTemplate = builder.retryTemplate;
        this.responseErrorHandler = builder.responseErrorHandler;
        this.restClientBuilder = builder.restClientBuilder;
        this.webClientBuilder = builder.webClientBuilder;
        this.observationRegistry = builder.observationRegistry;
        this.toolExecutionEligibilityPredicate = builder.toolExecutionEligibilityPredicate;
        this.observationConvention = builder.observationConvention;
        
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
        this.model = builder.model;
        this.temperature = builder.temperature;
        this.completionsPath = builder.completionsPath;
        this.embeddingsPath = builder.embeddingsPath;
        this.defaultOptions = builder.defaultOptions;

        init();
    }

    /**
     * 初始化方法，创建OpenAI API和ChatModel实例
     */
    protected void init() {
        // 创建OpenAI API客户端
        if (this.openAiApi == null) {
            this.openAiApi = createOpenAiApi();
        }

        // 创建OpenAI聊天模型
        if (this.openAiChatModel == null) {
            this.openAiChatModel = createOpenAiChatModel();
        }

        if (toolCallingManager == null) {
            this.toolCallingManager = ToolCallingManager.builder().build();
        }

        if (retryTemplate == null) {
            this.retryTemplate = RetryTemplate.defaultInstance();
        }

        if (observationRegistry == null) {
            this.observationRegistry = ObservationRegistry.NOOP;
        }

        if (toolExecutionEligibilityPredicate == null) {
            this.toolExecutionEligibilityPredicate = new DefaultToolExecutionEligibilityPredicate();
        }

        if (restClientBuilder == null) {
            this.restClientBuilder = RestClient.builder();
        }

        if (webClientBuilder == null) {
            this.webClientBuilder = WebClient.builder();
        }
    }

    /**
     * 获取OpenAI聊天模型实例
     * 
     * @return OpenAI聊天模型
     */
    public OpenAiChatModel getOpenAiChatModel() {
        return openAiChatModel;
    }

    /**
     * 获取OpenAI API客户端实例
     * 
     * @return OpenAI API客户端
     */
    public OpenAiApi getOpenAiApi() {
        return openAiApi;
    }

    /**
     * 创建OpenAI API客户端
     * 
     * @return OpenAI API客户端
     */
    protected OpenAiApi createOpenAiApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(new SimpleApiKey(apiKey))
                .headers(CollectionUtils.toMultiValueMap(new HashMap<>()))
                .completionsPath(completionsPath)
                .embeddingsPath(embeddingsPath)
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .responseErrorHandler(responseErrorHandler)
                .build();
    }

    /**
     * 创建OpenAI聊天模型
     * 
     * @return OpenAI聊天模型
     */
    protected OpenAiChatModel createOpenAiChatModel() {
        // 创建聊天选项
        OpenAiChatOptions options = defaultOptions != null ? defaultOptions : 
                OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature != null ? temperature : 0.7f)
                        .build();
        
        // 创建聊天模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .toolCallingManager(toolCallingManager)
                .toolExecutionEligibilityPredicate(toolExecutionEligibilityPredicate)
                .retryTemplate(retryTemplate)
                .observationRegistry(observationRegistry)
                .build();
        
        // 设置观察约定
        if (observationConvention != null) {
            chatModel.setObservationConvention(observationConvention);
        }
        
        return chatModel;
    }

    /**
     * 创建一个Builder实例
     *
     * @return 新的Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * OpenAiChatConfiguration的Builder类
     */
    public static class Builder {

        // 服务依赖
        protected ToolCallingManager toolCallingManager;
        protected RetryTemplate retryTemplate;
        protected ResponseErrorHandler responseErrorHandler;
        protected RestClient.Builder restClientBuilder;
        protected WebClient.Builder webClientBuilder;
        protected ObservationRegistry observationRegistry;
        protected ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate;
        protected ChatModelObservationConvention observationConvention;

        // 配置参数
        protected String apiKey;
        protected String baseUrl;
        protected String model;
        protected Double temperature;
        protected String completionsPath;
        protected String embeddingsPath;
        protected OpenAiChatOptions defaultOptions;

        protected Builder() {
            // 受保护的构造函数，允许子类继承
        }

        public Builder toolCallingManager(ToolCallingManager toolCallingManager) {
            this.toolCallingManager = toolCallingManager;
            return this;
        }

        public Builder retryTemplate(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            this.responseErrorHandler = responseErrorHandler;
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

        public Builder observationRegistry(ObservationRegistry observationRegistry) {
            this.observationRegistry = observationRegistry;
            return this;
        }

        public Builder toolExecutionEligibilityPredicate(ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate) {
            this.toolExecutionEligibilityPredicate = toolExecutionEligibilityPredicate;
            return this;
        }

        public Builder observationConvention(ChatModelObservationConvention observationConvention) {
            this.observationConvention = observationConvention;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder completionsPath(String completionsPath) {
            this.completionsPath = completionsPath;
            return this;
        }

        public Builder embeddingsPath(String embeddingsPath) {
            this.embeddingsPath = embeddingsPath;
            return this;
        }

        public Builder defaultOptions(OpenAiChatOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

/*        *//**
         * 从ObjectProvider设置依赖
         *//*
        public Builder fromProviders(
                ToolCallingManager toolCallingManager,
                RetryTemplate retryTemplate,
                ResponseErrorHandler responseErrorHandler,
                ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                ObjectProvider<WebClient.Builder> webClientBuilderProvider,
                ObjectProvider<ObservationRegistry> observationRegistryProvider,
                ObjectProvider<ToolExecutionEligibilityPredicate> toolExecutionEligibilityPredicateProvider,
                ObjectProvider<ChatModelObservationConvention> observationConventionProvider) {

            this.toolCallingManager = toolCallingManager;
            this.retryTemplate = retryTemplate;
            this.responseErrorHandler = responseErrorHandler;
            this.restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
            this.webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
            this.observationRegistry = observationRegistryProvider.getIfUnique(() -> ObservationRegistry.NOOP);
            this.toolExecutionEligibilityPredicate = toolExecutionEligibilityPredicateProvider.getIfUnique(DefaultToolExecutionEligibilityPredicate::new);
            this.observationConvention = observationConventionProvider.getIfAvailable();
            
            return this;
        }*/

        /**
         * 构建OpenAiChatConfiguration实例
         *
         * @return 新的OpenAiChatConfiguration实例
         * @throws IllegalStateException 如果配置无效
         */
        public OpenAiChatConfiguration build() {
            validate();
            return new OpenAiChatConfiguration(this);
        }

        /**
         * 验证Builder配置是否有效
         */
        protected void validate() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("API密钥不能为空");
            }
        }
    }
}
