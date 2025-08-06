package com.yonchain.ai.console.file.service;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.storage.StorageService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class StorageServiceImpl implements StorageService {

    @Override
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType, Map<String, String> metadata) {
        try {
            // 创建本地存储目录
            Path storagePath = Paths.get("storage", bucketName);
            Files.createDirectories(storagePath);

            // 创建目标文件路径
            Path filePath = storagePath.resolve(objectName);

            // 将输入流写入文件
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回文件路径
            return filePath.toString();
        } catch (IOException e) {
            throw new YonchainException("文件上传失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectName) {
        return null;
    }

    @Override
    public void deleteFile(String bucketName, String objectName) {

    }

    @Override
    public String getFileUrl(String bucketName, String objectName, int expires) {
        return "";
    }

    @Override
    public String getStorageType() {
        return "";
    }

    @Override
    public String uploadFile(String objectName, byte[] content) {
        try {
            // 根据操作系统设置存储路径
            Path storagePath;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows系统存储在C盘上传目录
                storagePath = Paths.get("C:");
            } else {
                // 其他系统存储在项目目录下
                storagePath = Paths.get("storage", "default");
            }
            
            // 创建存储目录
            Files.createDirectories(storagePath);

            // 创建目标文件路径
            Path filePath = storagePath.resolve(objectName);

            // 确保文件所在的所有父目录都存在
            Path parentDir = filePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            // 将字节数组写入文件
            Files.write(filePath, content);

            // 返回文件路径
            return filePath.toString();
        } catch (IOException e) {
            throw new YonchainException("文件上传失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            // 根据操作系统设置存储路径
            Path storagePath;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows系统存储在C盘上传目录
                storagePath = Paths.get("C:");
            } else {
                // 其他系统存储在项目目录下
                storagePath = Paths.get("storage", "default");
            }
            
            // 构造完整文件路径
            Path filePath = storagePath.resolve(key);
            
            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                throw new YonchainResourceNotFoundException("文件不存在: " + filePath);
            }
            
            // 创建并返回文件输入流
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new YonchainException("文件下载失败: " + key, e);
        }
    }
}
