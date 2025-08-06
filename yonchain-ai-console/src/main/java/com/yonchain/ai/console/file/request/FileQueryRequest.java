package com.yonchain.ai.console.file.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文件查询请求对象
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class FileQueryRequest {

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量最小为1")
    private Integer limit = 10;

    private String storageType;

    private Boolean used;
}
