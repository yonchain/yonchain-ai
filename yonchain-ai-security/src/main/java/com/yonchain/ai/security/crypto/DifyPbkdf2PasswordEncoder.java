package com.yonchain.ai.security.crypto;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.security.Password;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Dify PBKDF2 密码编码器实现
 *
 * <p>
 * 该类实现了Spring Security的PasswordEncoder接口，使用PBKDF2算法进行密码哈希.
 *
 * </p>
 *
 * 与Pbkdf2PasswordEncoder最大的区别是： </br>
 *  第一：编码后需返回盐值存储到数据库，而Pbkdf2PasswordEncoder返回不了盐值 </br>
 *  第二：匹配比较时需用数据库盐值和原始密码进行哈希后比较，而Pbkdf2PasswordEncoder用的是加密密码里面的盐值而不是数据库的盐值 </br>
 *
 * <p>
 *
 * 另外该类主要功能包括：
 *  1. 密码加密(encode) - 生成随机盐值并对密码进行哈希
 *  2. 密码验证(matches) - 验证原始密码与存储的哈希是否匹配
 *
 * </p>
 *
 * 安全特性：
 * - 使用NIST推荐的PBKDF2算法
 * - 默认配置：10000次迭代，256位密钥长度
 * - 每次加密生成随机盐值
 * - 盐值和密码哈希分开存储
 *
 * @author Cgy
 * @since 1.0.0
 */
public class DifyPbkdf2PasswordEncoder implements PasswordEncoder {

    private final String algorithm;
    private final int iterations;
    private final int keyLength;

    public DifyPbkdf2PasswordEncoder(int iterations, int keyLength, String algorithm) {
        this.iterations = iterations;
        this.keyLength = keyLength;
        this.algorithm = algorithm;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            // 生成随机盐值
            byte[] salt = generateSalt();

            String encodedPassword = this.encode(rawPassword, salt);

            // 将盐值编码为Base64
            String saltBase64 = toBase64(salt);

            // 将盐值和密码哈希编码返回给保存到用户表
            return PasswordUtil.encode(encodedPassword, saltBase64);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new YonchainException("密码加密失败", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 解码密码,获取到盐值和密码哈希0
        Password difyPassword = PasswordUtil.decode(encodedPassword);

        try {
            // 解码存储的盐值
            byte[] salt = fromBase64(difyPassword.getSalt());

            String hashPassword = this.encode(rawPassword, salt);

            // 比较计算得到的哈希值与存储的哈希值
            return hashPassword.equals(difyPassword.getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new YonchainException("密码匹配失败", e);
        }
    }


    /**
     * 使用相同的盐值对输入密码进行哈希编码
     *
     * @param rawPassword
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private String encode(CharSequence rawPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 使用相同的盐值对输入密码进行哈希
        byte[] passwordHash = hashPassword(rawPassword.toString(), salt);

        // 将计算得到的哈希值编码为Base64
        return toBase64(passwordHash);
    }

    // 安全增强方法
    public static DifyPbkdf2PasswordEncoder defaultEncoder() {
        return new DifyPbkdf2PasswordEncoder(
                10000,  // NIST推荐的迭代次数
                256,       // 256-bit密钥长度
                "PBKDF2WithHmacSHA256" // 应用级密钥
        );
    }


    /**
     * 使用PBKDF2算法对密码进行哈希，并转换为十六进制字符串
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 哈希后的密码字节数组
     * @throws NoSuchAlgorithmException 如果算法不可用
     * @throws InvalidKeySpecException  如果密钥规格无效
     */
    public byte[] hashPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                this.iterations,
                this.keyLength
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance(this.algorithm);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // 转换为十六进制字符串，与Dify实现一致
        String hexString = bytesToHex(hash);

        // 将十六进制字符串转换为字节数组
        return hexString.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 要转换的字节数组
     * @return 十六进制字符串（小写）
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 将字节数组转换为Base64编码的字符串
     *
     * @param bytes 要编码的字节数组
     * @return Base64编码的字符串
     */
    public static String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将Base64编码的字符串转换为字节数组
     *
     * @param base64String Base64编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] fromBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }


    /**
     * 生成随机盐值
     *
     * @return 随机生成的盐值字节数组
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 128位盐值
        random.nextBytes(salt);
        return salt;
    }
}
