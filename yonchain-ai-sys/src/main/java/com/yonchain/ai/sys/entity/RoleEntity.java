package com.yonchain.ai.sys.entity;

import com.yonchain.ai.api.sys.Role;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class RoleEntity implements Role {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色代码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色状态，默认为normal
     */
    private String status;

    /**
     * 创建者ID
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新者ID
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 角色类别(0-系统角色，1-业务角色)
     */
    private String category;
}
