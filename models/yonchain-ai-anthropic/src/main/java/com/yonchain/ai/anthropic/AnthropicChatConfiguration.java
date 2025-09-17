/*
 * Copyright 2025-2025 the original author or authors.
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

package com.yonchain.ai.anthropic;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.observation.ObservationRegistry;

/**
 * Anthropic聊天配置类
 * 使用构建者模式提供AnthropicChatModel的创建和获取
 */
public class AnthropicChatConfiguration {

    // 服务依赖
    protected RetryTemplate retryTemplate;
    protected ResponseErrorHandler responseErrorHandler;
    protected RestClient.Builder restClientBuilder;
    protected WebClient.Builder webClientBuilder;
    protected ObservationRegistry observationRegistry;
    protected ChatModelObservationConvention observationConvention;

    // 配置参数
    protected String apiKey;
    protected String baseUrl;
    protected String model = "claude-3-sonnet-20240229";
    protected Double temperature;
    protected AnthropicChatOptions defaultOptions;

    // 创建的实例
    protected AnthropicApi anthropicApi;
    protected AnthropicChatModel anthropicChatModel;

    /**
     * 受保护的构造函数，通过Builder创建实例
     */
    protected AnthropicChatConfiguration(Builder builder) {
        this.retryTemplate = builder.retryTemplate;
        this.responseErrorHandler = builder.responseErrorHandler;
        this.restClientBuilder = builder.restClientBuilder;
        this.webClientBuilder = builder.webClientBuilder;
        this.observationRegistry = builder.observationRegistry;
        this.observationConvention = builder.observationConvention;
        
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
        this.model = builder.model;
        this.temperature = builder.temperature;
        this.defaultOptions = builder.defaultOptions;

        init();
    }

    /**
     * 初始化方法，创建Anthropic API和ChatModel实例
     */
    protected void init() {
        // 创建Anthropic API客户端
        if (this.anthropicApi == null) {
            this.anthropicApi = createAnthropicApi();
        }

        // 创建Anthropic聊天模型
        if (this.anthropicChatModel == null) {
            this.anthropicChatModel = createAnthropicChatModel();
        }
    }

    /**
     * 获取Anthropic聊天模型实例
     * 
     * @return Anthropic聊天模型
     */
    public AnthropicChatModel getAnthropicChatModel() {
        return anthropicChatModel;
    }

    /**
     * 获取Anthropic API客户端实例
     * 
     * @return Anthropic API客户端
     */
    public AnthropicApi getAnthropicApi() {
        return anthropicApi;
    }

    /**
     * 创建Anthropic API客户端
     * 
     * @return Anthropic API客户端
     */
    protected AnthropicApi createAnthropicApi() {
        return AnthropicApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl != null ? baseUrl : "https://api.anthropic.com")
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .responseErrorHandler(responseErrorHandler)
                .build();
    }

    /**
     * 创建Anthropic聊天模型
     * 
     * @return Anthropic聊天模型
     */
    protected AnthropicChatModel createAnthropicChatModel() {
        // 创建聊天选项
        AnthropicChatOptions options = defaultOptions != null ? defaultOptions : 
                AnthropicChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build();
        
        // 创建聊天模型
        AnthropicChatModel chatModel = AnthropicChatModel.builder()
                .anthropicApi(anthropicApi)
                .defaultOptions(options)
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
     * AnthropicChatConfiguration的Builder类
     */
    public static class Builder {

        // 服务依赖
        protected RetryTemplate retryTemplate;
        protected ResponseErrorHandler responseErrorHandler;
        protected RestClient.Builder restClientBuilder;
        protected WebClient.Builder webClientBuilder;
        protected ObservationRegistry observationRegistry;
        protected ChatModelObservationConvention observationConvention;

        // 配置参数
        protected String apiKey;
        protected String baseUrl;
        protected String model;
        protected Double temperature;
        protected AnthropicChatOptions defaultOptions;

        protected Builder() {
            // 受保护的构造函数，允许子类继承
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

        public Builder defaultOptions(AnthropicChatOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        /**
         * 构建AnthropicChatConfiguration实例
         *
         * @return 新的AnthropicChatConfiguration实例
         * @throws IllegalStateException 如果配置无效
         */
        public AnthropicChatConfiguration build() {
            validate();
            return new AnthropicChatConfiguration(this);
        }

        /**
         * 验证Builder配置是否有效
         */
        protected void validate() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("API密钥不能为空");
            }
            
            if (retryTemplate == null) {
                this.retryTemplate = RetryTemplate.defaultInstance();
            }
            
            if (observationRegistry == null) {
                this.observationRegistry = ObservationRegistry.NOOP;
            }
            
            if (restClientBuilder == null) {
                this.restClientBuilder = RestClient.builder();
            }
            
            if (webClientBuilder == null) {
                this.webClientBuilder = WebClient.builder();
            }
        }
    }
}
