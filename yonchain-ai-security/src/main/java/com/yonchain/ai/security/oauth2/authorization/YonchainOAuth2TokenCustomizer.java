package com.yonchain.ai.security.oauth2.authorization;

import com.yonchain.ai.security.oauth2.jwt.JwtConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * 自定义OAuth2令牌内容的定制器
 *
 * <p>
 * 该类负责向JWT令牌中添加自定义声明，如用户信息、权限等。
 * 通过实现OAuth2TokenCustomizer接口，可以在令牌生成过程中添加或修改JWT声明。
 * </p>
 *
 * @author Cgy
 * @since 1.0.0
 */
public class YonchainOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private static final Logger logger = LoggerFactory.getLogger(YonchainOAuth2TokenCustomizer.class);

    /**
     * 自定义JWT令牌的声明内容
     *
     * @param context JWT编码上下文，包含令牌信息和认证信息
     */
    @Override
    public void customize(JwtEncodingContext context) {
        if (context == null || context.getClaims() == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.SERVER_ERROR,
                    "JWT编码上下文或声明为空，无法自定义令牌",
                    null));
        }

        JwtClaimsSet.Builder claims = context.getClaims();
        claims.issuer(JwtConstant.JWT_ISSUER);

        // 只处理访问令牌和刷新令牌
        String tokenType = context.getTokenType().getValue();
        if (OAuth2TokenType.ACCESS_TOKEN.getValue().equals(tokenType) ||
                OAuth2TokenType.REFRESH_TOKEN.getValue().equals(tokenType)) {
            // 添加令牌元数据
            addTokenMetadata(context, claims);

            // 添加用户信息
            addUserInformation(context, claims);
        }
    }

    /**
     * 添加令牌相关元数据
     *
     * @param context JWT编码上下文
     * @param claims  JWT声明构建器
     */
    private void addTokenMetadata(JwtEncodingContext context, JwtClaimsSet.Builder claims) {
        try {
            if (context.getRegisteredClient() != null) {
                claims.claim("client_id", context.getRegisteredClient().getClientId());
            }
        } catch (Exception e) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.SERVER_ERROR,
                    "添加令牌元数据时发生错误",
                    null));
        }
    }

    /**
     * 添加用户相关信息
     *
     * @param context JWT编码上下文
     * @param claims  JWT声明构建器
     */
    private void addUserInformation(JwtEncodingContext context, JwtClaimsSet.Builder claims) {
        Authentication principal = context.getPrincipal();
        if (principal == null) {
            logger.debug("认证主体为空，跳过用户信息添加");
            return;
        }

        if (principal instanceof YonchainOAuth2AuthorizationToken token){
            claims.claim("user_id", token.getPrincipal());
        }else {
            logger.debug("认证主体不是yonchainOAuth2AuthorizationToken类型，跳过用户信息添加");
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.SERVER_ERROR,
                    "生成Token失败,未知的认证主体无法获取用户信息",
                    null));
        }
    }
}
