package com.yonchain.ai.console.sys.request;

import com.yonchain.ai.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询租户请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询租户请求")
public class TenantQueryRequest extends PageRequest {

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String name;

    /**
     * 计划类型
     */
    @Schema(description = "计划类型", example = "basic")
    private String plan;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "normal")
    private String status;

/*    *//**
     * 获取租户
     *
     * @return 租户
     *//*
    public Tenant getTenant() {
        Tenant tenant = new DefaultTenant();
        tenant.setName(this.getName());
        tenant.setPlan(this.getPlan());
        tenant.setStatus(this.getStatus());
        return tenant;
    }*/
}
