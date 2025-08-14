package com.yonchain.ai.console.tag.entity;

import com.yonchain.ai.api.tag.Tag;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体
 */
@Data
public class TagEntity implements Tag {

    /**
     * 标签ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 标签类型
     */
    private String type;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}
