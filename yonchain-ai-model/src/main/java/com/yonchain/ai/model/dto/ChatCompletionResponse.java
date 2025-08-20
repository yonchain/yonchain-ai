package com.yonchain.ai.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 聊天完成响应
 */
@Data
public class ChatCompletionResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 响应选项列表
     */
    private List<Choice> choices;

    /**
     * 使用情况统计
     */
    private Usage usage;

    /**
     * 响应选项
     */
    @Data
    public static class Choice {
        /**
         * 选项索引
         */
        private Integer index;

        /**
         * 消息
         */
        private ChatCompletionRequest.ChatMessage message;

        /**
         * 结束原因
         */
        private String finishReason;
    }

    /**
     * 使用情况统计
     */
    @Data
    public static class Usage {
        /**
         * 提示令牌数
         */
        private Integer promptTokens;

        /**
         * 完成令牌数
         */
        private Integer completionTokens;

        /**
         * 总令牌数
         */
        private Integer totalTokens;
    }
}