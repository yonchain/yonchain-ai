package com.yonchain.ai.console.context;

public class ConsoleContextHolder {

    private static final ThreadLocal<ConsoleContext> contextHolder = new ThreadLocal<>();

    // 设置当前用户上下文
    public static void setContext(ConsoleContext userContext) {
        contextHolder.set(userContext);
    }

    // 获取当前用户上下文
    public static ConsoleContext getContext() {
        return contextHolder.get();
    }

    // 清除当前用户上下文（防止内存泄漏）
    public static void clearContext() {
        contextHolder.remove();
    }
}
