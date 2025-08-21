package com.yonchain.ai.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 模型提供商配置实体
 * 存储租户级别的提供商动态配置（API密钥、代理设置等）
 */
@Data
public class ModelProviderConfig {
    
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 提供商代码（对应静态配置中的code）
     */
    private String providerCode;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * API基础URL（可覆盖默认值）
     */
    private String baseUrl;
    
    /**
     * API代理URL
     */
    private String proxyUrl;
    
    /**
     * 自定义配置（JSON格式）
     * 存储提供商特定的配置参数
     */
    private String customConfig;
    
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