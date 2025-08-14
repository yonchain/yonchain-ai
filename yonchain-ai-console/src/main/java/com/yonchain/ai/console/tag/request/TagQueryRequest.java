package com.yonchain.ai.console.tag.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询标签请求
 */
@Data
@Schema(description = "查询标签请求")
@EqualsAndHashCode(callSuper = true)
public class TagQueryRequest extends PageRequest {

    /**
     * 标签类型
     */
    @Schema(description = "标签类型")
    @NotBlank(message = "标签类型不能为空")
    private String type;

}
