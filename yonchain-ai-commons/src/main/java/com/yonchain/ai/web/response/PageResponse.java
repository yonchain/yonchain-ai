package com.yonchain.ai.web.response;


import java.util.List;

/**
 * 分页响应
 *
 * @param <T> 数据类型
 * @author Cgy
 * @since 1.0.0
 */
public class PageResponse<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 页码
     */
    private Long pageNum;

    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 数据列表
     */
    private List<T> data;

    /**
     * 是否有更多数据
     */
    private Boolean hasMore;

    /**
     * 创建分页响应
     *
     * @param datas     数据列表
     * @param total     总记录数
     * @param pageNum   页码
     * @param pageSize 每页大小
     * @param <T>       数据类型
     * @return 分页响应
     */
    public static <T> PageResponse<T> of(List<T> datas, Long total, Long pageNum, Long pageSize) {
        PageResponse<T> response = new PageResponse<>();
        response.setData(datas);
        response.setTotal(total);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        return response;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public boolean isHasMore() {
        return hasMore == null ? calcHasMore() : hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * 计算是否有更多数据
     *
     * @return 是否有更多数据
     */
    public boolean calcHasMore() {
        if (total == null || pageNum == null || pageSize == null) {
            return false;
        }
        return total > pageNum * pageSize;
    }
}
