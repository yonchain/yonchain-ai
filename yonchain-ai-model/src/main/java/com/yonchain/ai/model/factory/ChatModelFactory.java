package com.yonchain.ai.model.factory;

import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.registry.OptionsHandlerRegistry;
import org.springframework.ai.chat.model.ChatModel;

/**
 * 聊天模型工厂接口
 */
public interface ChatModelFactory {
    
    /**
     * 创建聊天模型
     * 
     * @param definition 模型定义（包含所有必要的配置信息）
     * @return Spring AI ChatModel实例
     */
    ChatModel createChatModel(ModelDefinition definition);
}
