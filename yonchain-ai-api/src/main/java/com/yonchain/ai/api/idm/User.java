package com.yonchain.ai.api.idm;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface User {

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    String getId();

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    void setId(String id);

    /**
     * 获取账户名称
     *
     * @return 账户名称
     */
    String getName();

    /**
     * 设置账户名称
     *
     * @param name 账户名称
     */
    void setName(String name);

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    String getEmail();

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    void setEmail(String email);

    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 设置密码
     *
     * @param password 密码
     */
    void setPassword(String password);

    /**
     * 获取密码盐值
     *
     * @return 密码盐值
     */
    String getPasswordSalt();

    /**
     * 设置密码盐值
     *
     * @param passwordSalt 密码盐值
     */
    void setPasswordSalt(String passwordSalt);

    /**
     * 获取头像
     *
     * @return 头像
     */
    String getAvatar();

    /**
     * 设置头像
     *
     * @param avatar 头像
     */
    void setAvatar(String avatar);

    /**
     * 获取界面语言
     *
     * @return 界面语言
     */
    String getInterfaceLanguage();

    /**
     * 设置界面语言
     *
     * @param interfaceLanguage 界面语言
     */
    void setInterfaceLanguage(String interfaceLanguage);

    /**
     * 获取界面主题
     *
     * @return 界面主题
     */
    String getInterfaceTheme();

    /**
     * 设置界面主题
     *
     * @param interfaceTheme 界面主题
     */
    void setInterfaceTheme(String interfaceTheme);

    /**
     * 获取时区
     *
     * @return 时区
     */
    String getTimezone();

    /**
     * 设置时区
     *
     * @param timezone 时区
     */
    void setTimezone(String timezone);

    /**
     * 获取最后登录时间
     *
     * @return 最后登录时间
     */
    LocalDateTime getLastLoginAt();

    /**
     * 设置最后登录时间
     *
     * @param lastLoginAt 最后登录时间
     */
    void setLastLoginAt(LocalDateTime lastLoginAt);

    /**
     * 获取最后登录IP
     *
     * @return 最后登录IP
     */
    String getLastLoginIp();

    /**
     * 设置最后登录IP
     *
     * @param lastLoginIp 最后登录IP
     */
    void setLastLoginIp(String lastLoginIp);

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
     * 获取初始化时间
     *
     * @return 初始化时间
     */
    LocalDateTime getInitializedAt();

    /**
     * 设置初始化时间
     *
     * @param initializedAt 初始化时间
     */
    void setInitializedAt(LocalDateTime initializedAt);

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    Date getCreatedAt();

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    void setCreatedAt(Date createdAt);

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    Date getUpdatedAt();

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    void setUpdatedAt(Date updatedAt);

    /**
     * 获取最后活跃时间
     *
     * @return 最后活跃时间
     */
    Date getLastActiveAt();

    /**
     * 设置最后活跃时间
     *
     * @param lastActiveAt 最后活跃时间
     */
    void setLastActiveAt(Date lastActiveAt);

}
