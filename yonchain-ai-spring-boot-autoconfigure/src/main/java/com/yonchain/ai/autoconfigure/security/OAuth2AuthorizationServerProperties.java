package com.yonchain.ai.autoconfigure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2 授权服务器配置属性类
 *
 * @author Cgy
 */
@ConfigurationProperties("yonchain.security.oauth2.authorizationserver")
public class OAuth2AuthorizationServerProperties {

    /**
     * 是否开启验证码（适配密码模式）
     */
    private boolean captchaEnabled = true;

    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }

    public void setCaptchaEnabled(boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

}
