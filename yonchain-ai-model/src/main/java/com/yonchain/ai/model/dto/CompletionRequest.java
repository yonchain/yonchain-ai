package com.yonchain.ai.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 文本完成请求
 */
@Data
public class CompletionRequest {

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 温度参数，控制输出的随机性
     */
    private Double temperature;

    /**
     * 最大令牌数
     */
    private Integer maxTokens;

    /**
     * 是否流式输出
     */
    private Boolean stream;

    /**
     * 其他参数
     */
    private Map<String, Object> options;
}