package com.yonchain.ai.console.model.response;

import lombok.Data;

import java.util.List;

/**
 * 图像生成响应
 */
@Data
public class ImageGenerationResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 生成的图像列表
     */
    private List<Image> data;

    /**
     * 图像信息
     */
    @Data
    public static class Image {
        /**
         * 图像URL
         */
        private String url;

        /**
         * 图像Base64数据
         */
        private String b64Json;

        /**
         * 修订版本
         */
        private String revisedPrompt;
    }
}