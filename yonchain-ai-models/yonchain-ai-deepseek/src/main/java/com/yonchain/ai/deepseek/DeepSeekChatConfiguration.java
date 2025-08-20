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

package com.yonchain.ai.deepseek;

import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import io.micrometer.observation.ObservationRegistry;

/**
 * DeepSeek Chat Configuration
 * 参考OpenAiChatConfiguration实现，使用构建者模式配置DeepSeek Chat模型
 */
public class DeepSeekChatConfiguration {

    // 服务依赖
    protected final ToolCallingManager toolCallingManager;
    protected final RetryTemplate retryTemplate;
    protected final ResponseErrorHandler responseErrorHandler;
    protected final RestClient.Builder restClientBuilder;
    protected final WebClient.Builder webClientBuilder;
    protected final ObservationRegistry observationRegistry;
    protected final ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate;
    protected final ChatModelObservationConvention observationConvention;

    // 配置参数
    protected final String apiKey;
    protected final String baseUrl;
    protected final String model;
    protected final Double temperature;
    protected final DeepSeekChatOptions defaultOptions;

    // 实例缓存
    private DeepSeekApi deepSeekApi;
    private DeepSeekChatModel deepSeekChatModel;

    /**
     * 构造函数
     */
    protected DeepSeekChatConfiguration(Builder builder) {
        // 服务依赖
        this.toolCallingManager = builder.toolCallingManager;
        this.retryTemplate = builder.retryTemplate;
        this.responseErrorHandler = builder.responseErrorHandler;
        this.restClientBuilder = builder.restClientBuilder;
        this.webClientBuilder = builder.webClientBuilder;
        this.observationRegistry = builder.observationRegistry;
        this.toolExecutionEligibilityPredicate = builder.toolExecutionEligibilityPredicate;
        this.observationConvention = builder.observationConvention;

        // 配置参数
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
        this.model = builder.model;
        this.temperature = builder.temperature;
        this.defaultOptions = builder.defaultOptions;

        // 创建DeepSeek API客户端
        if (this.deepSeekApi == null) {
            this.deepSeekApi = createDeepSeekApi();
        }

        // 创建DeepSeek聊天模型
        if (this.deepSeekChatModel == null) {
            this.deepSeekChatModel = createDeepSeekChatModel();
        }
    }

    /**
     * 获取DeepSeek聊天模型实例
     * 
     * @return DeepSeek聊天模型
     */
    public DeepSeekChatModel getDeepSeekChatModel() {
        return deepSeekChatModel;
    }

    /**
     * 获取DeepSeek API客户端实例
     * 
     * @return DeepSeek API客户端
     */
    public DeepSeekApi getDeepSeekApi() {
        return deepSeekApi;
    }

    /**
     * 创建DeepSeek API客户端
     * 
     * @return DeepSeek API客户端
     */
    protected DeepSeekApi createDeepSeekApi() {
        return DeepSeekApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .restClientBuilder(restClientBuilder)
                .responseErrorHandler(responseErrorHandler)
                .build();
    }

    /**
     * 创建DeepSeek聊天模型
     * 
     * @return DeepSeek聊天模型
     */
    protected DeepSeekChatModel createDeepSeekChatModel() {
        // 创建聊天选项
        DeepSeekChatOptions options = defaultOptions != null ? defaultOptions : 
                DeepSeekChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build();
        
        // 创建聊天模型
        DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(options)
                .toolCallingManager(toolCallingManager)
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
     * DeepSeekChatConfiguration的Builder类
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
        protected DeepSeekChatOptions defaultOptions;

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

        public Builder defaultOptions(DeepSeekChatOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        /**
         * 构建DeepSeekChatConfiguration实例
         *
         * @return 新的DeepSeekChatConfiguration实例
         * @throws IllegalStateException 如果配置无效
         */
        public DeepSeekChatConfiguration build() {
            validate();
            return new DeepSeekChatConfiguration(this);
        }

        /**
         * 验证Builder配置是否有效
         */
        protected void validate() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("API密钥不能为空");
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
            
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                this.baseUrl = "https://api.deepseek.com";
            }
            
            if (model == null || model.trim().isEmpty()) {
                this.model = "deepseek-chat";
            }
        }
    }
}