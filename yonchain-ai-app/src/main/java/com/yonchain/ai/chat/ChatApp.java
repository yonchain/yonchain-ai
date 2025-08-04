package com.yonchain.ai.chat;

import com.yonchain.ai.app.App;
import reactor.core.publisher.Flux;

/**
 * 聊天应用接口，继承自App和StreamingChatApp接口
 * <p>
 * 提供两种交互方式：
 * 1. 同步调用 call() 方法
 * 2. 异步流式调用 stream() 方法（默认不支持，需子类实现）
 *
 * @author Cgy
 */
public interface ChatApp extends App<InputMessage, ChatResponse>, StreamingChatApp {

    @Override
    ChatResponse call(InputMessage input);

    @Override
    default Flux<ChatResponse> stream(InputMessage input) {
        throw new UnsupportedOperationException("streaming is not supported");
    }
}
