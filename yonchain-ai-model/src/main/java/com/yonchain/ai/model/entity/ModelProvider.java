package com.yonchain.ai.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模型提供商实体类
 */
@Data
@Accessors(chain = true)
public class ModelProvider {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 提供商代码，唯一标识
     */
    private String code;

    /**
     * 提供商名称
     */
    private String name;

    /**
     * 提供商描述
     */
    private String description;

    /**
     * 提供商图标URL
     */
    private String icon;

    /**
     * 提供商官网URL
     */
    private String websiteUrl;

    /**
     * 提供商类型（SYSTEM: 系统内置, CUSTOM: 自定义）
     */
    private String type;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * API代理URL
     */
    private String proxyUrl;

    /**
     * 提供商配置参数，JSON格式
     * 数据库中存储为JSON字符串
     */
    private String config;

    /**
     * 提供商配置参数Schema，JSON Schema格式
     * 数据库中存储为JSON字符串
     */
    private String configSchema;

    /**
     * 支持的模型类型列表
     * 数据库中存储为JSON字符串数组
     */
    private String modelType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 提供商支持的模型列表
     * 数据库中存储为JSON字符串
     */
    private List<AiModel> models;

    /**
     * 提供商支持的能力映射，key为能力代码，value为能力对象
     * 数据库中存储为JSON字符串
     */
    private String capabilities;
}