package com.yonchain.ai.agent.mapper;

import com.yonchain.ai.api.agent.AgentPublishRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 智能体发布记录Mapper接口
 *
 * @author Cgy
 * @since 2024-08-27
 */
@Mapper
public interface AgentPublishRecordMapper {

    /**
     * 插入发布记录
     *
     * @param record 发布记录
     * @return 影响行数
     */
    int insert(AgentPublishRecord record);

    /**
     * 根据ID查询发布记录
     *
     * @param id 记录ID
     * @return 发布记录
     */
    AgentPublishRecord selectById(@Param("id") String id);

    /**
     * 根据智能体ID查询最新的发布记录
     *
     * @param agentId 智能体ID
     * @return 发布记录
     */
    AgentPublishRecord selectLatestByAgentId(@Param("agentId") String agentId);

    /**
     * 根据智能体ID查询所有发布记录
     *
     * @param agentId 智能体ID
     * @return 发布记录列表
     */
    List<AgentPublishRecord> selectByAgentId(@Param("agentId") String agentId);

    /**
     * 根据智能体ID获取最新版本号
     *
     * @param agentId 智能体ID
     * @return 最新版本号
     */
    Integer selectLatestVersionByAgentId(@Param("agentId") String agentId);

    /**
     * 分页查询发布记录
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 发布记录列表
     */
    List<AgentPublishRecord> selectPageByTenantIdAndParams(
            @Param("tenantId") String tenantId,
            @Param("params") Map<String, Object> params);

    /**
     * 统计发布记录数量
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 记录数量
     */
    long countByTenantIdAndParams(
            @Param("tenantId") String tenantId,
            @Param("params") Map<String, Object> params);
}