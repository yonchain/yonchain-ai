package com.yonchain.ai.console.idm.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询菜单请求
 */
@Data
@Schema(description = "查询菜单请求")
@EqualsAndHashCode(callSuper = true)
public class MenuQueryRequest extends PageRequest {

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型")
    private String menuType;
}
