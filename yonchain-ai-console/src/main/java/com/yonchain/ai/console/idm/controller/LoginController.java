package com.yonchain.ai.console.idm.controller;

import com.yonchain.ai.web.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 *
 * @author Cgy
 */
@RestController
@RequestMapping
public class LoginController {


    /**
     * 退出登录
     *
     * @return ApiResponse<Void>
     */
    @DeleteMapping("/auth/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}

