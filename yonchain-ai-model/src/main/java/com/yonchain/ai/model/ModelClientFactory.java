package com.yonchain.ai.model;

/**
 * Factory for creating ModelClient instances.
 */
public interface ModelClientFactory {

    ModelClient createClient();

    ModelConfiguration getConfiguration();
}



