package com.yonchain.ai.api.model;

import java.util.List;

/**
 * 聊天完成响应
 */
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

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public ChatCompletionRequest.ChatMessage getMessage() {
            return message;
        }

        public void setMessage(ChatCompletionRequest.ChatMessage message) {
            this.message = message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
    }

    /**
     * 使用情况统计
     */
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

        public Integer getPromptTokens() {
            return promptTokens;
        }

        public void setPromptTokens(Integer promptTokens) {
            this.promptTokens = promptTokens;
        }

        public Integer getCompletionTokens() {
            return completionTokens;
        }

        public void setCompletionTokens(Integer completionTokens) {
            this.completionTokens = completionTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}