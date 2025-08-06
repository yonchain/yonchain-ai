package com.yonchain.ai.api.idm;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 默认用户实现类
 *
 * @author Cgy
 * @since 1.0.0
 */
public class DefaultUser implements User {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 账户名称
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码盐值
     */
    private String passwordSalt;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 界面语言
     */
    private String interfaceLanguage;

    /**
     * 界面主题
     */
    private String interfaceTheme;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 状态
     */
    private String status;

    /**
     * 初始化时间
     */
    private LocalDateTime initializedAt;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 最后活跃时间
     */
    private Date lastActiveAt;

    /**
     * 获取主键ID
     *
     * @return 主键ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取账户名称
     *
     * @return 账户名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置账户名称
     *
     * @param name 账户名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取密码盐值
     *
     * @return 密码盐值
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * 设置密码盐值
     *
     * @param passwordSalt 密码盐值
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * 获取头像
     *
     * @return 头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像
     *
     * @param avatar 头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取界面语言
     *
     * @return 界面语言
     */
    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    /**
     * 设置界面语言
     *
     * @param interfaceLanguage 界面语言
     */
    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }

    /**
     * 获取界面主题
     *
     * @return 界面主题
     */
    public String getInterfaceTheme() {
        return interfaceTheme;
    }

    /**
     * 设置界面主题
     *
     * @param interfaceTheme 界面主题
     */
    public void setInterfaceTheme(String interfaceTheme) {
        this.interfaceTheme = interfaceTheme;
    }

    /**
     * 获取时区
     *
     * @return 时区
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * 设置时区
     *
     * @param timezone 时区
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * 获取最后登录时间
     *
     * @return 最后登录时间
     */
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    /**
     * 设置最后登录时间
     *
     * @param lastLoginAt 最后登录时间
     */
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * 获取最后登录IP
     *
     * @return 最后登录IP
     */
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    /**
     * 设置最后登录IP
     *
     * @param lastLoginIp 最后登录IP
     */
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
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
     * 获取初始化时间
     *
     * @return 初始化时间
     */
    public LocalDateTime getInitializedAt() {
        return initializedAt;
    }

    /**
     * 设置初始化时间
     *
     * @param initializedAt 初始化时间
     */
    public void setInitializedAt(LocalDateTime initializedAt) {
        this.initializedAt = initializedAt;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取最后活跃时间
     *
     * @return 最后活跃时间
     */
    public Date getLastActiveAt() {
        return lastActiveAt;
    }

    /**
     * 设置最后活跃时间
     *
     * @param lastActiveAt 最后活跃时间
     */
    public void setLastActiveAt(Date lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

}
