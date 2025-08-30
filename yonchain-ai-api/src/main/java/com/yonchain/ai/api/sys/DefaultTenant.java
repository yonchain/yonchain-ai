package com.yonchain.ai.api.sys;


import java.time.LocalDateTime;

/**
 * 默认租户
 */
public class DefaultTenant implements Tenant {

    /**
     * 租户ID
     */
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
     * 角色
     */
    private Boolean current;

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置租户ID
     *
     * @param id 租户ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取租户名称
     *
     * @return 租户名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置租户名称
     *
     * @param name 租户名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取加密公钥
     *
     * @return 加密公钥
     */
    public String getEncryptPublicKey() {
        return encryptPublicKey;
    }

    /**
     * 设置加密公钥
     *
     * @param encryptPublicKey 加密公钥
     */
    public void setEncryptPublicKey(String encryptPublicKey) {
        this.encryptPublicKey = encryptPublicKey;
    }

    /**
     * 获取计划类型
     *
     * @return 计划类型
     */
    public String getPlan() {
        return plan;
    }

    /**
     * 设置计划类型
     *
     * @param plan 计划类型
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
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
     * 获取当前租户
     *
     * @return 当前租户
     */
    @Override
    public Boolean getCurrent() {
        return current;
    }

    /**
     * 设置当前租户
     *
     * @param current 当前租户
     */
    @Override
    public void setCurrent(Boolean current) {
        this.current = current;
    }
}
