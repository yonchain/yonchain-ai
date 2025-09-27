package com.yonchain.ai.plugin;

import com.yonchain.ai.model.ModelFactory;
import com.yonchain.ai.plugin.spi.ModelProvider;

public interface PluginModelFactory extends ModelFactory {

   void registerProvider(String providerName, ModelProvider modelProvider);

   void unregisterProvider(String providerName);
}
