package com.yonchain.ai.web.response;

public enum ErrorCode {

    // 鉴权
    INVALID_REQUEST ("401","请求参数不对"),

    INVALID_GRANT ("402","授权类型不对"),

    NOT("402","余额不足"),

    SERVER_ERROR("500", "服务器内部错误"),

    //身份验证

    //租户管理
    TENANT("TENANT.01","租户不存在"),

    //应用管理
    APP_NOT_FOUND("APP.01","应用未找到");



    private final String code;

    private final String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ErrorCode getErrorCode(String name) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.name().equals(name)) {
                return errorCode;
            }
        }
        return null;
    }

    ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
