package com.yonchain.ai.security.captcha;

import com.yonchain.ai.security.oauth2.authorization.OAuth2AuthorizationGrantTypes;
import com.yonchain.ai.web.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.yonchain.ai.security.captcha.CaptchaController.CAPTCHA_KEY_PREFIX;


/**
 * 验证码认证过滤器，用于在登录请求时校验验证码
 * 主要功能：
 * 1. 拦截登录请求(/oauth2/token)
 * 2. 校验请求中的验证码参数(verifyCode和captchaKey)
 * 3. 与Redis中存储的验证码进行比对
 * 4. 验证失败时返回错误信息
 * 5. 验证成功后删除Redis中的验证码
 */
public class CaptchaAuthenticationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CaptchaAuthenticationFilter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 只对密码模式登录请求进行验证码校验
        if (!request.getRequestURI().equals("/oauth2/token") && !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if(!OAuth2AuthorizationGrantTypes.PASSWORD.getValue().equals(grantType)){
            filterChain.doFilter(request, response);
            return;
        }


        String verifyCode = request.getParameter("verifyCode");
        String captchaKey = request.getParameter("captchaKey");

        if (verifyCode == null || captchaKey == null) {
            handleCaptchaError(response, "验证码不能为空");
            return;
        }

        String key = CAPTCHA_KEY_PREFIX + captchaKey;
        String storedCaptcha = redisTemplate.opsForValue().get(key);

        if (storedCaptcha == null) {
            handleCaptchaError(response, "验证码错误或已失效");
            return;
        }

        if (!verifyCode.equalsIgnoreCase(storedCaptcha)) {
            handleCaptchaError(response, "验证码错误或已失效");
            return;
        }

        // 验证成功后删除验证码
        redisTemplate.delete(key);

        filterChain.doFilter(request, response);
    }

    private void handleCaptchaError(HttpServletResponse response, String message) throws IOException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorResponse errorResponse = new ApiErrorResponse(status.value(), "captcha_invalid", message);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }


}
