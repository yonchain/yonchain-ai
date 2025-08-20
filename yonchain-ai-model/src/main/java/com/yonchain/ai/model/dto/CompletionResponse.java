package com.yonchain.ai.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 文本完成响应
 */
@Data
public class CompletionResponse {

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
    private ChatCompletionResponse.Usage usage;

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
         * 文本
         */
        private String text;

        /**
         * 结束原因
         */
        private String finishReason;
    }
}