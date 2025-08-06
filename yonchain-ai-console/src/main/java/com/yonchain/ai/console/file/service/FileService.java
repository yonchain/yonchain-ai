package com.yonchain.ai.console.file.service;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.console.file.entity.FileEntity;

import java.util.List;
import java.util.Map;

/**
 * 文件服务接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface FileService {

    /**
     * 根据ID获取文件
     *
     * @param id 文件ID
     * @return 文件对象
     */
    FileEntity getFileById(String id);

    /**
     * 分页查询文件
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param page       页码
     * @param limit      每页数量
     * @return 分页结果
     */
    Page<FileEntity> pageFiles(String tenantId, Map<String, Object> queryParam, int page, int limit);

    /**
     * 根据ID删除文件
     *
     * @param id 文件ID
     */
    void deleteFileById(String id);

    /**
     * 批量删除文件
     *
     * @param ids 文件ID列表
     */
    void deleteFileByIds(List<String> ids);

    /**
     * 上传文件
     *
     * @param filename      文件名
     * @param content       文件内容
     * @param contentType   文件类型
     * @param currentUserId 当前用户id
     * @param tenantId      租户id
     * @return 上传的文件对象
     */
    FileEntity uploadFile(String filename, byte[] content, String contentType, String currentUserId, String tenantId);

    /**
     * 验证签名URL的有效性
     *
     * @param fileId    文件ID
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param sign      签名
     * @return 验证结果 true-有效 false-无效
     */
    boolean validateSignedUrl(String fileId, long timestamp, String nonce,String sign);

    /**
     * 生成带签名的文件URL
     *
     * @param fileId 文件ID
     * @return 签名URL
     */
    String getSignedFileUrl(String fileId);
}
