package com.yonchain.ai.chat;

import com.yonchain.ai.app.StreamingApp;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface StreamingChatApp extends StreamingApp<InputMessage, ChatResponse> {

/*    default Flux<String> stream(String message) {
        InputMessage prompt = new InputMessage();
 *//*       return stream(prompt).map(response -> (response.getResult() == null || response.getResult().getOutput() == null
                || response.getResult().getOutput().getText() == null) ? ""
                : response.getResult().getOutput().getText());*//*
        return null;
    }*/

   /* default Flux<String> stream(Message... messages) {
        Prompt prompt = new Prompt(Arrays.asList(messages));
        return stream(prompt).map(response -> (response.getResult() == null || response.getResult().getOutput() == null
                || response.getResult().getOutput().getText() == null) ? ""
                : response.getResult().getOutput().getText());
    }*/

    @Override
    Flux<ChatResponse> stream(InputMessage input);


}

