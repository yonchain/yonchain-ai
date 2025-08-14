package com.yonchain.ai.console.tag.mapper;

import com.yonchain.ai.api.tag.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Tag表的Mapper接口
 *
 * @author Cgy
 */
@Mapper
public interface TagMapper {

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签实体
     */
    Tag selectById(@Param("id") String id);

    /**
     * 查询标签列表
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 标签列表
     */
    List<Tag> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 插入一条标签记录
     *
     * @param tag 标签实体
     * @return 影响的行数
     */
    int insert(Tag tag);

    /**
     * 根据ID更新标签信息
     *
     * @param tag 标签实体
     * @return 影响的行数
     */
    int updateById(Tag tag);

    /**
     * 根据ID删除标签
     *
     * @param id 标签ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除标签
     *
     * @param ids 标签ID列表
     * @return 影响的行数
     */
    void deleteByIds(@Param("ids") List<String> ids);
}
