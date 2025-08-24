package com.yonchain.ai.chat;

import com.yonchain.ai.agent.AppResult;
import com.yonchain.ai.agent.ResultMetadata;

public class Generation implements AppResult<String> {

    private final String output;

    private final ResultMetadata metadata;

    public Generation(String output, ResultMetadata metadata) {
        this.output = output;
        this.metadata = metadata;
    }

    @Override
    public String getOutput() {
        return "";
    }

    @Override
    public ResultMetadata getMetadata() {
        return null;
    }
}
