package com.yonchain.ai.console.app.controller;

import com.yonchain.ai.api.app.Application;
import com.yonchain.ai.api.app.AppService;
import com.yonchain.ai.api.app.DefaultApplication;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.CurrentUser;
import com.yonchain.ai.api.sys.Role;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.app.request.AppCreateRequest;
import com.yonchain.ai.console.app.request.AppQueryRequest;
import com.yonchain.ai.console.app.request.AppUpdateRequest;
import com.yonchain.ai.console.app.response.AppResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用控制器
 *
 * @author Cgy
 * @since 2024-01-20
 */
@RestController
@RequestMapping("/apps")
@Tag(name = "应用管理", description = "应用相关接口")
public class AppController extends BaseController {

    @Autowired
    private AppService appService;

    /**
     * 根据ID获取应用详情
     *
     * @param id 应用ID
     * @return 应用详情响应
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取应用详情", description = "根据应用ID获取应用的详细信息")
    public AppResponse getAppById(@Parameter(description = "应用ID") @PathVariable String id) {
        Application app = appService.getAppById(id);
        if (app == null) {
            throw new YonchainResourceNotFoundException("APP_NOT_FOUND", "应用未找到");
        }
        return buildResponse(app);
    }


    /**
     * 分页查询应用列表
     *
     * @param request 查询请求参数
     * @return 分页应用列表
     */
    @GetMapping
    @Operation(summary = "分页查询应用列表", description = "根据查询条件分页获取应用列表")
    public PageResponse<AppResponse> pageApps(AppQueryRequest request) {
        CurrentUser currentUser = this.getCurrentUser();

        // 构建查询参数
        Map<String, Object> queryParam = new HashMap<>();
        //应用名称
        if (StringUtils.hasText(request.getName())) {
            queryParam.put("name", request.getName());
        }
        //应用类型
        if (StringUtils.hasText(request.getMode())) {
            queryParam.put("mode", request.getMode());
        }
        // 不是超级管理员只查所属角色应用
        if (!currentUser.isSuperAdmin()) {
            queryParam.put("roleIds", currentUser.getRoleIds());
        }

        //分页查询应用
        Page<Application> apps = appService.getAppsByPage(currentUser.getTenantId(), queryParam, request.getPage(), request.getLimit());

        return buildResponse(apps);
    }

    /**
     * 创建新应用
     *
     * @param request 创建应用请求
     * @return 创建的应用详情
     */
    @PostMapping
    @Operation(summary = "创建应用", description = "创建新的应用")
    public AppResponse createApp(@Valid @RequestBody AppCreateRequest request) {
        Application app = new DefaultApplication();
        app.setId(UUID.randomUUID().toString());
        app.setTenantId(this.getCurrentTenantId());

        //从请求获取数据填充
        app.setName(request.getName());
        app.setMode(request.getMode());
       // app.setProvider(request.getProvider());
        app.setApiKey(request.getApiKey());
        app.setBaseUrl(request.getBaseUrl());
        app.setIcon(request.getIcon());
        app.setIconBackground(request.getIconBackground());
        app.setDescription(request.getDescription());

        app.setCreatedBy(this.getCurrentUserId());
        app.setUpdatedBy(this.getCurrentUserId());

        //创建应用
        appService.createApp(app, request.getRoleIds());

        app = appService.getAppById(app.getId());
        return responseFactory.createAppResponse(app);
    }

    /**
     * 更新应用信息
     *
     * @param id      应用ID
     * @param request 更新请求
     * @return 更新后的应用详情
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新应用", description = "根据ID更新应用信息")
    public AppResponse updateApp(@Parameter(description = "应用ID") @PathVariable String id,
                                 @Valid @RequestBody AppUpdateRequest request) {
        // 检查应用是否存在
        Application app = appService.getAppById(id);
        if (app == null) {
            throw new YonchainResourceNotFoundException("APP_NOT_FOUND", "应用未找到");
        }

        //从请求获取数据填充
        app.setName(request.getName());
        app.setIcon(request.getIcon());
        app.setIconType(request.getIconType());
        app.setIconBackground(request.getIconBackground());
        app.setDescription(request.getDescription());
        app.setApiKey(request.getApiKey());
        app.setBaseUrl(request.getBaseUrl());

        app.setUpdatedBy(this.getCurrentUserId());

        //更新用户
        appService.updateApp(app, request.getRoleIds());

        return responseFactory.createAppResponse(appService.getAppById(id));
    }

    /**
     * 删除应用
     *
     * @param id 应用ID
     * @return 空响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用", description = "根据ID删除应用")
    public ApiResponse<Void> deleteAppById(@Parameter(description = "应用ID") @PathVariable String id) {
        appService.deleteAppById(id);
        return ApiResponse.success();
    }


    private AppResponse buildResponse(Application app) {
        AppResponse response = responseFactory.createAppResponse(app);
        List<Role> roles = appService.getAppRoles(app.getId());
        response.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
        return response;
    }

    private PageResponse<AppResponse> buildResponse(Page<Application> apps) {
        PageResponse<AppResponse> response = responseFactory.createAppPageResponse(apps);
        response.getData().forEach(appResponse -> {
            List<Role> roles = appService.getAppRoles(appResponse.getId());
            appResponse.setRoles(responseFactory.createRoleListResponse(roles).getData());
        });
        return response;
    }

}
