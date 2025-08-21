package com.yonchain.ai.console.model.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 文本嵌入请求
 */
@Data
public class EmbeddingRequest {

    /**
     * 需要嵌入的文本
     */
    private String text;

    /**
     * 需要嵌入的文本列表
     */
    private List<String> texts;

    /**
     * 其他参数
     */
    private Map<String, Object> options;
}