package com.yonchain.ai.web.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求基类
 *
 * @author Cgy
 * @since 1.0.0
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 当前页码，默认为1
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数，默认为10
     */
    private Integer pageSize = 10;

}
