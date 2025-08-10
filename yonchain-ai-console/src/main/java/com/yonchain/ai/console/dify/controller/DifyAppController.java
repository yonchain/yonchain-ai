package com.yonchain.ai.console.dify.controller;

import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.dify.DifyAppService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.dify.DifyResponseFactory;
import com.yonchain.ai.console.dify.response.DifyAppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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



    @Operation(summary = "获取Dify应用", description = "根据API密钥和基础URL获取Dify应用信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取应用信息", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = DifyAppResponse.class))),
        @ApiResponse(responseCode = "400", description = "无效的请求参数"),
        @ApiResponse(responseCode = "404", description = "应用未找到")
    })
    @RequestMapping
    public DifyAppResponse getAppByApiKey(
            @Parameter(description = "Dify API密钥", required = true) @RequestParam("apiKey") String apiKey,
            @Parameter(description = "Dify基础URL", required = true) @RequestParam("baseUrl") String baseUrl) {
        DifyApp difyApp = difyAppService.getAppByApiKey(apiKey, baseUrl);
        return difyResponseFactory.createAppResponse(difyApp);
    }


}
