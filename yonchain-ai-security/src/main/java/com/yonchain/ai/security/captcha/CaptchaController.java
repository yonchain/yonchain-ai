package com.yonchain.ai.security.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    /*private final DefaultKaptcha defaultKaptcha;
    private final StringRedisTemplate redisTemplate;*/

    public static final String CAPTCHA_KEY_PREFIX = "captcha:";

    @GetMapping
    public void generateCaptcha(@RequestParam(required = false) String captchaKey, HttpServletResponse response) throws IOException {
        // 如果没有提供key，则生成一个
        captchaKey = captchaKey != null ? captchaKey : UUID.randomUUID().toString();

      /*  // 生成验证码文本
        String captchaText = defaultKaptcha.createText();
        log.debug("Generated captcha: {} for id: {}", captchaText, captchaKey);

        // 将验证码存储到Redis，设置过期时间
        redisTemplate.opsForValue().set(
                CAPTCHA_KEY_PREFIX + captchaKey,
                captchaText,
                60,
                TimeUnit.SECONDS);

        // 生成验证码图片
        BufferedImage captchaImage = defaultKaptcha.createImage(captchaText);

        // 设置响应头
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader("Captcha-Key", captchaKey);
        response.setHeader("Cache-Control", "no-store, no-cache");

        // 输出图片
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(captchaImage, "png", outputStream);
        outputStream.flush();*/

    }
}
