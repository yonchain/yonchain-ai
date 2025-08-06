package com.yonchain.ai.security.oauth2.authorization;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.*;

/**
 * JWT格式的OAuth2访问令牌生成器
 *
 * <p>
 * 实现了OAuth2TokenGenerator接口，用于生成JWT格式的访问令牌 </br>
 * <p>
 * 主要功能：
 * 1. 通过JwtEncoder对令牌进行编码
 * 2. 支持自定义JWT令牌内容
 * 3. 兼容标准的OAuth2令牌生成流程
 */
public class OAuth2JwtAccessTokenGenerator implements OAuth2TokenGenerator<Jwt> {


    private final JwtGenerator jwtGenerator;


    public OAuth2JwtAccessTokenGenerator(JwtEncoder jwtEncoder) {
        this(jwtEncoder, null);
    }

    public OAuth2JwtAccessTokenGenerator(JwtEncoder jwtEncoder, OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        this.jwtGenerator = new JwtGenerator(jwtEncoder);
        this.setJwtCustomizer(jwtCustomizer);
    }


    @Override
    public Jwt generate(OAuth2TokenContext context) {
        //由于JwtGenerator内置的token信息较多，后续可能不需要，所以不直接使用JwtGenerator，使用JwtOAuth2AccessTokenGenerator为后续优化扩展做准备
        return jwtGenerator.generate(context);
    }


    public void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
    }
}
