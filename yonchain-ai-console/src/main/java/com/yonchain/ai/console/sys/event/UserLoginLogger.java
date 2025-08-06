package com.yonchain.ai.console.sys.event;

import com.yonchain.ai.api.security.UserLoginEvent;
import com.yonchain.ai.api.security.UserLoginListener;
import com.yonchain.ai.console.sys.service.LoginLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户登录日志记录器
 */
public class UserLoginLogger implements UserLoginListener {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginLogger.class);

    @Autowired
    private LoginLogService loginLogService;

    @Override
    public void onUserLogin(UserLoginEvent event) {
        try {
            loginLogService.saveLoginLog(event.getUserId(), event.getIpAddress());
        }catch (Exception e) {
            logger.error("保存用户登录日志失败", e);
            e.printStackTrace();
        }
    }
}
