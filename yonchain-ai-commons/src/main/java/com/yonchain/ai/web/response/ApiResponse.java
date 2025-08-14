package com.yonchain.ai.web.response;

/**
 * API响应
 *
 * @param <T> 数据类型
 * @author Cgy
 * @since 1.0.0
 */
public class ApiResponse<T> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功响应
     *
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }


    /**
     * 成功响应
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode("200");
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode("200");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     *
     * @param code    状态码
     * @param message 消息
     * @return API响应
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * Failed return with msg and detail error information.
     *
     * @return ApiResponse
     */
    public static ApiResponse<String> fail(String msg) {
        return fail(ErrorCode.SERVER_ERROR.getCode(), msg);
    }

    /**
     * Failed return with errorCode and msg.
     *
     * @param <T> data type
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMsg());
    }

    /**
     * Failed return with errorCode, msg and data.
     *
     * @param <T> data type
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T data) {
        return fail(errorCode.getCode(), errorCode.getMsg(), data);
    }

    /**
     * Failed return with code, msg and data.
     *
     * @param <T>  data type
     * @param code error code
     * @param msg  error msg
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(String code, String msg, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(msg);
        response.setData(data);
        return response;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
