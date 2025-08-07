package com.yonchain.ai.console.sys.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取版本信息
 *
 * @author Cgy
 */
@RestController
public class SysVersionController {


    @Value("${yonchain.version}")
    private String version;


    /**
     * 获取当前版本号
     *
     * @return
     */
    @GetMapping("/version")
    public String getVersion() {
        return version;
    }
}
