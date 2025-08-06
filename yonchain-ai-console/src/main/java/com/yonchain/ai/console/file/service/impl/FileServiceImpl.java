package com.yonchain.ai.console.file.service.impl;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.storage.StorageService;
import com.yonchain.ai.console.file.entity.FileEntity;
import com.yonchain.ai.console.file.mapper.FileMapper;
import com.yonchain.ai.console.file.service.FileService;
import com.yonchain.ai.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 文件服务实现类
 *
 * @author Cgy
 * @since 1.0.0
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${storage.type:local}")
    private String storageType;

    @Value("${storage.local.path:storage}")
    private String storageLocalPath;

    @Value("${file.max-size:52428800}") // 默认50MB
    private long maxFileSize;

    @Value("${file.files-base-url:52428800}") // 默认50MB
    private String filesBaseUrl;

    @Value("${file.secret-key:52428800}") // 默认50MB
    private String secretKey;

    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "txt", "markdown", "md", "pdf", "html", "htm", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "csv", "json","png","jpg"
    );

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private StorageService storageService;

    @Override
    public FileEntity getFileById(String id) {
        return fileMapper.selectById(id);
    }

    @Override
    public Page<FileEntity> pageFiles(String tenantId, Map<String, Object> queryParam, int page, int limit) {
        long total = fileMapper.countByTenantIdAndParams(tenantId, queryParam);
        List<FileEntity> files = fileMapper.selectPageByTenantIdAndParams(tenantId, queryParam, (page - 1) * limit, limit);
        return PageUtil.convert(files);
    }

    @Override
    public void deleteFileById(String id) {
        fileMapper.deleteById(id);
    }

    @Override
    public void deleteFileByIds(List<String> ids) {
        fileMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileEntity uploadFile(String filename, byte[] content, String contentType, String currentUserId, String tenantId) {

        // 检查文件大小
        if (content.length > maxFileSize) {
            throw new YonchainIllegalStateException("文件大小超过限制");
        }

        // 获取文件扩展名
        String extension = getFileExtension(filename);

        // 检查文件类型
        if (!SUPPORTED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new YonchainIllegalStateException("文件类型不支持");
        }

        // 生成文件键
        String fileId = UUID.randomUUID().toString();
        String fileKey = "upload_files/" + tenantId + "/" + fileId + "." + extension;

        // 保存文件到存储系统
        storageService.uploadFile(fileKey, content);

        // 创建并返回上传文件对象
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setTenantId(tenantId);
        fileEntity.setStorageType(storageType);
        fileEntity.setKey(fileKey);
        fileEntity.setName(filename);
        fileEntity.setSize(content.length);
        fileEntity.setExtension(extension);
        fileEntity.setMimeType(contentType);
        fileEntity.setCreatedBy(currentUserId);
        fileEntity.setCreatedAt(LocalDateTime.now());
        fileEntity.setUsed(false);
        fileEntity.setSourceUrl(this.getSignedFileUrl(fileId));
        fileEntity.setCreatedByRole("account");

        // 保存到数据库
        fileMapper.insert(fileEntity);

        return fileEntity;
    }

    @Override
    public boolean validateSignedUrl(String fileId, long timestamp, String nonce,String sign) {
        // 1. 验证时间戳是否过期（5分钟内有效）
        long currentTime = Instant.now().getEpochSecond();
        if (Math.abs(currentTime - timestamp) > 300) {
            return false;
        }

        // 2. 获取文件信息
        FileEntity file = fileMapper.selectById(fileId);
        if (file == null) {
            return false;
        }

        // 3. 构造签名消息
        String message = String.format("file-preview|%s|%s|%s",
                fileId, timestamp, nonce);

        // 4. 计算HMAC签名
        String expectedSign = calculateHmacSha256(message, secretKey);

        // 5. 比较签名是否一致
        return sign.equals(Base64.getUrlEncoder().withoutPadding().encodeToString(
                HexFormat.of().parseHex(expectedSign)
        ));
    }


    /**
     * 生成带签名的文件URL
     *
     * @param fileId 文件ID
     * @return 签名URL
     */
    public String getSignedFileUrl(String fileId) {
        // 1. 基础URL构造
       // String baseUrl = filesBaseUrl + "/files/" + fileId + "/file-preview";
        String baseUrl = "/files/" + fileId + "/file-preview";

        // 2. 生成安全参数
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String nonce = generateNonce();

        // 3. 生成签名消息
        String message = String.format("file-preview|%s|%s|%s",
                fileId, timestamp, nonce);

        // 4. 计算HMAC签名
        String signature = calculateHmacSha256(message, secretKey);

        // 5. 构造最终URL
        return String.format("%s?timestamp=%s&nonce=%s&sign=%s",
                baseUrl,
                timestamp,
                nonce,
                Base64.getUrlEncoder().withoutPadding().encodeToString(
                        HexFormat.of().parseHex(signature)
                )
        );
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    private String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String calculateHmacSha256(String message, String secret) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmac.init(keySpec);
            byte[] result = hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new YonchainException("文件上传生成HMAC签名失败", e);
        }
    }
}
