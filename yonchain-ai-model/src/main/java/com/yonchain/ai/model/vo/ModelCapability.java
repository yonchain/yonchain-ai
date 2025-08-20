package com.yonchain.ai.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 模型能力值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ModelCapability {

    /**
     * 能力代码
     */
    private String code;

    /**
     * 能力名称
     */
    private String name;

    /**
     * 能力描述
     */
    private String description;

    /**
     * 能力类型
     */
    private String type;

    /**
     * 能力图标
     */
    private String icon;

    /**
     * 是否支持流式输出
     */
    private Boolean supportsStreaming;

    /**
     * 能力参数Schema，JSON Schema格式
     */
    private Object paramSchema;
}