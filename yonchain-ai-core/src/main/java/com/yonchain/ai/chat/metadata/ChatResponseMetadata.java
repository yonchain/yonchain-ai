package com.yonchain.ai.chat.metadata;

import com.yonchain.ai.app.ResponseMetadata;

import java.util.Map;
import java.util.Set;

public class ChatResponseMetadata implements ResponseMetadata {

    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public <T> T getRequired(Object key) {
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public <T> T getOrDefault(Object key, T defaultObject) {
        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return Set.of();
    }

    @Override
    public Set<String> keySet() {
        return Set.of();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
