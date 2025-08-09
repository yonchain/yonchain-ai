package com.yonchain.ai.sys.entity;

import com.yonchain.ai.api.sys.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户实体类
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class UserEntity implements User {

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

}
