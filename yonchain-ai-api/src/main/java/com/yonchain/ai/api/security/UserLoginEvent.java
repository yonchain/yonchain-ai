package com.yonchain.ai.api.security;

import java.util.EventObject;

/**
 * 用户登录事件
 */
public class UserLoginEvent extends EventObject {

    private final String ipAddress;

    private final long loginTime;

    public UserLoginEvent(String userId,String ipAddress) {
        super(userId);
        this.loginTime = System.currentTimeMillis();
        this.ipAddress = ipAddress;
    }

    public String getUserId() {
        return source.toString();
    }

    public long getLoginTime() {
        return loginTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
