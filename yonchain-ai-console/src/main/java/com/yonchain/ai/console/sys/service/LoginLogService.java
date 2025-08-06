package com.yonchain.ai.console.sys.service;

import com.yonchain.ai.console.sys.entity.LoginLogEntity;
import com.yonchain.ai.console.sys.mapper.LoginLogMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 登录日志服务
 */
@Service
public class LoginLogService {

    private static final Logger logger = LoggerFactory.getLogger(LoginLogService.class);

    @Autowired
    private LoginLogMapper loginLogMapper;

    /**
     * 保存登录日志
     * @param userId 用户ID
     * @param request HTTP请求对象
     */
    public void saveLoginLog(String userId, String ip) {
        LoginLogEntity log = new LoginLogEntity();
        log.setId(UUID.randomUUID().toString());
        log.setUserId(userId);
        log.setIpAddress(ip);
        log.setIpRegion(getIpRegion(log.getIpAddress()));
        log.setLoginTime(LocalDateTime.now());
        //log.setDeviceType(getDeviceType(request));

        loginLogMapper.insert(log);
    }

    /**
     * 根据用户ID查询登录日志
     * @param userId 用户ID
     * @return 登录日志列表
     */
    public List<LoginLogEntity> getLogsByUserId(String userId) {
        return loginLogMapper.selectByUserId(userId);
    }

    /**
     * 查询最近的登录日志
     * @param limit 查询条数
     * @return 登录日志列表
     */
    public List<LoginLogEntity> getRecentLogs(int limit) {
        return loginLogMapper.selectRecentLogs(limit);
    }


    /**
     * 获取设备类型
     */
    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            return "未知";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile")) {
            return "移动端";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone")) {
            return "iPhone";
        } else if (userAgent.contains("ipad")) {
            return "iPad";
        } else if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "Mac";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else {
            return "其他";
        }
    }

    /**
     * IP地址转地区 - 使用淘宝IP库API
     */
    private String getIpRegion(String ipAddress) {
        if (!StringUtils.hasText(ipAddress) || "127.0.0.1".equals(ipAddress)) {
            return "本地";
        }
        
        try {
            // 调用淘宝IP库API
            String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ipAddress;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = new ObjectMapper().readTree(response.getBody());
                if (root.get("code").asInt() == 0) {
                    JsonNode data = root.get("data");
                    String country = data.get("country").asText();
                    String region = data.get("region").asText();
                    String city = data.get("city").asText();
                    return country + " " + region + " " + city;
                }
            }
        } catch (Exception e) {
            logger.error("查询IP地区信息失败: {}", ipAddress, e);
        }
        
        return "未知";
    }
}
