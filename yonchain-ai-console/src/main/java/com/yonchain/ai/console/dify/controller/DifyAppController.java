package com.yonchain.ai.console.dify.controller;

import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.dify.DifyAppService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.dify.DifyResponseFactory;
import com.yonchain.ai.console.dify.response.DifyAppResponse;
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
public class DifyAppController extends BaseController {

    @Autowired
    private DifyAppService difyAppService;

    @Autowired
    private DifyResponseFactory difyResponseFactory;

    @RequestMapping
    public DifyAppResponse getAppByApiKey(@RequestParam("apiKey") String apiKey,
                                          @RequestParam("baseUrl") String baseUrl) {
        DifyApp difyApp = difyAppService.getAppByApiKey(apiKey, baseUrl);
        return difyResponseFactory.createAppResponse(difyApp);
    }


}
