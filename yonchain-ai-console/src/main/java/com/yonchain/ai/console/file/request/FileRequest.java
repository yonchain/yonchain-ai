package com.yonchain.ai.console.file.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件请求对象
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class FileRequest {

    @NotBlank(message = "存储类型不能为空")
    private String storageType;

    @NotBlank(message = "文件key不能为空")
    private String key;

    @NotBlank(message = "文件名不能为空")
    private String name;

    @NotNull(message = "文件大小不能为空")
    private Integer size;

    @NotBlank(message = "文件扩展名不能为空")
    private String extension;

    private String mimeType;

    private Boolean used;

    private String usedBy;

    private LocalDateTime usedAt;

    private String hash;

    private String createdByRole;

    private String sourceUrl;
}
