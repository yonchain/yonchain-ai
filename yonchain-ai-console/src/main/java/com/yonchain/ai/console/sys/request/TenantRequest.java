package com.yonchain.ai.console.sys.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建租户请求
 */
@Data
@Schema(description = "租户请求")
public class TenantRequest {

    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    @Schema(description = "租户名称", required = true)
    private String name;

    /*    *//**
     * 加密公钥
     *//*
    @Schema(description = "加密公钥")
    private String encryptPublicKey;*/

/*    *//**
     * 计划类型
     *//*
    @Schema(description = "计划类型", example = "basic")
    private String plan;*/

    /*    *//**
     * 状态
     *//*
    @Schema(description = "状态", example = "normal")
    private String status;*/

    /*    *//**
     * 自定义配置
     *//*
    @Schema(description = "自定义配置")
    private String customConfig;*/

   /* *//**
     * 将请求对象转换为租户实体
     *
     * @return 转换后的租户实体对象
     *//*
    public Tenant getTenant() {
        Tenant tenant = new DefaultTenant();
        tenant.setName(name);
        tenant.setPlan(plan);
        tenant.setStatus(status);
        tenant.setCustomConfig(customConfig);
        return tenant;
    }*/
}
