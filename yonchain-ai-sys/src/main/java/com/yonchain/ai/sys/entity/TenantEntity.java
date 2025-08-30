package com.yonchain.ai.sys.entity;

import com.yonchain.ai.api.sys.Tenant;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户实体类
 */
@Data
public class TenantEntity implements Tenant {

    /**
     * 租户ID
     */
    //@TableId
    private String id;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 加密公钥
     */
    private String encryptPublicKey;

    /**
     * 计划类型
     */
    private String plan;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否当前租户
     */
    private Boolean current;

}
