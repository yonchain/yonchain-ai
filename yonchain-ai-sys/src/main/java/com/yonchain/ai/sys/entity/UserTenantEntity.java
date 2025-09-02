package com.yonchain.ai.sys.entity;

import lombok.Data;

import java.util.Date;

/**
 * 租户账户关联实体类
 * <p>
 * 映射数据库表 sys_user_tenant
 * </p>
 * 
 * @author Cgy
 * @since 2024-01-20
 */
@Data
public class UserTenantEntity {

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
