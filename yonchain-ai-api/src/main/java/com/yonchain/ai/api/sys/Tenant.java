package com.yonchain.ai.api.sys;

import java.time.LocalDateTime;

/**
 * 租户接口
 */
public interface Tenant {

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    String getId();

    /**
     * 设置租户ID
     *
     * @param id 租户ID
     */
    void setId(String id);

    /**
     * 获取租户名称
     *
     * @return 租户名称
     */
    String getName();

    /**
     * 设置租户名称
     *
     * @param name 租户名称
     */
    void setName(String name);

    /**
     * 获取加密公钥
     *
     * @return 加密公钥
     */
    String getEncryptPublicKey();

    /**
     * 设置加密公钥
     *
     * @param encryptPublicKey 加密公钥
     */
    void setEncryptPublicKey(String encryptPublicKey);

    /**
     * 获取计划类型
     *
     * @return 计划类型
     */
    String getPlan();

    /**
     * 设置计划类型
     *
     * @param plan 计划类型
     */
    void setPlan(String plan);

    /**
     * 获取状态
     *
     * @return 状态
     */
    String getStatus();

    /**
     * 设置状态
     *
     * @param status 状态
     */
    void setStatus(String status);

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
     * 获取自定义配置
     *
     * @return 自定义配置
     */
    String getCustomConfig();

    /**
     * 设置自定义配置
     *
     * @param customConfig 自定义配置
     */
    void setCustomConfig(String customConfig);

    /**
     * 获取角色
     *
     * @return 角色
     */
    String getRole();

    /**
     * 获取角色
     *
     * @param role 角色
     */
    void setRole(String role);

}
