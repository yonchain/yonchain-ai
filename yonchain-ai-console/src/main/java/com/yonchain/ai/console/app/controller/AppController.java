package com.yonchain.ai.console.app.controller;

import com.yonchain.ai.api.app.Application;
import com.yonchain.ai.api.app.AppService;
import com.yonchain.ai.api.app.DefaultApplication;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.idm.CurrentUser;
import com.yonchain.ai.api.idm.Role;
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
        app.setIcon(request.getIcon());
        app.setIconBackground(request.getIconBackground());
        app.setDescription(request.getDescription());

        app.setCreatedBy(this.getCurrentUserId());
        app.setUpdatedBy(this.getCurrentUserId());

        //从请求获取数据填充
        //populateAppFromRequest(app, request);

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
        app.setUseIconAsAnswerIcon(request.getUseIconAsAnswerIcon());

        app.setUpdatedBy(this.getCurrentUserId());

        //从请求获取数据填充
        // populateAppFromRequest(app, request);

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


    /**
     * 从请求对象填充应用数据
     * <p>
     * 该方法将AppRequest对象中的非空字段值设置到App对象中，
     * 用于应用创建和更新时的数据填充。
     * </p>
     *//*

    private void populateAppFromRequest(App app, AppCreateRequest request) {
        // 基本信息
        if (StringUtils.isNotBlank(request.getName())) {
            // 设置应用名称，用于前端展示和系统标识
            app.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getMode())) {
            // 设置应用模式，决定应用的基本行为和工作流程
            app.setMode(request.getMode());
        }
        if (StringUtils.isNotBlank(request.getIcon())) {
            // 设置应用图标URL，用于前端展示
            app.setIcon(request.getIcon());
        }
        if (StringUtils.isNotBlank(request.getIconBackground())) {
            // 设置图标背景色，格式为十六进制颜色代码
            app.setIconBackground(request.getIconBackground());
        }
        if (StringUtils.isNotBlank(request.getDescription())) {
            // 设置应用描述，用于说明应用的功能和用途
            app.setDescription(request.getDescription());
        }

        // 状态设置
        if (request.getEnableSite() != null) {
            // 启用/禁用应用站点，true表示用户可以访问应用的前端页面
            app.setEnableSite(request.getEnableSite());
        }
        if (request.getEnableApi() != null) {
            // 启用/禁用应用API，true表示可以通过API访问应用功能
            app.setEnableApi(request.getEnableApi());
        }
        if (request.getIsDemo() != null) {
            // 标记是否为演示应用，演示应用可能有功能限制
            app.setIsDemo(request.getIsDemo());
        }
        if (request.getIsPublic() != null) {
            // 设置应用是否公开，公开应用可以被所有用户访问
            app.setIsPublic(request.getIsPublic());
        }
        if (request.getUseIconAsAnswerIcon() != null) {
            // 是否使用应用图标作为回答消息的图标
            app.setUseIconAsAnswerIcon(request.getUseIconAsAnswerIcon());
        }

        // API限制设置
        if (request.getApiRpm() != null) {
            // 设置API每分钟最大请求数，用于限流
            app.setApiRpm(request.getApiRpm());
        }
        if (request.getApiRph() != null) {
            // 设置API每小时最大请求数，用于限流
            app.setApiRph(request.getApiRph());
        }
        if (request.getMaxActiveRequests() != null) {
            // 设置最大并发请求数，防止系统过载
            app.setMaxActiveRequests(request.getMaxActiveRequests());
        }

        // 工作流和模型配置
        if (StringUtils.isNotBlank(request.getWorkflowId())) {
            // 设置关联的工作流ID，决定应用的处理流程
            app.setWorkflowId(request.getWorkflowId());
        }
        if (StringUtils.isNotBlank(request.getAppModelConfigId())) {
            // 设置应用模型配置ID，决定使用的AI模型和参数
            app.setAppModelConfigId(request.getAppModelConfigId());
        }

        // 追踪配置
        if (StringUtils.isNotBlank(request.getTracing())) {
            // 设置追踪配置，用于日志记录和调试
            app.setTracing(request.getTracing());
        }
    }
*/
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
