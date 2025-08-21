package com.yonchain.ai.console.model.response;

import com.yonchain.ai.api.model.ChatCompletionResponse;
import lombok.Data;

import java.util.List;

/**
 * 文本嵌入响应
 */
@Data
public class EmbeddingResponse {

    /**
     * 对象类型
     */
    private String object;

    /**
     * 嵌入数据列表
     */
    private List<EmbeddingData> data;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 使用情况统计
     */
    private ChatCompletionResponse.Usage usage;

    /**
     * 嵌入数据
     */
    @Data
    public static class EmbeddingData {
        /**
         * 对象类型
         */
        private String object;

        /**
         * 嵌入向量
         */
        private List<Float> embedding;

        /**
         * 索引
         */
        private Integer index;
    }
}