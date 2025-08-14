package com.yonchain.ai.sys.entity;

import com.yonchain.ai.api.sys.Menu;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单实体类
 */
@Data
public class MenuEntity implements Menu {

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
    private Integer sort;
    private String menuType;
    private String permission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
