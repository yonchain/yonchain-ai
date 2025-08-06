package com.yonchain.ai.security.user;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 鉴权用户详情接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface Dify4jUserDetails extends UserDetails {

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    String getUserId();

    /**
     * 获取用户邮箱
     *
     * @return 用户邮箱地址
     */
    String getEmail();

}
