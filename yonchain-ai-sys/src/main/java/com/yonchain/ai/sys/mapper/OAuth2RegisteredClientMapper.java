package com.yonchain.ai.sys.mapper;

import com.yonchain.ai.api.sys.OAuth2RegisteredClient;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * OAuth2注册客户端Mapper接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface OAuth2RegisteredClientMapper {

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return 实体对象
     */
    OAuth2RegisteredClient selectById(@Param("id") String id);

    /**
     * 根据客户端ID查询
     *
     * @param clientId 客户端ID
     * @return 实体对象
     */
    OAuth2RegisteredClient selectByClientId(@Param("clientId") String clientId);

    /**
     * 分页查询
     *
     * @param params 查询参数
     * @return 实体对象列表
     */
    List<OAuth2RegisteredClient> selectList(@Param("params") Map<String, Object> params);

    /**
     * 插入
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(OAuth2RegisteredClient entity);

    /**
     * 更新
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(OAuth2RegisteredClient entity);

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据ID批量删除
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatchIds(@Param("ids") List<String> ids);
}
