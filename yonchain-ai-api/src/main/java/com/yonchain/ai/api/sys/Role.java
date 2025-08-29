package com.yonchain.ai.api.sys;

import java.time.LocalDateTime;

/**
 * 角色接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface Role {

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    String getId();

    /**
     * 设置角色ID
     *
     * @param id 角色ID
     */
    void setId(String id);

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    String getTenantId();

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    void setTenantId(String tenantId);


    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    String getName();

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    void setName(String name);

    /**
     * 获取角色描述
     *
     * @return 角色描述
     */
    String getDescription();

    /**
     * 设置角色描述
     *
     * @param description 角色描述
     */
    void setDescription(String description);

    /**
     * 获取角色代码
     *
     * @return 角色代码
     */
    String getCode();

    /**
     * 设置角色代码
     *
     * @param code 角色代码
     */
    void setCode(String code);

    /**
     * 获取角色状态
     *
     * @return 角色状态
     */
    String getStatus();

    /**
     * 设置角色状态
     *
     * @param status 角色状态
     */
    void setStatus(String status);

    /**
     * 获取创建者ID
     *
     * @return 创建者ID
     */
    String getCreatedBy();

    /**
     * 设置创建者ID
     *
     * @param createdBy 创建者ID
     */
    void setCreatedBy(String createdBy);

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    LocalDateTime getCreatedAt();

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    void setCreatedAt(LocalDateTime createdAt);

    /**
     * 获取更新者ID
     *
     * @return 更新者ID
     */
    String getUpdatedBy();

    /**
     * 设置更新者ID
     *
     * @param updatedBy 更新者ID
     */
    void setUpdatedBy(String updatedBy);

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    LocalDateTime getUpdatedAt();

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    void setUpdatedAt(LocalDateTime updatedAt);

    /**
     * 获取角色类别(0-系统角色，1-业务角色)
     *
     * @return 角色类别(0 - 系统角色 ， 1 - 业务角色)
     */
    String getCategory();

    /**
     * 设置角色类别(0-系统角色，1-业务角色)
     *
     * @param category 角色类别(0-系统角色，1-业务角色)
     */
    void setCategory(String category);

}
