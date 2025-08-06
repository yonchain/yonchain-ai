package com.yonchain.ai.security.oauth2.authorization.jackson2;

import com.yonchain.ai.security.oauth2.authorization.YonchainOAuth2AuthorizationToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class YonchainOAuth2AuthorizationServerJackson2Module extends SimpleModule {

    public YonchainOAuth2AuthorizationServerJackson2Module() {
        super(YonchainOAuth2AuthorizationServerJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(YonchainOAuth2AuthorizationToken.class, YonchainOAuthAuthorizationTokenMixin.class);
    }
}