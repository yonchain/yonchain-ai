package com.yonchain.ai.tmpl.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 模型提供商实体类
 * 对应数据库表：model_provider
 */
@Data
@Accessors(chain = true)
public class ModelProviderEntity {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 提供商代码，关联静态配置文件中的provider.code
     */
    private String providerCode;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API基础URL，可覆盖静态配置
     */
    private String baseUrl;

    /**
     * API代理URL
     */
    private String proxyUrl;

    /**
     * 自定义配置参数，JSON格式
     */
    private String customConfig;

    /**
     * 租户是否启用该提供商
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

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 更新者
     */
    private String updatedBy;
}
