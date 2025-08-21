package com.yonchain.ai.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 模型实例配置实体
 * 存储租户级别的模型个性化配置和默认参数
 */
@Data
public class ModelInstanceConfig {
    
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 模型代码（对应静态配置中的code）
     */
    private String modelCode;
    
    /**
     * 提供商代码
     */
    private String providerCode;
    
    /**
     * 模型配置（JSON格式）
     * 存储租户对模型的个性化配置，如参数范围限制等
     */
    private String modelConfig;
    
    /**
     * 默认参数（JSON格式）
     * 存储租户设置的模型默认参数，如默认temperature值
     */
    private String defaultParams;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
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
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 更新者
     */
    private String updatedBy;
}