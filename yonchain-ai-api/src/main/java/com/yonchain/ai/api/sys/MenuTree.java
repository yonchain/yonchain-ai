package com.yonchain.ai.api.sys;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树形结构接口
 * @param <T> 具体的菜单树实现类型
 */
public interface MenuTree<T extends MenuTree<T>> extends Menu {

    /**
     * 获取子菜单列表
     * @return 子菜单列表
     */
    List<T> getChildren();

    /**
     * 设置子菜单列表
     * @param children 子菜单列表
     */
    void setChildren(List<T> children);

    /**
     * 添加子菜单
     * @param child 子菜单
     */
    default void addChild(T child) {
        if (getChildren() == null) {
            setChildren(new ArrayList<>());
        }
        getChildren().add(child);
    }

    /**
     * 移除子菜单
     * @param child 子菜单
     * @return 是否移除成功
     */
    default boolean removeChild(T child) {
        return getChildren() != null && getChildren().remove(child);
    }
}
