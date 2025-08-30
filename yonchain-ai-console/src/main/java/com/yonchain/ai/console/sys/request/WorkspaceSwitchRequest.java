package com.yonchain.ai.console.sys.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 工作空间切换请求
 */
@Schema(description = "工作空间切换请求")
public class WorkspaceSwitchRequest {

    /**
     * 租户id
     */
    @NotBlank(message = "租户id不能为空")
    @Schema(description = "租户id", required = true)
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
