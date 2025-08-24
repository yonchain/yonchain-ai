package com.yonchain.ai.agent;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Interface representing metadata associated with an AI app's response.
 *
 * @author Cgy
 * @since 0.1.0
 */
public interface ResponseMetadata {

    /**
     * Gets an entry from the context. Returns {@code null} when entry is not present.
     *
     * @param key key
     * @param <T> value type
     * @return entry or {@code null} if not present
     */
    @Nullable
    <T> T get(String key);

    /**
     * Gets an entry from the context. Throws exception when entry is not present.
     *
     * @param key key
     * @param <T> value type
     * @return entry
     * @throws IllegalArgumentException if not present
     */
    @NonNull
    <T> T getRequired(Object key);

    /**
     * Checks if context contains a key.
     *
     * @param key key
     * @return {@code true} when the context contains the entry with the given key
     */
    boolean containsKey(Object key);

    /**
     * Returns an element or default if not present.
     *
     * @param key           key
     * @param defaultObject default object to return
     * @param <T>           value type
     * @return object or default if not present
     */
    <T> T getOrDefault(Object key, T defaultObject);

    /**
     * Returns an element or default if not present.
     *
     * @param key                   key
     * @param defaultObjectSupplier supplier for default object to return
     * @param <T>                   value type
     * @return object or default if not present
     * @since 1.11.0
     */
    default <T> T getOrDefault(String key, Supplier<T> defaultObjectSupplier) {
        T value = get(key);
        return value != null ? value : defaultObjectSupplier.get();
    }

    Set<Map.Entry<String, Object>> entrySet();

    Set<String> keySet();

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    boolean isEmpty();
}
