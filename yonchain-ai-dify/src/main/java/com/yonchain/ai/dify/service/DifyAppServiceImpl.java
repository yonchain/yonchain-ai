package com.yonchain.ai.dify.service;

import com.yonchain.ai.api.dify.DifyApp;
import com.yonchain.ai.api.dify.DifyAppService;
import com.yonchain.ai.dify.api.DifyAppApi;
import com.yonchain.ai.util.MapUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import com.yonchain.ai.dify.api.DifyAppApi.AppInfoResponse;
import com.yonchain.ai.dify.api.DifyAppApi.AppParametersResponse;

import java.util.Map;

/**
 * dify app 服务实现
 *
 * @author Cgy
 */
public class DifyAppServiceImpl implements DifyAppService {

    @Override
    public DifyApp getAppByApiKey(String apiKey, String baseUrl) {
        DifyAppApi difyAppApi = DifyAppApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
        ResponseEntity<AppInfoResponse> responseEntity = difyAppApi.getAppInfo();
        AppInfoResponse response = responseEntity.getBody();
        DifyApp difyApp = new DifyApp();
        difyApp.setName(response.name());
        difyApp.setDescription(response.description());
        difyApp.setMode(response.mode());
        return difyApp;
    }

    @Override
    public Map<String, Object> getAppParameters(String apiKey, String baseUrl) {
        DifyAppApi difyAppApi = DifyAppApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
        ResponseEntity<AppParametersResponse> responseEntity = difyAppApi.getAppParameters();
        AppParametersResponse response = responseEntity.getBody();
        return MapUtil.convertToMap(response);
    }
}
