package com.yonchain.ai.api.tag;

import java.time.LocalDateTime;

/**
 * 默认标签
 *
 * @author Cgy
 */
public class DefaultTag implements Tag {

    /**
     * 标签ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 标签类型
     */
    private String type;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


    /**
     * 设置标签ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取标签ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获取租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置标签类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取标签类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置标签名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取标签名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置创建人ID
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取创建人ID
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


}
