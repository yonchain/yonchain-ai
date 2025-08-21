package com.yonchain.ai.api.model;


import java.util.List;
import java.util.Map;

/**
 * 聊天完成请求
 */
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
    public static class ChatMessage {
        /**
         * 消息角色（system, user, assistant）
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;


        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
}