package com.yonchain.ai.chat;

import com.yonchain.ai.model.ModelService;
import org.springframework.ai.chat.model.ChatModel;

public interface ChatModelService extends ModelService<ChatModel> {

    ChatModel getModel(String model);
}
