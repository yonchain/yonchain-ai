package com.yonchain.ai.mybatis;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * mybatis 自动装配类
 *
 * @author Cgy
 */
@AutoConfiguration
@MapperScan("com.yonchain.ai.**.mapper.**")
public class MybatisAutoConfiguration {
}
