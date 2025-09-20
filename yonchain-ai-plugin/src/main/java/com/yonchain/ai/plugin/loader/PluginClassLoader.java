package com.yonchain.ai.plugin.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 插件类加载器
 * 负责动态加载插件JAR文件和类
 * 
 * @author yonchain
 */
@Component
public class PluginClassLoader {
    
    private static final Logger log = LoggerFactory.getLogger(PluginClassLoader.class);
    
    // 缓存已加载的类加载器，避免重复加载
    private final Map<String, URLClassLoader> classLoaderCache = new ConcurrentHashMap<>();
    
    // 缓存已加载的类，提高性能
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();
    
    /**
     * 从指定路径加载类
     * 
     * @param pluginPath 插件路径（JAR文件或目录）
     * @param className 类名
     * @return 加载的类
     * @throws ClassNotFoundException 类未找到异常
     * @throws IOException IO异常
     */
    public Class<?> loadClass(Path pluginPath, String className) throws ClassNotFoundException, IOException {
        String cacheKey = pluginPath.toString() + ":" + className;
        
        // 先检查缓存
        Class<?> cachedClass = classCache.get(cacheKey);
        if (cachedClass != null) {
            log.debug("Using cached class: {} from {}", className, pluginPath);
            return cachedClass;
        }
        
        // 获取类加载器
        URLClassLoader classLoader = getOrCreateClassLoader(pluginPath);
        
        try {
            // 加载类
            Class<?> loadedClass = classLoader.loadClass(className);
            
            // 缓存已加载的类
            classCache.put(cacheKey, loadedClass);
            
            log.debug("Successfully loaded class: {} from {}", className, pluginPath);
            return loadedClass;
            
        } catch (ClassNotFoundException e) {
            log.error("Failed to load class: {} from {}", className, pluginPath, e);
            throw e;
        }
    }
    
    /**
     * 从指定路径加载类（字符串路径）
     * 
     * @param pluginPathStr 插件路径字符串
     * @param className 类名
     * @return 加载的类
     * @throws ClassNotFoundException 类未找到异常
     * @throws IOException IO异常
     */
    public Class<?> loadClass(String pluginPathStr, String className) throws ClassNotFoundException, IOException {
        Path pluginPath = Paths.get(pluginPathStr);
        return loadClass(pluginPath, className);
    }
    
    /**
     * 创建类实例
     * 
     * @param pluginPath 插件路径
     * @param className 类名
     * @param <T> 类型参数
     * @return 类实例
     * @throws Exception 创建实例异常
     */
    @SuppressWarnings("unchecked")
    public <T> T createInstance(Path pluginPath, String className) throws Exception {
        Class<?> clazz = loadClass(pluginPath, className);
        return (T) clazz.getDeclaredConstructor().newInstance();
    }
    
    /**
     * 创建类实例（字符串路径）
     * 
     * @param pluginPathStr 插件路径字符串
     * @param className 类名
     * @param <T> 类型参数
     * @return 类实例
     * @throws Exception 创建实例异常
     */
    public <T> T createInstance(String pluginPathStr, String className) throws Exception {
        Path pluginPath = Paths.get(pluginPathStr);
        return createInstance(pluginPath, className);
    }
    
    /**
     * 卸载插件类加载器
     * 
     * @param pluginPath 插件路径
     */
    public void unloadPlugin(Path pluginPath) {
        String pathKey = pluginPath.toString();
        URLClassLoader classLoader = classLoaderCache.remove(pathKey);
        
        if (classLoader != null) {
            try {
                classLoader.close();
                log.debug("Unloaded plugin class loader for: {}", pluginPath);
            } catch (IOException e) {
                log.error("Failed to close class loader for: {}", pluginPath, e);
            }
        }
        
        // 清理相关的类缓存
        classCache.entrySet().removeIf(entry -> entry.getKey().startsWith(pathKey + ":"));
    }
    
    /**
     * 卸载插件类加载器（字符串路径）
     * 
     * @param pluginPathStr 插件路径字符串
     */
    public void unloadPlugin(String pluginPathStr) {
        Path pluginPath = Paths.get(pluginPathStr);
        unloadPlugin(pluginPath);
    }
    
    /**
     * 获取或创建类加载器
     * 
     * @param pluginPath 插件路径
     * @return 类加载器
     * @throws IOException IO异常
     */
    private URLClassLoader getOrCreateClassLoader(Path pluginPath) throws IOException {
        String pathKey = pluginPath.toString();
        
        // 先检查缓存
        URLClassLoader cachedClassLoader = classLoaderCache.get(pathKey);
        if (cachedClassLoader != null) {
            return cachedClassLoader;
        }
        
        // 创建新的类加载器
        URLClassLoader classLoader = createClassLoader(pluginPath);
        classLoaderCache.put(pathKey, classLoader);
        
        return classLoader;
    }
    
    /**
     * 创建类加载器
     * 
     * @param pluginPath 插件路径
     * @return 类加载器
     * @throws IOException IO异常
     */
    private URLClassLoader createClassLoader(Path pluginPath) throws IOException {
        if (!Files.exists(pluginPath)) {
            throw new IOException("Plugin path does not exist: " + pluginPath);
        }
        
        if (Files.isRegularFile(pluginPath)) {
            // JAR文件
            if (pluginPath.toString().endsWith(".jar")) {
                URL jarUrl = pluginPath.toUri().toURL();
                log.debug("Creating class loader for JAR: {}", pluginPath);
                return new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());
            } else {
                throw new IOException("Unsupported plugin file format: " + pluginPath);
            }
        } else if (Files.isDirectory(pluginPath)) {
            // 目录（开发模式）
            log.debug("Creating class loader for directory: {}", pluginPath);
            return createDirectoryClassLoader(pluginPath);
        } else {
            throw new IOException("Invalid plugin path: " + pluginPath);
        }
    }
    
    /**
     * 为目录创建类加载器（开发模式）
     * 
     * @param pluginDir 插件目录
     * @return 类加载器
     * @throws IOException IO异常
     */
    private URLClassLoader createDirectoryClassLoader(Path pluginDir) throws IOException {
        // 查找所有JAR文件和classes目录
        try (Stream<Path> paths = Files.walk(pluginDir)) {
            URL[] urls = paths
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".jar") ||
                               Files.isDirectory(path) && path.getFileName().toString().equals("classes"))
                .map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (Exception e) {
                        log.error("Failed to convert path to URL: {}", path, e);
                        return null;
                    }
                })
                .filter(url -> url != null)
                .toArray(URL[]::new);
            
            if (urls.length == 0) {
                // 如果没有找到JAR文件或classes目录，使用插件目录本身
                urls = new URL[]{pluginDir.toUri().toURL()};
            }
            
            log.debug("Creating directory class loader with {} URLs for: {}", urls.length, pluginDir);
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        }
    }
    
    /**
     * 检查类是否存在
     * 
     * @param pluginPath 插件路径
     * @param className 类名
     * @return 是否存在
     */
    public boolean classExists(Path pluginPath, String className) {
        try {
            loadClass(pluginPath, className);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查类是否存在（字符串路径）
     * 
     * @param pluginPathStr 插件路径字符串
     * @param className 类名
     * @return 是否存在
     */
    public boolean classExists(String pluginPathStr, String className) {
        Path pluginPath = Paths.get(pluginPathStr);
        return classExists(pluginPath, className);
    }
    
    /**
     * 清理所有缓存
     */
    public void clearCache() {
        log.info("Clearing plugin loader cache...");
        
        // 关闭所有类加载器
        for (URLClassLoader classLoader : classLoaderCache.values()) {
            try {
                classLoader.close();
            } catch (IOException e) {
                log.error("Failed to close class loader", e);
            }
        }
        
        // 清理缓存
        classLoaderCache.clear();
        classCache.clear();
        
        log.info("Plugin loader cache cleared");
    }
    
    /**
     * 获取缓存统计信息
     * 
     * @return 统计信息
     */
    public CacheStats getCacheStats() {
        return new CacheStats(classLoaderCache.size(), classCache.size());
    }
    
    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        private final int classLoaderCount;
        private final int classCount;
        
        public CacheStats(int classLoaderCount, int classCount) {
            this.classLoaderCount = classLoaderCount;
            this.classCount = classCount;
        }
        
        public int getClassLoaderCount() {
            return classLoaderCount;
        }
        
        public int getClassCount() {
            return classCount;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{classLoaders=%d, classes=%d}", classLoaderCount, classCount);
        }
    }
}
