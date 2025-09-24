package com.yonchain.ai.model.core.impl;

import com.yonchain.ai.model.core.ModelClient;
import com.yonchain.ai.model.core.ModelClientFactory;
import com.yonchain.ai.model.core.ModelConfiguration;

/**
 * 默认的ModelClientFactory实现
 */
public class DefaultModelClientFactory implements ModelClientFactory {
    
    private final ModelConfiguration configuration;
    
    public DefaultModelClientFactory(ModelConfiguration configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public ModelClient createClient() {
        return new DefaultModelClient(configuration);
    }
    
    @Override
    public ModelConfiguration getConfiguration() {
        return configuration;
    }
}
