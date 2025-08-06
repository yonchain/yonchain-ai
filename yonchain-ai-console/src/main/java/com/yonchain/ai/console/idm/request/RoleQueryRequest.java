package com.yonchain.ai.console.idm.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询请求")
public class RoleQueryRequest extends PageRequest {

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    /**
     * 角色组id
     */
    @Schema(description = "角色组id")
    private String groupId;

}
