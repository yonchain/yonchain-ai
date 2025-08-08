package com.yonchain.ai.autoconfigure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.yonchain.ai.api.app.AppService;
import com.yonchain.ai.api.idm.IdmService;
import com.yonchain.ai.api.idm.UserService;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.security.SecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Properties;

/**
 * 安全服务 自动装配配置
 *
 * @author Cgy
 */
@AutoConfiguration(after = OAuth2JwtAutoConfiguration.class)
@ConditionalOnClass(SecurityService.class)
@EnableConfigurationProperties(SecurityProperties.class)
@Import({OAuth2AuthorizationServerConfiguration.class, Oauth2ResourceServerConfiguration.class})
public class SecurityAutoConfiguration {

    @Bean
    public SecurityConfiguration securityConfiguration(JdbcTemplate jdbcTemplate,
                                                       UserService userService,
                                                       StringRedisTemplate redisTemplate,
                                                       ObjectMapper objectMapper,
                                                       JWKSource<SecurityContext> jwkSource,
                                                       JwtEncoder jwtEncoder,
                                                       JwtDecoder jwtDecoder,
                                                       SecurityProperties securityProperties,
                                                       IdmService idmService,
                                                       AppService appService) {
        return SecurityConfiguration.builder()
                .jdbcTemplate(jdbcTemplate)
                .redisTemplate(redisTemplate)
                .objectMapper(objectMapper)
                .userService(userService)
                .jwkSource(jwkSource)
                .jwtEncoder(jwtEncoder)
                .jwtDecoder(jwtDecoder)
                .passwordEncoderType(securityProperties.getPasswordEncoderType())
                .enabledPasswordEncoderPrefix(securityProperties.isEnablePasswordEncoderPrefix())
                .idmService(idmService)
               // .appService(appService)
                .difySecretKey(securityProperties.getDifySecretKey())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityService securityService(SecurityConfiguration securityConfiguration) {
        return securityConfiguration.getSecurityService();
    }


    @Bean
    @ConditionalOnMissingBean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();

        // 图片边框
        properties.setProperty("kaptcha.border", "no");

        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "red");

        // 图片宽度
        properties.setProperty("kaptcha.image.width", "150");

        // 图片高度
        properties.setProperty("kaptcha.image.height", "50");

        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");

        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");

        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");

        // 干扰线实现类（设置为NoNoise表示不使用干扰线）
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 文字间隔
        properties.setProperty("kaptcha.textproducer.char.space", "5");

        //背景颜色 (使用RGB数值)
        properties.setProperty("kaptcha.background.clear.to","36,180,255"); // 稍浅的蓝色(#1CA0FF)

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

}
