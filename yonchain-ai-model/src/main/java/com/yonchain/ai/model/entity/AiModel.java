package com.yonchain.ai.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * AI模型实体类
 */
@Data
@Accessors(chain = true)
public class AiModel {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

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
    private String icon;

    /**
     * 模型类型（TEXT: 文本, IMAGE: 图像, AUDIO: 音频, VIDEO: 视频, MULTIMODAL: 多模态）
     */
    private String modelType;

    /**
     * 模型提供商ID
     */
    private String providerId;

    /**
     * 模型提供商代码
     */
    private String providerCode;

    /**
     * 模型版本
     */
    private String version;

    /**
     * 数据来源（seed: 启动种子加载；manual: 控制台/接口人工新增；remote: 远程同步等）
     */
    private String source;

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
     * 数据库中存储为JSON字符串
     */
    private String config;

    /**
     * 模型配置参数Schema，JSON Schema格式
     * 数据库中存储为JSON字符串
     */
    private String configSchema;

    /**
     * 模型能力标签，如文本生成、图像生成等
     * 数据库中存储为JSON字符串数组
     */
    private String capabilities;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}