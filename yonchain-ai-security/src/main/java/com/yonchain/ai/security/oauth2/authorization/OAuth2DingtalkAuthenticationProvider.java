package com.yonchain.ai.security.oauth2.authorization;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.yonchain.ai.api.idm.*;
import com.yonchain.ai.api.idm.enums.RoleType;
import com.yonchain.ai.security.user.Dify4jUser;
import com.yonchain.ai.util.IdUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2密码认证提供者
 *
 * <p>
 * 该认证提供者用于处理OAuth2密码授权模式的认证流程。
 * </p>
 * 主要功能包括：</br>
 * 1. 验证客户端是否支持密码授权模式 </br>
 * 2. 验证用户凭据（用户名和密码） </br>
 * 3. 生成访问令牌和刷新令牌 </br>
 * 4. 保存授权信息到OAuth2授权服务 </br>
 * <p>
 * 使用场景：适用于需要用户名密码直接获取访问令牌的OAuth2授权流程
 *
 * @author Cgy
 * @since 1.0.0
 */
public class OAuth2DingtalkAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final UserDetailsService userDetailsService;

    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    private final IdmService idmService;


    public OAuth2DingtalkAuthenticationProvider(UserDetailsService userDetailsService,
                                                OAuth2AuthorizationService authorizationService,
                                                OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                                PasswordEncoder passwordEncoder,
                                                IdmService idmService) {
        this.userDetailsService = userDetailsService;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.idmService = idmService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2DingtalkAuthenticationToken dingtalkAuthenticationToken = (OAuth2DingtalkAuthenticationToken) authentication;

        // 获取OAuth2客户端认证信息
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(dingtalkAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 验证客户端是否支持钉钉授权码授权类型
        if (!registeredClient.getAuthorizationGrantTypes().contains(OAuth2AuthorizationGrantTypes.DINGTALK)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, "客户端不支持该授权类型", ERROR_URI));
        }

        // 获取钉钉授权码
        String code = dingtalkAuthenticationToken.getCode();

        //根据钉钉授权码获取钉钉用户token
        String dingtalkToken = this.getDingtalkTAccessToken(code);

        //根据钉钉token获取钉钉用户信息
        DingtalkUser dingtalkUser = this.getDingtalkUser(dingtalkToken);

        //判断用户是否存在，如果不存在，创建用户。根据邮箱查找用户，如果邮箱为空，则使用格式为openId@dingtalk.com作为邮箱查找用户
        String email = dingtalkUser.getEmail();
        Dify4jUser userDetails = (Dify4jUser) userDetailsService.loadUserByUsername(email);
        if (userDetails == null) {
            this.createUser(dingtalkUser);
            userDetails = (Dify4jUser) userDetailsService.loadUserByUsername(email);
        }

        // 创建用户认证令牌
        YonchainOAuth2AuthorizationToken dify4jAuthorization = YonchainOAuth2AuthorizationToken.authenticated(userDetails.getUserId(), userDetails.getAuthorities());

        // 准备令牌上下文
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(dify4jAuthorization)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(dingtalkAuthenticationToken.getScopes())
                .authorizationGrantType(OAuth2AuthorizationGrantTypes.DINGTALK)
                .authorizationGrant(dingtalkAuthenticationToken);

        // 生成访问令牌
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "生成访问令牌失败", ERROR_URI));
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(),
                generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(),
                tokenContext.getAuthorizedScopes());

        // 生成刷新令牌
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (generatedRefreshToken == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "生成刷新令牌失败", ERROR_URI));
            }
            refreshToken = new OAuth2RefreshToken(generatedRefreshToken.getTokenValue(), generatedRefreshToken.getIssuedAt(), generatedRefreshToken.getExpiresAt());
        }

        // 保存授权信息
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(userDetails.getUsername())
                .authorizationGrantType(OAuth2AuthorizationGrantTypes.DINGTALK)
                .attribute(Principal.class.getName(), dify4jAuthorization);
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }
        if (refreshToken != null) {
            authorizationBuilder.refreshToken(refreshToken);
        }
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        // 构建额外参数
        Map<String, Object> additionalParameters = new HashMap<>();
        if (refreshToken != null) {
            additionalParameters.put(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        }

        // 返回访问令牌认证结果
        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2DingtalkAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }


    private void createUser(DingtalkUser dingtalkUser) {
        try {
            // 创建租户 - 使用钉钉用户的昵称作为租户名称
            Tenant tenant = new DefaultTenant();
            tenant.setId(IdUtil.generateId());
            tenant.setName(dingtalkUser.getNick() + "的工作空间");
            idmService.getTenantService().createTenant(tenant);

            // 查找超级管理员角色ID
            List<String> roles = idmService.getRoleService().getSystemRoles(tenant.getId())
                    .stream()
                    .filter(role -> RoleType.OWNER.getValue().equals(role.getCode()))
                    .map(Role::getId)
                    .toList();

            // 创建用户
            User newUser = new DefaultUser();
            newUser.setId(IdUtil.generateId());
            newUser.setName(dingtalkUser.getNick()); // 使用钉钉OpenID作为用户名
            newUser.setEmail(dingtalkUser.getEmail()); // 设置邮箱
            newUser.setName(dingtalkUser.getNick()); // 设置用户昵称
            newUser.setAvatar(dingtalkUser.getAvatarUrl()); // 设置用户头像
            newUser.setPassword("123456"); // 密码默认123456

            // 创建用户
            idmService.getUserService().createUser(tenant.getId(), newUser, roles);

            // 为用户分配超级管理员角色
            //userService.assignRole(createdUser.getId(), adminRoleId);

        } catch (Exception e) {
            // 处理异常，转换为OAuth2认证异常
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.SERVER_ERROR,
                    "创建用户失败: " + e.getMessage(),
                    ERROR_URI
            ));
        }
    }


    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public com.aliyun.dingtalkoauth2_1_0.Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }


    private DingtalkUser getDingtalkUser(String dingtalkToken) {
        try {
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            com.aliyun.dingtalkcontact_1_0.Client client = new com.aliyun.dingtalkcontact_1_0.Client(config);
            GetUserHeaders getUserHeaders = new GetUserHeaders();
            getUserHeaders.xAcsDingtalkAccessToken = dingtalkToken;
            GetUserResponse response = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
            GetUserResponseBody body = response.body;
            return new DingtalkUser(body.loginEmail, body.nick, body.getAvatarUrl(), body.openId);

         //   return new DingtalkUser(null, "test", "https://work.dingtalk.com/favicon.ico", IdUtil.generateId());
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, err.message, ERROR_URI));
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, err.message, ERROR_URI));

        }
    }

    public String getDingtalkTAccessToken(String code) {
        try {
            com.aliyun.dingtalkoauth2_1_0.Client client = createClient();
            GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                    .setClientId("dingiy7eptqtptzxc76i")
                    .setClientSecret("DKazCTZOenWzsQvurdXNZKT-XBb9GHlRlqdmxlLpiPJp1boxVmfEfPMiALKMwH72")
                    .setCode(code)
                    .setRefreshToken("")
                    .setGrantType("authorization_code");
            GetUserTokenResponse response = client.getUserToken(getUserTokenRequest);
            return response.body.accessToken;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, err.message, ERROR_URI));

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, err.message, ERROR_URI));
        }

    }

    /**
     * 钉钉用户信息记录类
     *
     * <p>
     * 用于封装从钉钉API获取的用户信息，包含以下字段：
     * </p>
     * <ul>
     *   <li>loginEmail - 用户登录邮箱</li>
     *   <li>nick - 用户昵称</li>
     *   <li>avatarUrl - 用户头像URL</li>
     *   <li>openId - 用户在钉钉的唯一标识</li>
     * </ul>
     */
    private class DingtalkUser {

        private String loginEmail;
        private String nick;
        private String avatarUrl;
        String openId;

        public DingtalkUser(String loginEmail, String nick, String avatarUrl, String openId) {
            this.avatarUrl = avatarUrl;
            this.nick = nick;
            this.openId = openId;
            this.loginEmail = loginEmail;
        }

        /**
         * 如果邮箱为空，则使用格式为openId@dingtalk.com作为邮箱查找用户
         *
         * @return 邮箱
         */
        public String getEmail() {
            return StringUtils.hasText(loginEmail) ? loginEmail : openId + "@dingtalk.com";
        }

        public void setEmail(String loginEmail) {
            this.loginEmail = loginEmail;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }
    }
}
