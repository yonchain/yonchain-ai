package com.yonchain.ai.api.tag;


import com.yonchain.ai.api.common.Page;

import java.util.List;
import java.util.Map;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 获取标签
     *
     * @param id 标签ID
     * @return 标签信息
     */
    Tag getTagById(String id);


    /**
     * 分页查询标签
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @return 标签列表
     */
    List<Tag> getTags(String tenantId, Map<String, Object> queryParam);

    /**
     * 分页查询标签
     *
     * @param tenantId   租户ID
     * @param queryParam 查询参数
     * @param pageNum    当前页
     * @param pageSize   每页大小
     * @return 分页结果
     */
    Page<Tag> pageTags(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize);

    /**
     * 创建标签
     *
     * @param tag 标签信息
     * @return 创建的标签
     */
    String createTag(Tag tag);

    /**
     * 更新标签
     *
     * @param tag 标签信息
     */
    void updateTag(Tag tag);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void deleteTagById(String id);

    /**
     * 批量删除标签
     *
     * @param ids 标签ID列表
     */
    void deleteTagByIds(List<String> ids);
}
