package com.yonchain.ai.security.service;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.security.Password;
import com.yonchain.ai.api.security.SecurityService;
import com.yonchain.ai.security.crypto.PasswordEncoderType;
import com.yonchain.ai.security.crypto.PasswordUtil;
import com.yonchain.ai.util.Assert;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.text.ParseException;
import java.util.Map;

/**
 * 安全服务实现类
 * <p>
 * 提供密码编码、密码匹配、令牌解析等功能
 *
 * @author Cgy
 */
public class SecurityServiceImpl implements SecurityService {

    private final PasswordEncoder passwordEncoder;

    private final JwtDecoder jwtDecoder;

    public SecurityServiceImpl(PasswordEncoder passwordEncoder,
                               JwtDecoder jwtDecoder) {
        this.passwordEncoder = passwordEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String encodePassword(CharSequence rawPassword, String salt) {
        Assert.notNull(rawPassword, "rawPassword不能为空");
        Assert.hasText(salt, "salt不能为空");
        rawPassword = PasswordUtil.encode(rawPassword.toString(), salt);
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matchesPassword(CharSequence rawPassword, String salt, String encodedPassword) {
        encodedPassword = PasswordUtil.encode(encodedPassword, salt);
        encodedPassword = "{dify@pbkdf2}" + encodedPassword;
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String getUserIdFromToken(String token) {
        Map<String, Object> claims = this.getClaimsFromToken(token);
        return (String) claims.get("user_id");
    }

    @Override
    public Map<String, Object> getClaimsFromToken(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            return jwt.getJWTClaimsSet().getClaims();
        } catch (ParseException e) {
            throw new YonchainException("Token解析失败", e);
        }
    }

    @Override
    public Password encodePassword(String rawPassword) {
        Assert.notNull(rawPassword, "原始密码不能为空");
        String password = passwordEncoder.encode(rawPassword);
        // 如果是dify算法密码，由于dify规范的密码不需要前缀，因此需去除密码算法前缀
        String prefix = "{" + PasswordEncoderType.DIFY_PBKDF2 + "}";
        if (password.startsWith(prefix)) {
            password = password.substring(prefix.length());
            return PasswordUtil.decode(password);
        }
        return new Password(password, null);

    }
}
