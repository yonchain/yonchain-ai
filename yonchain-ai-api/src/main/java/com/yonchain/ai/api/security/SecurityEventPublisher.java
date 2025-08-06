package com.yonchain.ai.api.security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 事件发布器
 *
 * @author Cgy
 */
public class SecurityEventPublisher {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static final List<UserLoginListener> listeners = new ArrayList<>();

    /**
     * 添加事件监听器
     *
     * @param listener 监听器实例
     */
    public static void addListener(UserLoginListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * 移除事件监听器
     *
     * @param listener 监听器实例
     */
    public static void removeListener(UserLoginListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * 发布用户登录事件
     *
     * @param userId 用户ID
     */
    public static void publishUserLoginEvent(String userId, String ipAddress) {
        UserLoginEvent event = new UserLoginEvent(userId, ipAddress);

        // 异步通知所有监听器
        executor.submit(() -> {
            System.out.println("用户登录事件发布成功，用户ID：" + userId);
            synchronized (listeners) {
                for (UserLoginListener listener : listeners) {
                    try {
                        listener.onUserLogin(event);
                    } catch (Exception e) {
                        // 防止一个监听器异常影响其他监听器
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
