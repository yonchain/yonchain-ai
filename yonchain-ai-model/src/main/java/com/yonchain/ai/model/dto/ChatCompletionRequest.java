package com.yonchain.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 聊天完成请求
 */
@Data
public class ChatCompletionRequest {

    /**
     * 聊天消息列表
     */
    private List<ChatMessage> messages;

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

    /**
     * 聊天消息
     */
    @Data
    public static class ChatMessage {
        /**
         * 消息角色（system, user, assistant）
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;
    }
}