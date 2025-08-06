package com.yonchain.ai.console.idm.request;

/**
 * 角色请求对象
 * 用于封装角色相关的请求参数
 *
 * @author Cgy
 * @since 1.0.0
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建角色请求
 */
@Schema(description = "创建角色请求")
public class RoleRequest {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 255, message = "角色名称长度不能超过255个字符")
    @Schema(description = "角色名称", required = true, example = "管理员")
    private String name;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 255, message = "角色编码长度不能超过255个字符")
    @Schema(description = "角色编码", required = true, example = "ADMIN")
    private String code;

    /**
     * 角色组id
     */
    @Schema(description = "角色组id")
    @NotBlank(message = "角色组id不能为空")
    private String groupId;

    /**
     * 状态
     */
    @Size(max = 255, message = "描述不能超过255个字符")
    @Schema(description = "描述")
    private String description;

    /**
     * 是否设置了角色名称
     */
    @JsonIgnore
    private Boolean nameSet = false;

    /**
     * 是否设置了角色编码
     */
    @JsonIgnore
    private Boolean codeSet = false;

    /**
     * 是否设置了角色组id
     */
    @JsonIgnore
    private Boolean groupIdSet = false;

    /**
     * 是否设置了描述
     */
    @JsonIgnore
    private Boolean descriptionSet = false;

    // Getter methods
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isNameSet() {
        return nameSet;
    }

    public Boolean isCodeSet() {
        return codeSet;
    }

    public Boolean isGroupIdSet() {
        return groupIdSet;
    }

    public Boolean isDescriptionSet() {
        return descriptionSet;
    }

    public void setName(String name) {
        this.name = name;
        this.nameSet = true;
    }

    public void setCode(String code) {
        this.code = code;
        this.codeSet = true;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
        this.groupIdSet = true;
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionSet = true;
    }
}
