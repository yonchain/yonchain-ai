package com.yonchain.ai.api.tag;

import java.time.LocalDateTime;

/**
 * 标签接口
 *
 * @author Cgy
 */
public interface Tag {

    /**
     * 获取标签ID
     *
     * @return 标签ID
     */
    String getId();

    /**
     * 设置标签ID
     *
     * @param id 标签ID
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
     * 获取标签类型
     *
     * @return 标签类型
     */
    String getType();

    /**
     * 设置标签类型
     *
     * @param type 标签类型
     */
    void setType(String type);

    /**
     * 获取标签名称
     *
     * @return 标签名称
     */
    String getName();

    /**
     * 设置标签名称
     *
     * @param name 标签名称
     */
    void setName(String name);

    /**
     * 获取创建人ID
     *
     * @return 创建人ID
     */
    String getCreatedBy();

    /**
     * 设置创建人ID
     *
     * @param createdBy 创建人ID
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
}
