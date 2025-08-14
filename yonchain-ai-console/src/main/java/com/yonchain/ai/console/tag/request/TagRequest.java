package com.yonchain.ai.console.tag.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 标签请求
 */
@Data
@Schema(description = "标签请求")
public class TagRequest {

    /**
     * 标签类型
     */
    @NotBlank(message = "标签类型不能为空")
    @Size(max = 16, message = "标签类型长度不能超过16个字符")
    private String type;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 255, message = "标签名称长度不能超过255个字符")
    private String name;

}
