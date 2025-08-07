package com.yonchain.ai.idm.entity;

import lombok.Data;

import java.util.Date;

/**
 * 租户账户关联实体类
 * <p>
 * 映射数据库表 tenant_account_joins
 * </p>
 * 
 * @author Cgy
 * @since 2024-01-20
 */
@Data
public class TenantAccountJoinEntity {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 账户ID
     */
    private String accountId;

    /**
     * 角色
     */
    private String role;

    /**
     * 邀请人ID
     */
    private String invitedBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 是否当前
     */
    private Boolean current;
}
