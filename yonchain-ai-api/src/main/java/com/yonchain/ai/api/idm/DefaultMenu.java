package com.yonchain.ai.api.idm;

import java.time.LocalDateTime;

/**
 * 默认菜单实现类
 */
public class DefaultMenu implements Menu {

    private String id;
    private String parentId;
    private Integer weight;
    private String name;
    private String path;
    private Boolean isLink;
    private Boolean isIframe;
    private Boolean isKeepAlive;
    private String icon;
    private String enName;
    private Boolean isAffix;
    private String title;
    private Boolean isHide;
    private Integer sortOrder;
    private String menuType;
    private String permission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Boolean getIsLink() {
        return isLink;
    }

    @Override
    public void setIsLink(Boolean isLink) {
        this.isLink = isLink;
    }

    public Boolean getIsIframe() {
        return isIframe;
    }

    public void setIsIframe(Boolean iframe) {
        isIframe = iframe;
    }

    public Boolean getIsKeepAlive() {
        return isKeepAlive;
    }

    public void setIsKeepAlive(Boolean keepAlive) {
        isKeepAlive = keepAlive;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getEnName() {
        return enName;
    }

    @Override
    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Boolean getIsAffix() {
        return isAffix;
    }

    public void setIsAffix(Boolean affix) {
        isAffix = affix;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(Boolean hide) {
        isHide = hide;
    }

    @Override
    public Integer getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String getMenuType() {
        return menuType;
    }

    @Override
    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
