package com.yonchain.ai.console.file.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件响应对象
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class FileResponse {

    private String id;
    private String tenantId;
    private String storageType;
    private String key;
    private String name;
    private int size;
    private String extension;
    private String mimeType;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean used;
    private String usedBy;
    private LocalDateTime usedAt;
    private String hash;
    private String createdByRole;
    private String sourceUrl;

}
