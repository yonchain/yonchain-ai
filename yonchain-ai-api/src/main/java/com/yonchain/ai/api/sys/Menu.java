package com.yonchain.ai.api.sys;

import java.time.LocalDateTime;

/**
 * 菜单接口定义
 */
public interface Menu {

    /**
     * 获取菜单ID
     * @return 菜单ID
     */
    String getId();
    
    /**
     * 设置菜单ID
     * @param id 菜单ID
     */
    void setId(String id);
    
    /**
     * 获取父菜单ID
     * @return 父菜单ID
     */
    String getParentId();
    
    /**
     * 设置父菜单ID
     * @param parentId 父菜单ID
     */
    void setParentId(String parentId);
    
    /**
     * 获取菜单权重
     * @return 菜单权重值
     */
    Integer getWeight();
    
    /**
     * 设置菜单权重
     * @param weight 菜单权重值
     */
    void setWeight(Integer weight);
    
    /**
     * 获取菜单名称
     * @return 菜单名称
     */
    String getName();
    
    /**
     * 设置菜单名称
     * @param name 菜单名称
     */
    void setName(String name);
    
    /**
     * 获取菜单路径
     * @return 菜单路径
     */
    String getPath();
    
    /**
     * 设置菜单路径
     * @param path 菜单路径
     */
    void setPath(String path);
    
    /**
     * 获取是否为外部链接
     * @return 外部链接地址，非链接则为null
     */
    Boolean getIsLink();
    
    /**
     * 设置是否为外部链接
     * @param isLink 外部链接地址，非链接则为null
     */
    void setIsLink(Boolean isLink);
    
    /**
     * 获取是否iframe嵌套
     * @return true表示iframe嵌套，false表示非iframe
     */
    Boolean getIsIframe();
    
    /**
     * 设置是否iframe嵌套
     * @param isIframe true表示iframe嵌套，false表示非iframe
     */
    void setIsIframe(Boolean isIframe);
    
    /**
     * 获取是否缓存页面
     * @return true表示缓存页面，false表示不缓存
     */
    Boolean getIsKeepAlive();
    
    /**
     * 设置是否缓存页面
     * @param isKeepAlive true表示缓存页面，false表示不缓存
     */
    void setIsKeepAlive(Boolean isKeepAlive);
    
    /**
     * 获取菜单图标
     * @return 菜单图标标识
     */
    String getIcon();
    
    /**
     * 设置菜单图标
     * @param icon 菜单图标标识
     */
    void setIcon(String icon);
    
    /**
     * 获取菜单英文名称
     * @return 菜单英文名称
     */
    String getEnName();
    
    /**
     * 设置菜单英文名称
     * @param enName 菜单英文名称
     */
    void setEnName(String enName);
    
    /**
     * 获取是否固定标签
     * @return true表示固定标签，false表示不固定
     */
    Boolean getIsAffix();
    
    /**
     * 设置是否固定标签
     * @param isAffix true表示固定标签，false表示不固定
     */
    void setIsAffix(Boolean isAffix);
    
    /**
     * 获取菜单标题
     * @return 菜单标题
     */
    String getTitle();
    
    /**
     * 设置菜单标题
     * @param title 菜单标题
     */
    void setTitle(String title);
    
    /**
     * 获取是否隐藏菜单
     * @return true表示隐藏菜单，false表示显示
     */
    Boolean getIsHide();
    
    /**
     * 设置是否隐藏菜单
     * @param isHide true表示隐藏菜单，false表示显示
     */
    void setIsHide(Boolean isHide);
    
    /**
     * 获取排序序号
     * @return 排序序号
     */
    Integer getSort();
    
    /**
     * 设置排序序号
     * @param sort 排序序号
     */
    void setSort(Integer sort);
    
    /**
     * 获取菜单类型
     * @return 菜单类型标识
     */
    String getMenuType();
    
    /**
     * 设置菜单类型
     * @param menuType 菜单类型标识
     */
    void setMenuType(String menuType);
    
    /**
     * 获取菜单权限标识
     * @return 权限标识字符串
     */
    String getPermission();
    
    /**
     * 设置菜单权限标识
     * @param permission 权限标识字符串
     */
    void setPermission(String permission);
    
    /**
     * 获取创建时间
     * @return 创建时间
     */
    LocalDateTime getCreatedAt();
    
    /**
     * 设置创建时间
     * @param createdAt 创建时间
     */
    void setCreatedAt(LocalDateTime createdAt);
    
    /**
     * 获取更新时间
     * @return 更新时间
     */
    LocalDateTime getUpdatedAt();
    
    /**
     * 设置更新时间
     * @param updatedAt 更新时间
     */
    void setUpdatedAt(LocalDateTime updatedAt);
}
