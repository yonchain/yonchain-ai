package com.yonchain.ai.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 使用Jackson库将对象转换为Map的工具类
 */
public class MapUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    /**
     * 将任意Java对象转换为Map<String, Object>
     *
     * @param object 要转换的对象
     * @return 包含对象属性的Map
     */
    public static Map<String, Object> convertToMap(Object object) {
        if (object == null) {
            return null;
        }

        try {
            // 使用Jackson的convertValue方法直接将对象转换为Map
            return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to map", e);
        }
    }

    /**
     * 将任意Java对象转换为Map，并支持指定Map的键值类型
     *
     * @param object 要转换的对象
     * @param keyClass Map键的类型
     * @param valueClass Map值的类型
     * @param <K> Map键的泛型类型
     * @param <V> Map值的泛型类型
     * @return 包含对象属性的Map
     */
    public static <K, V> Map<K, V> convertToMap(Object object, Class<K> keyClass, Class<V> valueClass) {
        if (object == null) {
            return null;
        }

        try {
            // 首先转换为普通Map
            Map<String, Object> stringMap = convertToMap(object);

            // 然后转换键和值到指定类型
            Map<K, V> typedMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : stringMap.entrySet()) {
                K key = objectMapper.convertValue(entry.getKey(), keyClass);
                V value = objectMapper.convertValue(entry.getValue(), valueClass);
                typedMap.put(key, value);
            }

            return typedMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to typed map", e);
        }
    }

    /**
     * 将Map转换回Java对象
     *
     * @param map 包含对象属性的Map
     * @param clazz 目标对象的类
     * @param <T> 目标对象的类型
     * @return 转换后的Java对象
     */
    public static <T> T convertToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }

        try {
            return objectMapper.convertValue(map, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to object", e);
        }
    }

    /**
     * 将对象的特定字段转换为Map
     *
     * @param object 要转换的对象
     * @param includeFields 要包含在结果Map中的字段名数组
     * @return 包含指定字段的Map
     */
    public static Map<String, Object> convertSelectedFieldsToMap(Object object, String... includeFields) {
        if (object == null) {
            return null;
        }

        try {
            Map<String, Object> fullMap = convertToMap(object);
            if (includeFields == null || includeFields.length == 0) {
                return fullMap;
            }

            return fullMap.entrySet().stream()
                    .filter(entry -> {
                        for (String field : includeFields) {
                            if (entry.getKey().equals(field)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert selected fields to map", e);
        }
    }

    /**
     * 将对象转换为Map，并排除指定字段
     *
     * @param object 要转换的对象
     * @param excludeFields 要从结果Map中排除的字段名数组
     * @return 不包含指定字段的Map
     */
    public static Map<String, Object> convertToMapExcluding(Object object, String... excludeFields) {
        if (object == null) {
            return null;
        }

        try {
            Map<String, Object> fullMap = convertToMap(object);
            if (excludeFields == null || excludeFields.length == 0) {
                return fullMap;
            }

            return fullMap.entrySet().stream()
                    .filter(entry -> {
                        for (String field : excludeFields) {
                            if (entry.getKey().equals(field)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to map excluding fields", e);
        }
    }

    /**
     * 将对象转换为扁平化的Map（嵌套属性使用点号分隔）
     *
     * @param object 要转换的对象
     * @return 扁平化的Map
     */
    public static Map<String, Object> convertToFlatMap(Object object) {
        if (object == null) {
            return null;
        }

        try {
            Map<String, Object> nestedMap = convertToMap(object);
            Map<String, Object> flatMap = new HashMap<>();
            flattenMap("", nestedMap, flatMap);
            return flatMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to flat map", e);
        }
    }

    /**
     * 递归地将嵌套Map扁平化
     */
    private static void flattenMap(String prefix, Map<String, Object> nestedMap, Map<String, Object> flatMap) {
        for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valueMap = (Map<String, Object>) value;
                flattenMap(key, valueMap, flatMap);
            } else {
                flatMap.put(key, value);
            }
        }
    }

    /**
     * 获取配置好的ObjectMapper实例，用于自定义配置
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}