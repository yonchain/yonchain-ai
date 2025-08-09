package com.yonchain.ai.console.sys.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
@Schema(description = "用户分页查询请求")
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest {

    /**
     * 账户名称
     */
    @Schema(description = "账户名称")
    private String name;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
}
