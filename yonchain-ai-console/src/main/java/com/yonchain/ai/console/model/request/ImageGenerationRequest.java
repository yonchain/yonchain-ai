package com.yonchain.ai.console.model.request;

import lombok.Data;

import java.util.Map;

/**
 * 图像生成请求
 */
@Data
public class ImageGenerationRequest {

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 图像尺寸
     */
    private String size;

    /**
     * 图像质量
     */
    private String quality;

    /**
     * 生成图像数量
     */
    private Integer n;

    /**
     * 其他参数
     */
    private Map<String, Object> options;
}