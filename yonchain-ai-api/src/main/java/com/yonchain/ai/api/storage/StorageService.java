package com.yonchain.ai.api.storage;

import java.io.InputStream;
import java.util.Map;

/**
 * 对象存储服务接口
 */
public interface StorageService {

    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 文件流
     * @param size 文件大小
     * @param contentType 文件类型
     * @param metadata 元数据
     * @return 文件访问URL
     */
    String uploadFile(String bucketName, String objectName, InputStream inputStream,
                     long size, String contentType, Map<String, String> metadata);

    /**
     * 下载文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件流
     */
    InputStream downloadFile(String bucketName, String objectName);

    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    void deleteFile(String bucketName, String objectName);

    /**
     * 获取文件访问URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 过期时间(秒)
     * @return 临时访问URL
     */
    String getFileUrl(String bucketName, String objectName, int expires);

    /**
     * 获取存储类型
     * @return 存储类型
     */
    String getStorageType();


    String uploadFile(String objectName, byte[] content);

    InputStream downloadFile(String key);
}
