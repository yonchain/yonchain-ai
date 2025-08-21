package com.yonchain.ai.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 租户模型提供商配置实体类
 * 存储租户级别的提供商动态配置（API密钥等）
 */
@Data
@Accessors(chain = true)
public class TenantModelProvider {

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
     * 存储租户特定的配置参数
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