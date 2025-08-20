package com.yonchain.ai.model.config;

import com.yonchain.ai.model.handler.JsonTypeHandler;
import com.yonchain.ai.model.handler.MapTypeHandler;
import com.yonchain.ai.model.handler.StringArrayTypeHandler;
import com.yonchain.ai.model.handler.StringListTypeHandler;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * MyBatis配置类
 * 注册自定义类型处理器
 */
@Configuration
public class MyBatisConfig {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addTypeHandlers() {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
            
            // 注册类型处理器
            typeHandlerRegistry.register(JsonTypeHandler.class);
            typeHandlerRegistry.register(MapTypeHandler.class);
            typeHandlerRegistry.register(StringArrayTypeHandler.class);
            typeHandlerRegistry.register(StringListTypeHandler.class);
            
            // 为特定类型注册处理器
            typeHandlerRegistry.register(Map.class, MapTypeHandler.class);
            typeHandlerRegistry.register(String[].class, StringArrayTypeHandler.class);
            typeHandlerRegistry.register(List.class, StringListTypeHandler.class);
        }
    }
}