package com.yonchain.ai.console.plugin.controller;

import com.yonchain.ai.api.plugin.PluginService;
import com.yonchain.ai.api.plugin.dto.PluginInfo;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.plugin.request.PluginQueryRequest;
import com.yonchain.ai.console.plugin.response.PluginPreviewResponse;
import com.yonchain.ai.console.plugin.response.PluginResponse;
import com.yonchain.ai.console.util.ValidationUtils;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import org.springframework.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件管理REST控制器
 * 统一的插件管理接口，支持租户感知
 * 
 * @author yonchain
 */
@Slf4j
@RestController
@RequestMapping("/plugins")
@RequiredArgsConstructor
@Tag(name = "插件管理", description = "插件安装、卸载、查询等管理接口")
public class PluginController extends BaseController {
    
    private final PluginService pluginService;
    
    // ==================== 插件查询接口 ====================
    
    @GetMapping
    @Operation(summary = "分页查询租户插件", description = "根据查询条件分页获取租户已安装的插件列表")
    public PageResponse<PluginResponse> getPlugins(PluginQueryRequest request) {
        log.debug("分页查询租户插件列表: {}", request);
        String tenantId = getCurrentTenantId();

        // 构建查询参数
        Map<String, Object> queryParam = new HashMap<>();
        
        // 插件名称
        if (StringUtils.hasText(request.getName())) {
            queryParam.put("name", request.getName());
        }
        
        // 插件状态
        if (StringUtils.hasText(request.getStatus())) {
            queryParam.put("status", request.getStatus());
        }
        
        // 插件类型
        if (StringUtils.hasText(request.getType())) {
            queryParam.put("type", request.getType());
        }

        // 分页查询插件
        Page<PluginInfo> plugins = pluginService.getPlugins(tenantId, queryParam, request.getPageNum(), request.getPageSize());

        return responseFactory.createPluginPageResponse(plugins, tenantId, pluginService);
    }
    
    @GetMapping("/{pluginId}")
    @Operation(summary = "获取插件详情", description = "根据插件ID获取插件详细信息，包含租户安装状态")
    public PluginResponse getPlugin(
            @Parameter(description = "插件ID") @PathVariable String pluginId) {
        log.debug("获取插件详情: {}", pluginId);
        ValidationUtils.validatePluginId(pluginId);
        
        String tenantId = getCurrentTenantId();
        PluginInfo pluginInfo = pluginService.getPlugin(pluginId);
        if (pluginInfo == null) {
            throw new IllegalArgumentException("插件不存在: " + pluginId);
        }
        
        Map<String, Object> tenantInstallation = pluginService.getTenantPluginInstallation(tenantId, pluginId);
        return responseFactory.createPluginResponse(pluginInfo, tenantInstallation);
    }

    // ==================== 插件管理接口 ====================
    
    @PostMapping("/preview")
    @Operation(summary = "预览插件", description = "上传插件文件预览插件信息，不进行安装")
    public PluginPreviewResponse previewPlugin(
            @Parameter(description = "插件文件") @RequestParam("file") MultipartFile file) {
        log.info("预览插件: {}", file.getOriginalFilename());
        ValidationUtils.validatePluginFile(file);
        
        try {
            Map<String, Object> descriptorMap = pluginService.previewPlugin(file.getInputStream(), file.getOriginalFilename());
            String tempIconUrl = (String) descriptorMap.get("iconUrl");

            return responseFactory.createPluginPreviewResponse(descriptorMap, tempIconUrl);
        } catch (Exception e) {
            log.error("预览插件失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("预览插件失败: " + e.getMessage(), e);
        }
    }
    
    @PostMapping("/install")
    @Operation(summary = "安装插件", description = "通过上传文件安装插件")
    public ApiResponse<Void> installPlugin(
            @Parameter(description = "插件文件") @RequestParam("file") MultipartFile file) {
        log.info("安装插件: {}", file.getOriginalFilename());
        ValidationUtils.validatePluginFile(file);
        
        try {
            String tenantId = getCurrentTenantId();
            pluginService.installPluginForTenant(tenantId, file.getInputStream(), file.getOriginalFilename());
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("安装插件失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("安装插件失败: " + e.getMessage(), e);
        }
    }

    
    @PostMapping("/install-url")
    @Operation(summary = "通过URL安装插件", description = "通过URL下载并安装插件")
    public ApiResponse<String> installPluginByUrl(
            @RequestBody Map<String, String> request) {
            String url = request.get("url");
        log.info("通过URL安装插件: {}", url);
        ValidationUtils.validateUrl(url);
        
        try {
            String tenantId = getCurrentTenantId();
            pluginService.installPluginByUrlForTenant(tenantId, url);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("通过URL安装插件失败: {}", url, e);
            throw new RuntimeException("通过URL安装插件失败: " + e.getMessage(), e);
        }
    }
    
    @PostMapping("/install-marketplace")
    @Operation(summary = "通过插件市场安装插件", description = "通过插件市场ID安装插件（预留接口）")
    public ApiResponse<Void> installPluginFromMarketplace(
            @RequestBody Map<String, String> request) {
            String marketplaceId = request.get("marketplace_id");
        log.info("通过插件市场安装插件: {}", marketplaceId);
        ValidationUtils.validateMarketplaceId(marketplaceId);
        
        try {
            String tenantId = getCurrentTenantId();
            pluginService.installPluginFromMarketplaceForTenant(tenantId, marketplaceId);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("通过插件市场安装插件失败: {}", marketplaceId, e);
            throw new RuntimeException("通过插件市场安装插件失败: " + e.getMessage(), e);
        }
    }
    
    @DeleteMapping("/{pluginId}/uninstall")
    @Operation(summary = "卸载插件", description = "卸载指定的插件")
    public ApiResponse<Void> uninstallPlugin(
            @Parameter(description = "插件ID") @PathVariable String pluginId) {
        log.info("卸载插件: {}", pluginId);
        ValidationUtils.validatePluginId(pluginId);
        
        pluginService.uninstallPlugin(pluginId);
        return ApiResponse.success();
    }
    
    @PostMapping("/{pluginId}/enable")
    @Operation(summary = "启用插件", description = "启用指定的插件")
    public ApiResponse<Void> enablePlugin(
            @Parameter(description = "插件ID") @PathVariable String pluginId) {
        log.info("启用插件: {}", pluginId);
        ValidationUtils.validatePluginId(pluginId);
        
        pluginService.enablePlugin(pluginId);
        return ApiResponse.success();
    }
    
    @PostMapping("/{pluginId}/disable")
    @Operation(summary = "禁用插件", description = "禁用指定的插件")
    public ApiResponse<Void> disablePlugin(
            @Parameter(description = "插件ID") @PathVariable String pluginId) {
        log.info("禁用插件: {}", pluginId);
        ValidationUtils.validatePluginId(pluginId);
        
        pluginService.disablePlugin(pluginId);
        return ApiResponse.success();
    }

    
    // ==================== 插件图标接口 ====================
    
    @GetMapping("/{pluginId}/icon")
    @Operation(summary = "获取插件图标", description = "获取指定插件的图标文件")
    public ResponseEntity<byte[]> getPluginIcon(
            @Parameter(description = "插件ID") @PathVariable String pluginId) {
        log.debug("获取插件图标: {}", pluginId);
        ValidationUtils.validatePluginId(pluginId);
        
        Map<String, Object> iconData = pluginService.getPluginIcon(pluginId);
        if (iconData == null) {
            return ResponseEntity.notFound().build();
        }

        return buildIconResponse(iconData);
    }
    
    @GetMapping("/{pluginId}/icon-url")
    @Operation(summary = "获取插件图标URL", description = "获取指定插件的图标访问URL")
    public ApiResponse<Map<String, Object>> getPluginIconUrl(
            @Parameter(description = "插件ID") @PathVariable String pluginId) {
        log.debug("获取插件图标URL: {}", pluginId);
        ValidationUtils.validatePluginId(pluginId);
        
        String iconUrl = pluginService.getPluginIconUrl(pluginId);
        String message = iconUrl != null ? "获取图标URL成功" : "插件无图标";
        return ApiResponse.success(message,null);
    }
    
    @GetMapping("/temp-icons/{iconId}")
    @Operation(summary = "获取临时图标", description = "获取临时图标文件（用于插件预览）")
    public ResponseEntity<byte[]> getTempIcon(
            @Parameter(description = "临时图标ID") @PathVariable String iconId) {
        log.debug("获取临时图标: {}", iconId);
        
        Map<String, Object> iconData = pluginService.getTempIcon(iconId);
        if (iconData == null) {
            return ResponseEntity.notFound().build();
        }
        
        return buildIconResponse(iconData);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 构建图标响应
     */
    private ResponseEntity<byte[]> buildIconResponse(Map<String, Object> iconData) {
        byte[] data = (byte[]) iconData.get("data");
        String contentType = (String) iconData.get("contentType");
        String fileName = (String) iconData.get("fileName");
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(data);
    }

}