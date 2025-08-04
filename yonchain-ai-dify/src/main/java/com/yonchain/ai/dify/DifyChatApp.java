package com.yonchain.ai.dify;

import com.yonchain.ai.chat.ChatApp;
import com.yonchain.ai.chat.ChatResponse;
import com.yonchain.ai.chat.InputMessage;
import com.yonchain.ai.dify.api.DifyApi;
import reactor.core.publisher.Flux;

/**
 * DifyChatApp 是基于 Dify API 实现的聊天应用
 * 该类实现了 ChatApp 接口，提供了同步调用和流式调用两种方式与 Dify 服务交互
 *
 * @see ChatApp 基础聊天应用接口
 * @see DifyApi Dify API 客户端
 * @see DifyCharOptions Dify 聊天配置
 *
 * @author Cgy
 */
public class DifyChatApp implements ChatApp {

    private final DifyApi difyApi;

    private final DifyCharOptions difyCharOptions;

    public DifyChatApp(DifyApi difyApi, DifyCharOptions difyCharOptions) {
        this.difyApi = difyApi;
        this.difyCharOptions = difyCharOptions;
    }

    @Override
    public ChatResponse call(InputMessage input) {
        //TODO: implement your chat app
        //return difyApi.createChatMessageEntity(difyCharOptions.toRequest(input));
        return null;
    }

    @Override
    public Flux<ChatResponse> stream(InputMessage input) {
        //TODO: implement your chat app
        return null;
    }
}
