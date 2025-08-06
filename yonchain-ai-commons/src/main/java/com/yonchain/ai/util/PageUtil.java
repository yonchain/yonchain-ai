package com.yonchain.ai.util;


import com.github.pagehelper.PageInfo;
import com.yonchain.ai.api.common.Page;

import java.util.List;

/**
 * 分页工具类
 *
 * @author Cgy
 * @since 1.0.0
 */
public class PageUtil {

    /**
     * 将PageInfo对象转换为Page对象
     *
     * @param datas 源数据
     * @param <T>   数据类型
     * @return Page对象
     */
    public static <T> Page<T> convert(List<T> datas) {
        PageInfo<T> pageInfo = new PageInfo<>(datas);
        Page<T> page = new Page<>();
        page.setTotal(pageInfo.getTotal());
        page.setCurrent(pageInfo.getPageNum());
        page.setSize(pageInfo.getPageSize());
        page.setRecords(pageInfo.getList());
        return page;
    }

}
