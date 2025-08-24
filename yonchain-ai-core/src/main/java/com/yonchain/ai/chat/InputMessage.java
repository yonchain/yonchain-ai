package com.yonchain.ai.chat;

import com.yonchain.ai.agent.AppRequest;
import io.micrometer.common.lang.Nullable;

public class InputMessage implements AppRequest<String> {

    @Nullable
    private ChatOptions chatOptions;

    @Override
    public String getInstructions() {
        return null;
    }

    @Override
    public ChatOptions getOptions() {
        return chatOptions;
    }
}
