package com.yonchain.ai.idm.entity;


import lombok.Data;

import java.util.Date;

/**
 * 租户账户关联实体类
 * </p>
 * 表示租户与账户之间的多对多关联关系
 * 包含角色、邀请信息等关联属性
 * </p
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class TenantAccountEntity {

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
     * 是否为当前租户
     */
    private boolean current;

}
