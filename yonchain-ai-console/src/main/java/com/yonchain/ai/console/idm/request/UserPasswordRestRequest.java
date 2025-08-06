package com.yonchain.ai.console.idm.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户密码重置请求
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class UserPasswordRestRequest {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String password;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 255, message = "新密码长度必须在6-255个字符之间")
    private String newPassword;

    /**
     * 重复新密码
     */
    @NotBlank(message = "重复新密码不能为空")
    @Size(min = 6, max = 255, message = "重复新密码长度必须在6-255个字符之间")
    private String repeatNewPassword;
}
