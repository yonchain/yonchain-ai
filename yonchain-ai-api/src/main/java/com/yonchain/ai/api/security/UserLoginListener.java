package com.yonchain.ai.api.security;

/**
 * 用户登录事件监听器接口
 */
public interface UserLoginListener {

    /**
     * 处理用户登录事件
     * @param event 登录事件对象
     */
    void onUserLogin(UserLoginEvent event);
}
