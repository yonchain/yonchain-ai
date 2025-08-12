package com.yonchain.ai.api.dify;

import java.util.Map;

/**
 * 应用服务
 *
 * @author Cgy
 */
public interface DifyAppService {

    /**
     * 通过apiKey获取应用信息
     *
     * @param apiKey  密钥
     * @param baseUrl 基础URL
     * @return 应用信息
     */
    DifyApp getAppByApiKey(String apiKey, String baseUrl);


    /**
     * 通过apiKey获取应用参数
     *
     * @param apiKey  密钥
     * @param baseUrl 基础URL
     * @return 应用参数
     */
    Map<String, Object> getAppParameters(String apiKey, String baseUrl);
}
