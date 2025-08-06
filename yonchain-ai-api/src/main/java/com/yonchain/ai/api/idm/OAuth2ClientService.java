package com.yonchain.ai.api.idm;

import com.yonchain.ai.api.common.Page;

import java.util.List;
import java.util.Map;

/**
 * OAuth2注册客户端服务接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface OAuth2ClientService {

    /**
     * 根据ID获取OAuth2注册客户端
     *
     * @param id 主键ID
     * @return OAuth2注册客户端
     */
    OAuth2RegisteredClient getById(String id);

    /**
     * 根据客户端ID获取OAuth2注册客户端
     *
     * @param clientId 客户端ID
     * @return OAuth2注册客户端
     */
    OAuth2RegisteredClient getByClientId(String clientId);

    /**
     * 分页查询OAuth2注册客户端
     *
     * @param queryParams 查询参数
     * @param page 页码
     * @param limit 每页记录数
     * @return 分页结果
     */
    Page<OAuth2RegisteredClient> pageOAuth2RegisteredClients(Map<String, Object> queryParams, int page, int limit);

    /**
     * 创建OAuth2注册客户端
     *
     * @param client OAuth2注册客户端
     * @return 是否成功
     */
    void createOAuth2RegisteredClient(OAuth2RegisteredClient client);

    /**
     * 更新OAuth2注册客户端
     *
     * @param client OAuth2注册客户端
     * @return 是否成功
     */
    void updateOAuth2RegisteredClient(OAuth2RegisteredClient client);

    /**
     * 根据ID删除OAuth2注册客户端
     *
     * @param id 主键ID
     * @return 是否成功
     */
    void deleteOAuth2RegisteredClientById(String id);

    /**
     * 根据ID批量删除OAuth2注册客户端
     *
     * @param ids 主键ID列表
     * @return 是否成功
     */
    void deleteOAuth2RegisteredClientByIds(List<String> ids);
}
