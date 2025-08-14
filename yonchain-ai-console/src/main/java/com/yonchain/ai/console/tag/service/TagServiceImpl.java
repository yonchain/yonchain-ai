package com.yonchain.ai.console.tag.service;

import com.github.pagehelper.PageHelper;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.tag.Tag;
import com.yonchain.ai.api.tag.TagService;
import com.yonchain.ai.console.tag.mapper.TagMapper;
import com.yonchain.ai.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 标签服务实现类
 *
 * @author Cgy
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;


    @Override
    public Tag getTagById(String id) {
        return tagMapper.selectById(id);
    }

    @Override
    public List<Tag> getTags(String tenantId, Map<String, Object> queryParam) {
        return tagMapper.selectList(tenantId, queryParam);
    }

    @Override
    public Page<Tag> pageTags(String tenantId, Map<String, Object> queryParam, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Tag> tags = tagMapper.selectList(tenantId, queryParam);

        return PageUtil.convert(tags);
    }

    @Override
    @Transactional
    public String createTag(Tag tag) {
        tagMapper.insert(tag);
        return tag.getId();
    }

    @Override
    @Transactional
    public void updateTag(Tag tag) {
        tagMapper.updateById(tag);
    }

    @Override
    @Transactional
    public void deleteTagById(String id) {
        tagMapper.deleteById(id);
    }

    @Override
    public void deleteTagByIds(List<String> ids) {
        tagMapper.deleteByIds(ids);
    }
}
