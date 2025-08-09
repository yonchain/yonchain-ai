package com.yonchain.ai.console.sys.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 更新用户请求
 *
 * @author Cgy
 * @since 1.0.0
 */
public class UserUpdateRequest {

    /**
     * 账户名称
     */
    @Size(max = 255, message = "账户名称长度不能超过255个字符")
    private String name;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;

    /**
     * 角色
     */
    private List<String> roleIds;

    /**
     * 头像
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;

    /**
     * 界面语言
     */
    @Size(max = 255, message = "界面语言长度不能超过255个字符")
    private String interfaceLanguage;

    /**
     * 界面主题
     */
    @Size(max = 255, message = "界面主题长度不能超过255个字符")
    private String interfaceTheme;

    /**
     * 时区
     */
    @Size(max = 255, message = "时区长度不能超过255个字符")
    private String timezone;

    /**
     * 是否设置角色
     */
    @JsonIgnore
    private boolean roleSet = false;

    /**
     * 是否设置头像
     */
    @JsonIgnore
    private boolean avatarSet = false;

    /**
     * 是否设置界面语言
     */
    @JsonIgnore
    private boolean interfaceLanguageSet = false;

    /**
     * 是否设置界面主题
     */
    @JsonIgnore
    private boolean interfaceThemeSet = false;

    /**
     * 是否设置时区
     */
    @JsonIgnore
    private boolean timezoneSet = false;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
        this.roleSet = true;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        this.avatarSet = true;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
        this.interfaceLanguageSet = true;
    }

    public String getInterfaceTheme() {
        return interfaceTheme;
    }

    public void setInterfaceTheme(String interfaceTheme) {
        this.interfaceTheme = interfaceTheme;
        this.interfaceThemeSet = true;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        this.timezoneSet = true;
    }

    public boolean isRoleSet() {
        return roleSet;
    }

    public boolean isAvatarSet() {
        return avatarSet;
    }

    public boolean isInterfaceLanguageSet() {
        return interfaceLanguageSet;
    }

    public boolean isInterfaceThemeSet() {
        return interfaceThemeSet;
    }

    public boolean isTimezoneSet() {
        return timezoneSet;
    }

}
