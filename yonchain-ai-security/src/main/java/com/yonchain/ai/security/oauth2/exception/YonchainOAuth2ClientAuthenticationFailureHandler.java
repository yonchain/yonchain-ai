package com.yonchain.ai.security.oauth2.exception;

import com.yonchain.ai.web.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
public class YonchainOAuth2ClientAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(YonchainOAuth2ClientAuthenticationFailureHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String error = "unauthorized";
        String message = exception.getMessage();

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2Error oauth2Error = ((OAuth2AuthenticationException) exception).getError();
            error = oauth2Error.getErrorCode();
            message = oauth2Error.getDescription();

            // 根据错误代码设置适当的 HTTP 状态码
            switch (error) {
                case "invalid_token":
                    status = HttpStatus.UNAUTHORIZED;
                    break;
                case "invalid_request":
                    status = HttpStatus.BAD_REQUEST;
                    break;
                case "insufficient_scope":
                    status = HttpStatus.FORBIDDEN;
                    break;
                default:
                    status = HttpStatus.UNAUTHORIZED;
            }
        }

        log.info("无效的客户端凭证 - 错误码：{}，错误信息：{}", error, message);

        ApiErrorResponse errorResponse = new ApiErrorResponse(status.value(), error,"无效的客户端凭证");

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
