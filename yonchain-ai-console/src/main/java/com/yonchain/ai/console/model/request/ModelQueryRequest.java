package com.yonchain.ai.console.model.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型查询请求参数
 */
@Data
@Schema(description = "模型查询请求参数")
public class ModelQueryRequest  {

    /**
     * 模型类型
     */
    @Schema(description = "模型类型")
    private String modelType;

    /**
     * 提供商名称
     */
    @Schema(description = "模型提供商")
    private String provider;

}
