package com.yonchain.ai.console.app.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用查询请求参数
 * 继承分页参数，包含应用筛选条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用查询请求")
public class AppQueryRequest extends PageRequest {

    /**
     * 应用名称
     * 模糊匹配查询
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模式
     * 精确匹配查询
     * 可选值：chat, completion, workflow等
     */
    @Schema(description = "应用模式")
    private String mode;

}
