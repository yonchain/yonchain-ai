package com.yonchain.ai.console.idm.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色响应
 */
@Data
@Schema(description = "角色响应")
public class RoleResponse {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID")
    private String id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码", example = "ADMIN")
    private String code;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "normal")
    private String status;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private String createdBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    /**
     * 更新者ID
     */
    @Schema(description = "更新者ID")
    private String updatedBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    /**
     * 角色组id
     */
    @Schema(description = "角色组id")
    private String groupId;

    /**
     * 角色组名称
     */
    @Schema(description = "角色组名称")
    private String groupName;

    /**
     * 角色类别(0-系统角色，1-业务角色)
     */
    @Schema(description = "角色类别(0-系统角色，1-业务角色)")
    private String category;
}
