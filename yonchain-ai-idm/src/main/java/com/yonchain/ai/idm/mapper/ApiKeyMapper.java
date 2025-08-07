package com.yonchain.ai.idm.mapper;

import com.yonchain.ai.api.idm.ApiKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * API密钥表的Mapper接口
 *
 * @author Cgy
 */
public interface ApiKeyMapper {

    /**
     * 查询API密钥列表
     *
     * @param tenantId 租户ID
     * @param type     密钥类型
     * @param params   查询参数
     * @return API密钥列表
     */
    List<ApiKey> selectList(@Param("tenantId") String tenantId, @Param("type") String type,
                            @Param("params") Map<String, Object> params);

    /**
     * 插入一条API密钥记录
     *
     * @param apiKey API密钥实体
     * @return 影响的行数
     */
    int insert(ApiKey apiKey);

    /**
     * 根据ID删除API密钥
     *
     * @param id API密钥ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);

}