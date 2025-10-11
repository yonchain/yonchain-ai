package com.yonchain.ai.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 租户模型配置实体类
 * 对应数据库表：model
 * 存储租户对模型的个性化配置，静态配置由YAML文件管理
 */
@Data
@Accessors(chain = true)
public class ModelEntity {

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
     * 租户的模型个性化配置，JSON格式
     */
    private String modelConfig;

    /**
     * 租户设置的模型默认参数，JSON格式
     */
    private String defaultParams;

    /**
     * 租户是否启用该模型
     */
    private Boolean enabled;

    /**
     * 租户自定义的排序权重
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
