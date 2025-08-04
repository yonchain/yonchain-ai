package com.yonchain.ai.chat;

import com.yonchain.ai.app.AppResponse;
import com.yonchain.ai.app.ResponseMetadata;

import java.util.List;

public class ChatResponse implements AppResponse<Generation> {

    @Override
    public Generation getResult() {
        return null;
    }

    @Override
    public List<Generation> getResults() {
        return List.of();
    }

    @Override
    public ResponseMetadata getMetadata() {
        return null;
    }
}
