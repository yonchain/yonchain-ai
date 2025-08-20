package com.yonchain.ai.model.config;

import lombok.Data;

/**
 * OpenAI配置属性
 */
@Data
public class OpenAIProperties {

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API基础URL
     */
    private String baseUrl = "https://api.openai.com";

    /**
     * 代理URL
     */
    private String proxyUrl;
}