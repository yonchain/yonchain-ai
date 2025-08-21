package com.yonchain.ai.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 租户模型配置实体类
 * 存储租户级别的模型个性化配置
 */
@Data
@Accessors(chain = true)
public class TenantModelConfig {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 模型代码，关联静态配置文件中的model.code
     */
    private String modelCode;

    /**
     * 提供商代码
     */
    private String providerCode;

    /**
     * 租户的模型个性化配置参数，JSON格式
     * 例如：自定义的temperature、max_tokens等参数范围
     */
    private String modelConfig;

    /**
     * 租户设置的模型默认参数，JSON格式
     * 例如：{"temperature": 0.7, "max_tokens": 2048}
     * 这些参数会作为该租户调用该模型时的默认值
     */
    private String defaultParams;

    /**
     * 租户是否启用该模型
     */
    private Boolean enabled;

    /**
     * 租户自定义的排序权重
     * 可以覆盖静态配置中的sortOrder
     */
    private Integer sortOrder;

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