package com.yonchain.ai.console.sys.controller;


import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainForbiddenException;
import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.sys.request.TenantQueryRequest;
import com.yonchain.ai.console.sys.request.TenantRequest;
import com.yonchain.ai.console.sys.request.WorkspaceSwitchRequest;
import com.yonchain.ai.console.sys.response.TenantResponse;
import com.yonchain.ai.util.Assert;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 租户(工作空间)管理控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@Tag(name = "租户(工作空间)管理", description = "租户相关接口")
@RestController
@RequestMapping("/workspaces")
public class WorkspaceController extends BaseController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserService userService;

    /**
     * 获取租户详情
     *
     * @param id 租户ID
     * @return 租户响应
     */
    @Operation(summary = "获取租户")
    @GetMapping("/{id}")
    public TenantResponse getTenant(@Parameter(description = "租户ID") @PathVariable String id) {
        Tenant tenant = getTenantFromRequest(id);
        return this.responseFactory.createTenantResponse(tenant);
    }

/*    *//**
     * 获取当前用户所属租户列表
     *
     * @return 租户列表
     *//*
    @Operation(summary = "查询当前用户所属租户列表")
    @GetMapping
    public ListResponse<TenantResponse> getTenants() {
        String userId = this.getCurrentUserId();
        List<Tenant> tenant = tenantService.getTenantsByUserId(userId);
        return responseFactory.createTenantListResponse(tenant);
    }*/

/*    *//**
     * 分页查询租户(工作空间)列表
     *
     * @param request 租户查询请求对象
     * @return 分页响应对象
     *//*
    @Operation(summary = "分页查询租户")
    @GetMapping
    public PageResponse<TenantResponse> pageTenants(TenantQueryRequest request) {
        String userId = this.getCurrentUserId();

        //查询参数
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("keyword", request.getKeyword());

        //获取当前用户所属租户数据列表
        Page<Tenant> tenantPage = tenantService.pageTenants(userId, queryParam, request.getPageNum(), request.getPageSize());

        return responseFactory.createTenantPageResponse(tenantPage);
    }*/

    /**
     * 查询租户(工作空间)列表
     *
     * @param request 租户查询请求对象
     * @return 分页响应对象
     */
    @Operation(summary = "查询租户(工作空间)列表")
    @GetMapping
    public ListResponse<TenantResponse> getTenants(TenantQueryRequest request) {
        String userId = this.getCurrentUserId();

        //查询参数
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("keyword", request.getKeyword());

        //获取当前用户所属租户数据列表
        List<Tenant> tenantPage = tenantService.getTenants(userId, queryParam);

        return responseFactory.createTenantListResponse(tenantPage);
    }

    /**
     * 查询当前用户的当前租户
     *
     * @return 租户响应
     */
    @Operation(summary = "查询当前用户的当前租户")
    @GetMapping("/current")
    public TenantResponse getCurrentTenant() {
        String userId = this.getCurrentUserId();
        Tenant tenant = tenantService.getCurrentTenantByUserId(userId);
        return responseFactory.createTenantResponse(tenant);
    }


    /**
     * 创建新租户(工作空间)
     *
     * @param request 租户创建请求对象
     * @return 包含新创建租户详情的响应对象
     */
    @Operation(summary = "创建租户")
    @PostMapping
    public TenantResponse createTenant(@Valid @RequestBody TenantRequest request) {
        Tenant tenant = new DefaultTenant();
        tenant.setId(UUID.randomUUID().toString());

        //从请求获取数据填充
        this.populateTenantFromRequest(tenant, request);

        // 创建租户
        tenantService.createTenant(tenant, this.getCurrentUserId());

        tenant = tenantService.getTenantById(tenant.getId());
        return responseFactory.createTenantResponse(tenant);
    }

    /**
     * 更新租户(工作空间)信息
     *
     * @param id      要更新的租户ID
     * @param request 租户更新请求对象
     * @return 空响应，仅包含操作状态码和消息
     */
    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateTenant(@Parameter(description = "租户ID") @PathVariable String id,
                                          @Valid @RequestBody TenantRequest request) {
        Tenant tenant = getTenantFromRequest(id);

        //从请求获取数据填充
        this.populateTenantFromRequest(tenant, request);

        // 更新租户
        tenantService.updateTenant(tenant);

        return ApiResponse.success();
    }

    /**
     * 切换租户(工作空间)
     *
     * @param request 请求
     * @return 租户响应
     */
    @Operation(summary = "切换租户")
    @PostMapping("/switch")
    public ApiResponse<Void> switchTenant(@RequestBody WorkspaceSwitchRequest request) {
        Tenant tenant = getTenantFromRequest(request.getTenantId());
        tenantService.switchTenant(tenant.getId(), this.getCurrentUserId());
        return ApiResponse.success();
    }

    /**
     * 删除指定租户(工作空间)
     *
     * @param id 要删除的租户ID，通过URL路径传递
     * @return 空响应，仅包含操作状态码和消息
     */
    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTenant(@Parameter(description = "租户ID") @PathVariable String id) {
        CurrentUser currentUser = this.getCurrentUser();
        //不是超级管理员，禁止删除
        if (!currentUser.isSuperAdmin()){
            throw new YonchainForbiddenException("删除权限不足");
        }
        //如果是当前租户，不允许删除
        if (currentUser.getTenantId().equals(id)){
            throw new YonchainForbiddenException("当前租户不允许删除");
        }

        //检查租户下是否有用户，如果有，不允许删除,如果只有自己，可删除
        List<User> users = userService.getUsers(id,new HashMap<>());
        if (users.size() > 1){
            throw new YonchainIllegalStateException("租户下有用户，不允许删除");
        }
        if (users.size() == 1 && !users.get(0).getId().equals(currentUser.getUserId())){
            throw new YonchainIllegalStateException("租户下有用户，不允许删除");
        }

        Tenant tenant = getTenantFromRequest(id);

        tenantService.deleteTenantById(tenant.getId());
        return ApiResponse.success();
    }


    /**
     * 根据租户ID获取租户信息
     *
     * @param id 租户ID，不能为空或空白
     * @return 租户实体对象，包含完整的租户信息
     */
    private Tenant getTenantFromRequest(String id) {
        Tenant tenant = tenantService.getTenantById(id);
        if (tenant == null) {
            throw new YonchainResourceNotFoundException("租户不存在");
        }
        return tenant;
    }

    /**
     * 从请求中填充租户信息，采用选择性更新策略
     *
     * @param tenant  租户
     * @param request 租户请求对象
     */
    private void populateTenantFromRequest(Tenant tenant, TenantRequest request) {
        // 更新租户名称（必填字段）
        // 租户名称用于标识和显示租户，支持中文、英文、数字等
        if (StringUtils.isNotBlank(request.getName())) {
            if (request.getName().length() < 2 || request.getName().length() > 50) {
                throw new YonchainIllegalStateException("租户名称长度必须在2-50个字符之间");
            }
            tenant.setName(request.getName());
        } else {
            throw new YonchainResourceNotFoundException("租户名称不能为空");
        }

        // 更新计划类型（可选字段）
        // 租户的订阅计划类型，如：basic（基础版）、pro（专业版）、enterprise（企业版）等
        if (StringUtils.isNotBlank(request.getPlan())) {
            tenant.setPlan(request.getPlan());
        }

      /*  // 更新租户状态（可选字段）
        // 租户的当前状态，如：normal（正常）、suspended（已暂停）、deleted（已删除）等
        if (StringUtils.isNotBlank(request.getStatus())) {
            tenant.setStatus(request.getStatus());
        }

        // 更新自定义配置（可选字段）
        // 租户的自定义配置信息，通常以JSON格式存储，包含租户特定的配置项
        if (StringUtils.isNotBlank(request.getCustomConfig())) {
            tenant.setCustomConfig(request.getCustomConfig());
        }*/
    }
}
