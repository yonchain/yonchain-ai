package com.yonchain.ai.console.file.request;

import com.yonchain.ai.web.request.PageRequest;
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
public class FileQueryRequest extends PageRequest {

    private String storageType;

    private Boolean used;
}
