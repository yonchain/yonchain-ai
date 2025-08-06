package com.yonchain.ai.console.idm.request;

import com.yonchain.ai.api.idm.DefaultUser;
import com.yonchain.ai.api.idm.User;
import com.yonchain.ai.constants.UserStatusConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建用户请求
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class UserCreateRequest {

    /**
     * 账户名称
     */
    @NotBlank(message = "账户名称不能为空")
    @Size(max = 255, message = "账户名称长度不能超过255个字符")
    private String name;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 255, message = "密码长度必须在6-255个字符之间")
    private String password;

    /**
     * 角色
     */
    @NotEmpty(message = "角色不能为空")
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
     * 将当前请求对象转换为用户实体对象
     *
     * @return 转换后的用户实体对象，包含所有请求字段值
     */
    public User getUser() {
        User user = new DefaultUser();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setAvatar(this.avatar);
        user.setInterfaceLanguage(this.interfaceLanguage);
        user.setInterfaceTheme(this.interfaceTheme);
        user.setTimezone(this.timezone);
        user.setStatus(UserStatusConstant.ACTIVE);
        return user;
    }
}
