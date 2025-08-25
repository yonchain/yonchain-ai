package com.yonchain.ai.console.dify.controller;


import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.dify.DefaultDifyApp;
import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.dify.DifyService;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.sys.CurrentUser;
import com.yonchain.ai.api.sys.Role;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.agent.request.AppCreateRequest;
import com.yonchain.ai.console.agent.request.AppQueryRequest;
import com.yonchain.ai.console.agent.request.AppUpdateRequest;
import com.yonchain.ai.console.dify.response.DifyAppResponse;
import com.yonchain.ai.console.tag.response.TagResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * dify 应用控制器
 *
 * @author Cgy
 */
@RestController
@RequestMapping("/dify/apps")
@Tag(name = "Dify应用", description = "Dify应用相关接口")
public class DifyAppController extends BaseController {

    @Autowired
    private DifyService difyAppService;

    /**
     * 根据ID获取应用详情
     *
     * @param id 应用ID
     * @return 应用详情响应
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取应用详情", description = "根据应用ID获取应用的详细信息")
    public DifyAppResponse getAppById(@Parameter(description = "应用ID") @PathVariable String id) {
        DifyApp app = difyAppService.getAppById(id);
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
    public PageResponse<DifyAppResponse> pageApps(AppQueryRequest request) {
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
        Page<DifyApp> apps = difyAppService.getAppsByPage(currentUser.getTenantId(), queryParam, request.getPageNum(), request.getPageSize());

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
    public DifyAppResponse createApp(@Valid @RequestBody AppCreateRequest request) {
        DifyApp app = new DefaultDifyApp();
        app.setId(UUID.randomUUID().toString());
        app.setTenantId(this.getCurrentTenantId());

        //从请求获取数据填充
        app.setName(request.getName());
/*        app.setMode(request.getMode());
        // app.setProvider(request.getProvider());
        app.setApiKey(request.getApiKey());
        app.setBaseUrl(request.getBaseUrl());*/
        app.setIcon(request.getIcon());
        app.setIconBackground(request.getIconBackground());
        app.setDescription(request.getDescription());

        app.setCreatedBy(this.getCurrentUserId());
        app.setUpdatedBy(this.getCurrentUserId());

        //创建应用
        difyAppService.createApp(app, request.getRoleIds());

        app = difyAppService.getAppById(app.getId());
        return responseFactory.createDifyAppResponse(app);
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
    public DifyAppResponse updateApp(@Parameter(description = "应用ID") @PathVariable String id,
                                 @Valid @RequestBody AppUpdateRequest request) {
        // 检查应用是否存在
        DifyApp app = difyAppService.getAppById(id);
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
        difyAppService.updateApp(app, request.getRoleIds());

        return responseFactory.createDifyAppResponse(difyAppService.getAppById(id));
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
        difyAppService.deleteAppById(id);
        return ApiResponse.success();
    }


    private DifyAppResponse buildResponse(DifyApp app) {
        DifyAppResponse response = responseFactory.createDifyAppResponse(app);
        List<Role> roles = difyAppService.getAppRoles(app.getId());
        response.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
        return response;
    }

    private PageResponse<DifyAppResponse> buildResponse(Page<DifyApp> apps) {
        PageResponse<DifyAppResponse> response = responseFactory.createDifyAppPageResponse(apps);
        response.getData().forEach(appResponse -> {
            List<Role> roles = difyAppService.getAppRoles(appResponse.getId());
            appResponse.setRoles(responseFactory.createRoleListResponse(roles).getData());

            List<TagResponse> tags = new ArrayList<>();
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName("金融");
            tagResponse.setId(UUID.randomUUID().toString());
            tags.add(tagResponse);
            TagResponse tagResponse2 = new TagResponse();
            tagResponse2.setName("人工智能");
            tagResponse2.setId(UUID.randomUUID().toString());
            tags.add(tagResponse2);
            appResponse.setTags(tags);
        });
        return response;
    }

    /**
     * 获取Dify应用
     *
     * @param apiKey  API密钥
     * @param baseUrl 基础URL
     * @return Dify应用响应
     */
    @Operation(summary = "获取Dify应用", description = "根据API密钥和基础URL获取Dify应用信息")
    @RequestMapping
    public DifyAppResponse getAppByApiKey(
            @Parameter(description = "API密钥", required = true) @RequestParam("api_key") String apiKey,
            @Parameter(description = "基础URL", required = true) @RequestParam("base_url") String baseUrl) {
        DifyApp difyApp = difyAppService.getAppByApiKey(apiKey, baseUrl);
        return responseFactory.createDifyAppResponse(difyApp);
    }


    /**
     * 获取应用参数
     *
     * @param apiKey  API密钥
     * @param baseUrl 基础URL
     * @return 应用参数
     */
    @Operation(summary = "获取应用参数", description = "根据API密钥和基础URL获取Dify应用参数")
    @RequestMapping("/parameters")
    public Map<String, Object> getAppParameters(
            @Parameter(description = "Dify API密钥", required = true) @RequestParam("apiKey") String apiKey,
            @Parameter(description = "Dify基础URL", required = true) @RequestParam("baseUrl") String baseUrl) {
        Map<String, Object> result = difyAppService.getAppParameters(apiKey, baseUrl);
        return result;
    }


}
