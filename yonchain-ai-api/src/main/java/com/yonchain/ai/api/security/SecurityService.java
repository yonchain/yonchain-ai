package com.yonchain.ai.api.security;

import java.util.Map;

/**
 * 安全服务接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface SecurityService {

    /**
     * 密码加密
     *
     * @param rawPassword 原始密码
     * @param salt        盐值，用于防止彩虹表攻击。为空时，系统自动生成盐值
     * @return 加密密码
     */
    String encodePassword(CharSequence rawPassword, String salt);

    /**
     * 密码匹配
     *
     * @param rawPassword     原始密码
     * @param salt            盐值，用于防止彩虹表攻击。为空时，系统自动生成盐值
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    boolean matchesPassword(CharSequence rawPassword, String salt, String encodedPassword);

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID，如果令牌无效则返回空
     */
    String getUserIdFromToken(String token);

    /**
     * 从令牌中获取声明
     *
     * @param token 令牌
     * @return 声明映射，如果令牌无效则返回空
     */
    Map<String, Object> getClaimsFromToken(String token);

    /**
     * 生成密码
     *
     * @param rawPassword 原始密码
     * @return 密码
     */
    Password encodePassword(String rawPassword);
}
