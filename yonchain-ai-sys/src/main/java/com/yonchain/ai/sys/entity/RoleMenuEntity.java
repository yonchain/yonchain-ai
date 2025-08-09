package com.yonchain.ai.sys.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色与菜单关联实体
 */
@Data
public class RoleMenuEntity {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}
