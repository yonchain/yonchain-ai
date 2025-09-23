package com.yonchain.ai.model.core;

/**
 * Factory for creating ModelClient instances.
 */
public interface ModelClientFactory {

    ModelClient createClient();

    ModelClient createClient(String namespace);

    ModelConfiguration getConfiguration();
}


