package com.yonchain.ai.api.storage;

/**
 * 存储类型枚举
 */
public enum StorageType {
    MINIO("MinIO对象存储"),
    S3("AWS S3存储"),
    OSS("阿里云OSS"),
    COS("腾讯云COS");

    private final String description;

    StorageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
