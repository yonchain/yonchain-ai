package com.yonchain.ai.model.core;

/**
 * Factory for creating ModelClient instances.
 */
public interface ModelClientFactory {

    ModelClient createClient();

    ModelConfiguration getConfiguration();
}


