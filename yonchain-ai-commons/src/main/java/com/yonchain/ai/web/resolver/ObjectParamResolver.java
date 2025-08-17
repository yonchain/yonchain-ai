package com.yonchain.ai.web.resolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class ObjectParamResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持所有非简单类型的参数（如对象）
        return !parameter.getParameterType().isPrimitive() && 
               !parameter.getParameterType().getName().startsWith("java.");
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Map<String, String> paramMap = new HashMap<>();

        // 收集并转换请求参数（下划线转驼峰）
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String camelCaseName = doConvertToCamelCase(paramName);
            paramMap.put(camelCaseName, request.getParameter(paramName));
        }

        // 将参数映射到目标对象
        return OBJECT_MAPPER.convertValue(paramMap, parameter.getParameterType());
    }


    private String doConvertToCamelCase(String str) {
        StringBuilder result = new StringBuilder(str.length());
        boolean nextUpper = false;
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    result.append(currentChar);
                }
            }
        }
        return result.toString();
    }
}
