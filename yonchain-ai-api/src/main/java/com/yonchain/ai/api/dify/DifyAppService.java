package com.yonchain.ai.api.dify;

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
}
