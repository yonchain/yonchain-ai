package com.yonchain.ai.api.dify;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.sys.Role;

import java.util.List;
import java.util.Map;

/**
 * 应用服务
 *
 * @author Cgy
 */
public interface DifyService {

    /**
     * 通过ID查询应用
     *
     * @param id 应用ID
     * @return 应用信息
     */
    DifyApp getAppById(String id);

    /**
     * 分页查询应用列表
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 分页应用列表
     */
    Page<DifyApp> getAppsByPage(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 新增应用
     *
     * @param app 应用信息
     */
    void createApp(DifyApp app);

    /**
     * 新增应用
     *
     * @param app     应用信息
     * @param roleIds 角色ID列表
     */
    void createApp(DifyApp app, List<String> roleIds);

    /**
     * 修改应用
     *
     * @param app 应用信息
     */
    void updateApp(DifyApp app);

    /**
     * 修改应用
     *
     * @param app     应用信息
     * @param roleIds 角色ID列表
     */
    void updateApp(DifyApp app, List<String> roleIds);

    /**
     * 通过ID删除应用
     *
     * @param id 应用ID
     */
    void deleteAppById(String id);

    /**
     * 获取应用关联的角色
     *
     * @param appId 应用ID
     * @return 角色列表
     */
    List<Role> getAppRoles(String appId);

    /**
     * 批量保存应用角色关联
     *
     * @param appId   应用ID
     * @param roleIds 角色ID列表
     */
    void saveAppRoles(String appId, List<String> roleIds);

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
