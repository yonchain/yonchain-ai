package com.yonchain.ai.model.util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 资源加载工具类
 */
public class Resources {
    
    /**
     * 获取资源输入流
     * 
     * @param resource 资源路径
     * @return 输入流
     */
    public static InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(null, resource);
    }
    
    /**
     * 获取资源输入流
     * 
     * @param loader 类加载器
     * @param resource 资源路径
     * @return 输入流
     */
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) {
        InputStream inputStream = null;
        
        if (loader != null) {
            inputStream = loader.getResourceAsStream(resource);
        }
        
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(resource);
        }
        
        if (inputStream == null) {
            inputStream = Resources.class.getResourceAsStream(resource);
        }
        
        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(resource);
            } catch (FileNotFoundException e) {
                // 忽略文件未找到异常，返回null
            }
        }
        
        return inputStream;
    }
    
    /**
     * 获取资源URL路径
     * 
     * @param resource 资源路径
     * @return URL路径
     */
    public static String getResourceURL(String resource) {
        return getResourceURL(null, resource);
    }
    
    /**
     * 获取资源URL路径
     * 
     * @param loader 类加载器
     * @param resource 资源路径
     * @return URL路径
     */
    public static String getResourceURL(ClassLoader loader, String resource) {
        java.net.URL url = null;
        
        if (loader != null) {
            url = loader.getResource(resource);
        }
        
        if (url == null) {
            url = ClassLoader.getSystemResource(resource);
        }
        
        if (url == null) {
            url = Resources.class.getResource(resource);
        }
        
        return url != null ? url.toString() : null;
    }
}
