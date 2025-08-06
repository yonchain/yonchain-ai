package com.yonchain.ai.console;

import com.yonchain.ai.api.idm.CurrentUser;
import com.yonchain.ai.console.context.ConsoleContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础控制器类
 *
 * @author Cgy
 * @since 1.0.0
 */
public class BaseController {

    @Autowired
    protected ResponseFactory responseFactory;

    /**
     * 获取当前租户ID
     *
     * @return 当前租户ID字符串
     */
    public String getCurrentTenantId() {
        return getCurrentUser().getTenantId();
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID字符串
     */
    public String getCurrentUserId() {
        return getCurrentUser().getUserId();
    }


    public CurrentUser getCurrentUser() {
        return ConsoleContextHolder.getContext().getCurrentUser();
    }

    /**
     * 抛出业务异常
     * @param code 错误码
     * @param message 错误信息
     *//*
    protected void throwBusinessException(String code, String message) {
        throw new BusinessException(code, message);
    }

    *//**
     * 抛出业务异常
     * @param code 错误码
     * @param status HTTP状态码
     * @param message 错误信息
     *//*
    protected void throwBusinessException(String code, int status, String message) {
        throw new BusinessException(code, HttpStatus.valueOf(status), message);
    }

    *//**
     * 构建错误响应
     * @param code 错误码
     * @param message 错误信息
     * @param request 请求对象
     * @return ErrorResponse
     *//*
    protected ApiErrorResponse buildErrorResponse(String code, String message, WebRequest request) {
        ApiErrorResponse response = responseFactory.createErrorResponse(code, message);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return response;
    }

    *//**
     * 构建错误响应
     * @param code 错误码
     * @param message 错误信息
     * @param detail 错误详情
     * @param request 请求对象
     * @return ErrorResponse
     *//*
    protected ApiErrorResponse buildErrorResponse(String code, String message, String detail, WebRequest request) {
        ApiErrorResponse response = responseFactory.createErrorResponse(code, message, detail);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return response;
    }

    *//**
     * 快速构建常见错误响应
     *//*
    protected ApiErrorResponse badRequest(WebRequest request) {
        return buildErrorResponse(ErrorCodes.BAD_REQUEST, "请求参数错误", request);
    }

    protected ApiErrorResponse unauthorized(WebRequest request) {
        return buildErrorResponse(ErrorCodes.UNAUTHORIZED, "未授权访问", request);
    }

    protected ApiErrorResponse notFound(WebRequest request) {
        return buildErrorResponse(ErrorCodes.NOT_FOUND, "资源不存在", request);
    }

    protected ApiErrorResponse internalServerError(WebRequest request) {
        return buildErrorResponse(ErrorCodes.INTERNAL_SERVER_ERROR, "服务器内部错误", request);
    }*/
}
