package com.yonchain.ai.web.response;


import java.io.Serializable;

/**
 * 标准错误响应格式
 *
 * @param status  状态码
 * @param code    错误码，格式为:模块代码.具体错误代码
 *                例如: 001.001 表示请求参数错误
 * @param message 错误信息
 * @author Cgy
 */
public record ApiErrorResponse(int status, String code, String message) implements Serializable {

    /**
     * 构造方法
     *
     * @param status  状态码
     * @param code    错误码
     * @param message 错误信息
     */
    public ApiErrorResponse {
    }
}
