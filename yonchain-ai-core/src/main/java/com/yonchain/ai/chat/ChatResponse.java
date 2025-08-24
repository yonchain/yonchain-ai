package com.yonchain.ai.chat;

import com.yonchain.ai.agent.AppResponse;
import com.yonchain.ai.agent.ResponseMetadata;
import com.yonchain.ai.chat.metadata.ChatResponseMetadata;

import java.util.List;

public class ChatResponse implements AppResponse<Generation> {

    private final Generation generation ;

    private final ChatResponseMetadata metadata;

    public ChatResponse(Generation generation) {
        this(generation, new ChatResponseMetadata());
    }

    public ChatResponse(Generation generation, ChatResponseMetadata metadata) {
        this.generation = generation;
        this.metadata = metadata;
    }

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
