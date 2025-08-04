package com.yonchain.ai.chat;

import com.yonchain.ai.app.AppResult;
import com.yonchain.ai.app.ResultMetadata;

public class Generation implements AppResult<String> {

    @Override
    public String getOutput() {
        return "";
    }

    @Override
    public ResultMetadata getMetadata() {
        return null;
    }
}
