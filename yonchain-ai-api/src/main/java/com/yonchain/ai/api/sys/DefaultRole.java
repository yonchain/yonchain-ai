package com.yonchain.ai.api.sys;

import java.time.LocalDateTime;

/**
 * 默认角色实现类
 *
 * @author Cgy
 * @since 1.0.0
 */
public class DefaultRole implements Role {

    /**
     * 角色ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 描述
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
     * 角色类别(0-系统角色，1-业务角色)
     */
    private String category;


    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置角色ID
     *
     * @param id 角色ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取角色编码
     *
     * @return 角色编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置角色编码
     *
     * @param code 角色编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取描述信息
     *
     * @return 描述信息
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述信息
     *
     * @param description 描述信息
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取角色状态
     *
     * @return 角色状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置角色状态
     *
     * @param status 角色状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取创建者ID
     *
     * @return 创建者ID
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置创建者ID
     *
     * @param createdBy 创建者ID
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新者ID
     *
     * @return 更新者ID
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 设置更新者ID
     *
     * @param updatedBy 更新者ID
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }


    /**
     * 获取角色类别(0-系统角色，1-业务角色)
     *
     * @return 角色类别(0-系统角色，1-业务角色)
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置角色类别(0-系统角色，1-业务角色)
     *
     * @param category 角色类别(0-系统角色，1-业务角色)
     */
    public void setCategory(String category) {
        this.category = category;
    }

}
