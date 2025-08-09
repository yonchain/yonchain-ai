package com.yonchain.ai.security;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.sys.IdmService;
import com.yonchain.ai.api.sys.UserService;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.security.captcha.CaptchaAuthenticationFilter;
import com.yonchain.ai.security.crypto.YonchainDelegatingPasswordEncoder;
import com.yonchain.ai.security.crypto.PasswordEncoderType;
import com.yonchain.ai.security.oauth2.authorization.*;
import com.yonchain.ai.security.oauth2.authorization.dify.DifyAuthenticationConverter;
import com.yonchain.ai.security.oauth2.authorization.dify.DifyAuthenticationProvider;
import com.yonchain.ai.security.oauth2.exception.YonchainAccessDeniedHandler;
import com.yonchain.ai.security.oauth2.exception.YonchainAuthenticationEntryPoint;
import com.yonchain.ai.security.oauth2.jwt.OAuth2JWKSource;
import com.yonchain.ai.security.oauth2.jwt.OAuth2JwtDecoder;
import com.yonchain.ai.security.oauth2.jwt.OAuth2JwtEncoder;
import com.yonchain.ai.security.service.SecurityServiceImpl;
import com.yonchain.ai.security.user.YonchainUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.*;

/**
 * 安全配置类
 *
 * <p>
 * 采用建造者模式设计，支持链式调用
 * </p>
 *
 * @author Cgy
 */
public class SecurityConfiguration {

    // ======== 核心组件 =========
    protected PasswordEncoder passwordEncoder;

    protected SecurityService securityService;

    // ========= 用户相关组件 ==========
    protected UserDetailsService userDetailsService;

    protected UserService userService;

    protected IdmService idmService;

    // ========== OAuth2 授权服务器 ========
    protected RegisteredClientRepository registeredClientRepository;

    protected OAuth2AuthorizationService authorizationService;

    protected OAuth2TokenGenerator<OAuth2Token> tokenGenerator;

    protected OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer;

    protected CaptchaAuthenticationFilter captchaAuthenticationFilter;

    //用户名密码授权模式
    protected OAuth2PasswordAuthenticationConverter passwordAuthenticationConverter;

    protected OAuth2PasswordAuthenticationProvider passwordAuthenticationProvider;

    //钉钉授权模式
    protected OAuth2DingtalkAuthenticationProvider dingtalkAuthenticationProvider;

    protected OAuth2DingtalkAuthenticationConverter dingtalkAuthenticationConverter;

    //dify 授权模式
    protected DifyAuthenticationProvider difyAuthenticationProvider;

    protected DifyAuthenticationConverter difyAuthenticationConverter;

    protected String passwordEncoderType;

    protected boolean enabledPasswordEncoderPrefix;

    // ========== OAuth2 资源服务器 ==========

    // ========== 数据存储相关组件 ===========
    protected JdbcTemplate jdbcTemplate;

    protected StringRedisTemplate redisTemplate;

    //=========== 自定义异常响应组件 ==========
    protected YonchainAuthenticationEntryPoint authenticationEntryPoint;

    protected YonchainAccessDeniedHandler accessDeniedHandler;

    //=========== JWT ===========
    protected JWKSource<SecurityContext> jwkSource;

    protected JwtDecoder jwtDecoder;

    protected JwtEncoder jwtEncoder;

    protected String privateKey;

    protected String publicKey;

    protected String kid;

    protected String difySecretKey;

    //========== Json ===========
    protected ObjectMapper objectMapper;

    protected SecurityConfiguration(Builder builder) {
        this.passwordEncoder = builder.passwordEncoder;
        this.securityService = builder.securityService;
        this.userDetailsService = builder.userDetailsService;
        this.userService = builder.userService;
        this.registeredClientRepository = builder.registeredClientRepository;
        this.authorizationService = builder.authorizationService;
        this.tokenGenerator = builder.tokenGenerator;
        this.tokenCustomizer = builder.tokenCustomizer;
        this.jdbcTemplate = builder.jdbcTemplate;
        this.redisTemplate = builder.redisTemplate;
        this.enabledPasswordEncoderPrefix = builder.enabledPasswordEncoderPrefix;
        this.passwordEncoderType = builder.passwordEncoderType;
        this.jwkSource = builder.jwkSource;
        this.jwtEncoder = builder.jwtEncoder;
        this.jwtDecoder = builder.jwtDecoder;
        this.privateKey = builder.privateKey;
        this.publicKey = builder.publicKey;
        this.kid = builder.kid;
        this.objectMapper = builder.objectMapper;
        this.idmService = builder.idmService;
        this.difySecretKey = builder.difySecretKey;

        // 初始化默认组件
        init();
    }


    protected void init() {
        // 初始化密码编码器（如果未设置）
        if (this.passwordEncoder == null) {
            this.passwordEncoder = new YonchainDelegatingPasswordEncoder();
        }

        // 初始化令牌定制器（如果未设置）
        if (this.tokenCustomizer == null) {
            this.tokenCustomizer = new YonchainOAuth2TokenCustomizer();
        }

        // 初始化Redis模板（如果未设置）
        if (this.redisTemplate == null) {
            this.redisTemplate = new StringRedisTemplate();
        }

        // 初始化ObjectMapper（如果未设置）
        if (this.objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        // 初始化注册客户端仓库（如果未设置）
        if (registeredClientRepository == null) {
            registeredClientRepository = new YonchainJdbcRegisteredClientRepository(jdbcTemplate);
        }

        // 初始化JWK源（如果未设置）
        if (jwkSource == null) {
            jwkSource = new OAuth2JWKSource(privateKey, publicKey, kid);
        }

        // 初始化JWT编码器（如果未设置）
        if (jwtEncoder == null) {
            jwtEncoder = new OAuth2JwtEncoder(jwkSource);
        }

        // 初始化JWT解码器（如果未设置）
        if (jwtDecoder == null) {
            jwtDecoder = new OAuth2JwtDecoder(jwkSource);
        }

        // 初始化令牌生成器（如果未设置）
        if (tokenGenerator == null) {
            tokenGenerator = new DelegatingOAuth2TokenGenerator(
                    new OAuth2JwtAccessTokenGenerator(jwtEncoder, tokenCustomizer),
                    new OAuth2AccessTokenGenerator(),
                    new OAuth2RefreshTokenGenerator()
            );
        }

        //初始化验证码认证过滤器
        captchaAuthenticationFilter = new CaptchaAuthenticationFilter(redisTemplate, objectMapper);

        // 初始化OAuth2用户名密码认证提供者
        passwordAuthenticationConverter = new OAuth2PasswordAuthenticationConverter();

        // 初始化自定义异常响应组件
        authenticationEntryPoint = new YonchainAuthenticationEntryPoint(objectMapper);
        accessDeniedHandler = new YonchainAccessDeniedHandler(objectMapper);

        if (userDetailsService == null) {
            userDetailsService = new YonchainUserDetailsService(userService);
        }

        // 初始化授权服务器
        if (authorizationService == null) {
            authorizationService = new YonchainJdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
        }

        //初始化OAuth2用户名密码认证转换器
        passwordAuthenticationProvider = new OAuth2PasswordAuthenticationProvider(
                userDetailsService, authorizationService, tokenGenerator, passwordEncoder);
        passwordAuthenticationProvider.setPasswordEncoderType(passwordEncoderType);
        passwordAuthenticationProvider.setEnabledPasswordEncoderPrefix(enabledPasswordEncoderPrefix);

        //初始化服务
        if (securityService == null) {
            securityService = new SecurityServiceImpl(passwordEncoder, jwtDecoder);
        }

        //初始化钉钉授权模式
        dingtalkAuthenticationProvider = new OAuth2DingtalkAuthenticationProvider(
                userDetailsService, authorizationService, tokenGenerator,
                passwordEncoder, idmService);
        //初始化钉钉授权模式认证转换器
        dingtalkAuthenticationConverter = new OAuth2DingtalkAuthenticationConverter();

        //初始化dify授权模式
        difyAuthenticationProvider = new DifyAuthenticationProvider(userDetailsService, authorizationService, jwtDecoder,difySecretKey);
        //初始化dify授权模式认证转换器
        difyAuthenticationConverter = new DifyAuthenticationConverter();
    }

    /**
     * 获取安全服务
     *
     * @return 安全服务实例
     */
    public SecurityService getSecurityService() {
        return securityService;
    }

    /**
     * 获取用户详情服务
     *
     * @return 用户详情服务实例
     */
    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }


    /**
     * 获取注册客户端仓库
     *
     * @return 注册客户端仓库实例
     */
    public RegisteredClientRepository getRegisteredClientRepository() {
        return registeredClientRepository;
    }

    /**
     * 获取OAuth2授权服务
     *
     * @return OAuth2授权服务实例
     */
    public OAuth2AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    /**
     * 获取OAuth2令牌生成器
     *
     * @return OAuth2令牌生成器实例
     */
    public OAuth2TokenGenerator<OAuth2Token> getTokenGenerator() {
        return tokenGenerator;
    }


    /**
     * 获取OAuth2用户名密码认证提供者
     *
     * @return OAuth2用户名密码认证提供者实例
     */
    public OAuth2PasswordAuthenticationProvider getUsernamePasswordAuthenticationProvider() {
        return passwordAuthenticationProvider;
    }

    /**
     * 获取用户名密码认证转换器
     *
     * @return 用户名密码认证转换器
     */
    public OAuth2PasswordAuthenticationConverter getUsernamePasswordAuthenticationConverter() {
        return passwordAuthenticationConverter;
    }

    /**
     * 获取密码编码器
     *
     * @return 密码编码器
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    /**
     * 获取验证码认证过滤器
     *
     * @return 验证码认证过滤器实例
     */
    public CaptchaAuthenticationFilter getCaptchaAuthenticationFilter() {
        return captchaAuthenticationFilter;
    }

    /**
     * 获取认证入口点
     *
     * @return 认证入口点实例
     */
    public YonchainAuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    /**
     * 获取访问拒绝处理器
     *
     * @return 访问拒绝处理器实例
     */
    public YonchainAccessDeniedHandler getAccessDeniedHandler() {
        return accessDeniedHandler;
    }

    /**
     * 获取钉钉授权模式认证提供者
     *
     * @return 钉钉授权模式认证提供者实例
     */
    public OAuth2DingtalkAuthenticationProvider getDingtalkAuthenticationProvider() {
        return dingtalkAuthenticationProvider;
    }

    /**
     * 获取钉钉授权模式认证转换器
     *
     * @return 钉钉授权模式认证转换器实例
     */
    public OAuth2DingtalkAuthenticationConverter getDingtalkAuthenticationConverter() {
        return dingtalkAuthenticationConverter;
    }

    /**
     * 获取dify授权模式认证提供者
     *
     * @return dify授权模式认证提供者实例
     */
    public DifyAuthenticationProvider getDifyAuthenticationProvider() {
        return difyAuthenticationProvider;
    }

    /**
     * 获取dify授权模式认证转换器
     *
     * @return dify授权模式认证转换器实例
     */
    public DifyAuthenticationConverter difyAuthenticationConverter() {
        return difyAuthenticationConverter;
    }

    /**
     * 获取dify密钥
     *
     * @return 构建器实例
     */
    public String getDifySecretKey() {
        return difySecretKey;
    }

    /**
     * 创建新的安全配置构建器
     *
     * @return 安全配置构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 安全配置构建器
     * 用于构建SecurityConfiguration实例
     */
    public static class Builder {

        // 核心组件
        protected PasswordEncoder passwordEncoder;

        protected SecurityService securityService;

        // 用户相关组件
        protected UserDetailsService userDetailsService;

        protected UserService userService;

        protected IdmService idmService;

        // OAuth2相关组件
        protected RegisteredClientRepository registeredClientRepository;

        protected OAuth2AuthorizationService authorizationService;

        protected OAuth2TokenGenerator<OAuth2Token> tokenGenerator;

        protected OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer;

        // 配置密码编码器类型，默认为Dify的PBKDF2加密器
        private String passwordEncoderType = PasswordEncoderType.DIFY_PBKDF2;

        // 是否启用密码编码器前缀，默认为true
        private boolean enabledPasswordEncoderPrefix = true;

        // 数据存储相关组件
        protected JdbcTemplate jdbcTemplate;

        protected StringRedisTemplate redisTemplate;

        //JWT
        protected JWKSource<SecurityContext> jwkSource;

        protected JwtDecoder jwtDecoder;

        protected JwtEncoder jwtEncoder;

        protected String privateKey;

        protected String publicKey;

        protected String kid;

        public String difySecretKey;

        // Json
        protected ObjectMapper objectMapper;


        /**
         * 设置密码编码器
         *
         * @param passwordEncoder 密码编码器
         * @return 构建器实例
         */
        public Builder passwordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        /**
         * 设置安全服务
         *
         * @param securityService 安全服务
         * @return 构建器实例
         */
        public Builder securityService(SecurityService securityService) {
            this.securityService = securityService;
            return this;
        }

        /**
         * 设置用户详情服务
         *
         * @param userDetailsService 用户详情服务
         * @return 构建器实例
         */
        public Builder userDetailsService(UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
            return this;
        }

        /**
         * 设置用户服务
         *
         * @param userService 用户服务
         * @return 构建器实例
         */
        public Builder userService(UserService userService) {
            this.userService = userService;
            return this;
        }

        /**
         * 设置身份管理服务
         *
         * @param idmService 身份管理服务
         * @return 构建器实例
         */
        public Builder idmService(IdmService idmService) {
            this.idmService = idmService;
            return this;
        }

        /**
         * 设置注册客户端仓库
         *
         * @param registeredClientRepository 注册客户端仓库
         * @return 构建器实例
         */
        public Builder registeredClientRepository(RegisteredClientRepository registeredClientRepository) {
            this.registeredClientRepository = registeredClientRepository;
            return this;
        }

        /**
         * 设置OAuth2授权服务
         *
         * @param authorizationService OAuth2授权服务
         * @return 构建器实例
         */
        public Builder authorizationService(OAuth2AuthorizationService authorizationService) {
            this.authorizationService = authorizationService;
            return this;
        }

        /**
         * 设置OAuth2令牌生成器
         *
         * @param tokenGenerator OAuth2令牌生成器
         * @return 构建器实例
         */
        public Builder tokenGenerator(OAuth2TokenGenerator<OAuth2Token> tokenGenerator) {
            this.tokenGenerator = tokenGenerator;
            return this;
        }

        /**
         * 设置JWK源
         *
         * @param jwkSource JWK源
         * @return 构建器实例
         */
        public Builder jwkSource(JWKSource<SecurityContext> jwkSource) {
            this.jwkSource = jwkSource;
            return this;
        }

        /**
         * 设置JWT编码器
         *
         * @param jwtEncoder JWT编码器
         * @return 构建器实例
         */
        public Builder jwtEncoder(JwtEncoder jwtEncoder) {
            this.jwtEncoder = jwtEncoder;
            return this;
        }

        /**
         * 设置JWT解码器
         *
         * @param jwtDecoder JWT解码器
         * @return 构建器实例
         */
        public Builder jwtDecoder(JwtDecoder jwtDecoder) {
            this.jwtDecoder = jwtDecoder;
            return this;
        }

        /**
         * 设置私钥
         *
         * @param privateKey 私钥
         * @return 构建器实例
         */
        public Builder privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        /**
         * 设置公钥
         *
         * @param publicKey 公钥
         * @return 构建器实例
         */
        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        /**
         * 设置kid
         *
         * @param kid kid
         * @return 构建器实例
         */
        public Builder kid(String kid) {
            this.kid = kid;
            return this;
        }

        /**
         * 设置OAuth2令牌定制器
         *
         * @param tokenCustomizer OAuth2令牌定制器
         * @return 构建器实例
         */
        public Builder tokenCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer) {
            this.tokenCustomizer = tokenCustomizer;
            return this;
        }

        /**
         * 设置JDBC模板
         *
         * @param jdbcTemplate JDBC模板
         * @return 构建器实例
         */
        public Builder jdbcTemplate(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
            return this;
        }

        /**
         * 设置Redis模板
         *
         * @param redisTemplate Redis模板
         * @return 构建器实例
         */
        public Builder redisTemplate(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }

        /**
         * 设置密码编码器类型
         *
         * @param passwordEncoderType 密码编码器类型
         * @return 构建器实例
         */
        public Builder passwordEncoderType(String passwordEncoderType) {
            this.passwordEncoderType = passwordEncoderType;
            return this;
        }

        /**
         * 设置是否启用密码编码器前缀
         *
         * @param enabledPasswordEncoderPrefix 是否启用前缀
         * @return 构建器实例
         */
        public Builder enabledPasswordEncoderPrefix(boolean enabledPasswordEncoderPrefix) {
            this.enabledPasswordEncoderPrefix = enabledPasswordEncoderPrefix;
            return this;
        }

        /**
         * 设置Json转换器
         *
         * @param objectMapper Json转换器
         * @return 构建器实例
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * 设置钉钉密钥
         *
         * @param difySecretKey 钉钉密钥
         * @return 构建器实例
         */
        public Builder difySecretKey(String difySecretKey) {
            this.difySecretKey = difySecretKey;
            return this;
        }

        /**
         * 构建安全配置实例
         *
         * @return 安全配置实例
         */
        public SecurityConfiguration build() {
            // 验证配置属性
            validate();

            // 返回配置实例
            return new SecurityConfiguration(this);
        }

        private void validate() {
            // 验证必需的组件
            if (userService == null) {
                throw new YonchainException("用户服务未设置");
            }
            if (jdbcTemplate == null) {
                throw new YonchainException("JDBC模板未设置");
            }
            if (jwtEncoder == null) {
                throw new YonchainException("JWT编码器未设置");
            }
            if (idmService == null) {
                throw new YonchainException("身份服务未设置");
            }
        }

    }

}


