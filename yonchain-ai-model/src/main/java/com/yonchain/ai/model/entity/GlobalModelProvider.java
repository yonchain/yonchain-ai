package com.yonchain.ai.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 全局模型提供商实体类（不分租户）
 * 存储静态的模型提供商配置信息
 */
@Data
@Accessors(chain = true)
public class GlobalModelProvider {

    /**
     * 主键ID
     */
    private String id;

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
     * 排序权重，数值越小排序越靠前
     */
    private Integer sortOrder;

    /**
     * 支持的模型类型列表
     * 数据库中存储为JSON字符串数组
     */
    private String supportedModelTypes;

    /**
     * 提供商配置参数Schema，JSON Schema格式
     * 数据库中存储为JSON字符串
     */
    private String configSchema;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}