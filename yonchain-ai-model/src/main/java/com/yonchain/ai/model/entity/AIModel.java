package com.yonchain.ai.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI模型实体类
 */
@Data
@Accessors(chain = true)
public class AIModel {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 模型代码，唯一标识
     */
    private String code;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 模型图标URL
     */
    private String iconUrl;

    /**
     * 模型类型（TEXT: 文本, IMAGE: 图像, AUDIO: 音频, VIDEO: 视频, MULTIMODAL: 多模态）
     */
    private String modelType;

    /**
     * 模型提供商ID
     */
    private Long providerId;

    /**
     * 模型提供商代码
     */
    private String providerCode;

    /**
     * 模型版本
     */
    private String version;

    /**
     * 是否是系统默认模型
     */
    private Boolean isSystem;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 模型配置参数，JSON格式
     */
    //@TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> config;

    /**
     * 模型配置参数Schema，JSON Schema格式
     */
   // @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> configSchema;

    /**
     * 模型能力标签，如文本生成、图像生成等
     */
  //  @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] capabilities;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}