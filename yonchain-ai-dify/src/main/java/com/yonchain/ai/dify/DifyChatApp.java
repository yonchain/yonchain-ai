package com.yonchain.ai.dify;

import com.yonchain.ai.chat.ChatApp;
import com.yonchain.ai.chat.ChatResponse;
import com.yonchain.ai.chat.Generation;
import com.yonchain.ai.chat.InputMessage;
import com.yonchain.ai.dify.api.DifyApi;
import com.yonchain.ai.dify.api.DifyApi.ChatCompletionRequest;
import com.yonchain.ai.dify.api.DifyApi.ChatCompletionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * DifyChatApp 是基于 Dify API 实现的聊天应用
 * 该类实现了 ChatApp 接口，提供了同步调用和流式调用两种方式与 Dify 服务交互
 *
 * @author Cgy
 * @see ChatApp 基础聊天应用接口
 * @see DifyApi Dify API 客户端
 * @see DifyCharOptions Dify 聊天配置
 */
public class DifyChatApp implements ChatApp {

    private final DifyApi difyApi;

    private final DifyCharOptions difyCharOptions;

    private final RetryTemplate retryTemplate;


    public DifyChatApp(DifyApi difyApi, DifyCharOptions difyCharOptions,
                       RetryTemplate retryTemplate) {
        Assert.notNull(difyApi, "openAiApi cannot be null");
        Assert.notNull(difyCharOptions, "defaultOptions cannot be null");
        Assert.notNull(retryTemplate, "retryTemplate cannot be null");
        this.difyApi = difyApi;
        this.difyCharOptions = difyCharOptions;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public ChatResponse call(InputMessage input) {
        //TODO: implement your chat app
        ChatCompletionRequest request = createRequest(input);
        difyApi.createChatMessageEntity(request);

        ResponseEntity<ChatCompletionResponse> response = this.retryTemplate
                .execute(ctx -> difyApi.createChatMessageEntity(request));
        var chatCompletion = response.getBody();
        if (chatCompletion == null) {
            throw new RuntimeException("chatCompletion is null");
        }
        return new ChatResponse(new Generation(chatCompletion.messageId(), null));
    }

    private ChatCompletionRequest createRequest(InputMessage input) {
        return new ChatCompletionRequest(input.getInstructions(), null, null);
    }

    @Override
    public Flux<ChatResponse> stream(InputMessage input) {
        //TODO: implement your chat app
        return null;
    }
}
