package com.yonchain.ai.console.dify.controller;

import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.dify.DifyAppService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.dify.DifyResponseFactory;
import com.yonchain.ai.console.dify.response.DifyAppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    private DifyAppService difyAppService;

    @Autowired
    private DifyResponseFactory difyResponseFactory;


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
            @Parameter(description = "API密钥", required = true) @RequestParam("apiKey") String apiKey,
            @Parameter(description = "基础URL", required = true) @RequestParam("baseUrl") String baseUrl) {
        DifyApp difyApp = difyAppService.getAppByApiKey(apiKey, baseUrl);
        return difyResponseFactory.createAppResponse(difyApp);
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
