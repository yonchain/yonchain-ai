package com.yonchain.ai.agent;

import reactor.core.publisher.Flux;

/**
 * The StreamingApp interface provides a generic API for invoking an AI apps with
 * streaming response. It abstracts the process of sending requests and receiving a
 * streaming responses. The interface uses Java generics to accommodate different types of
 * requests and responses, enhancing flexibility and adaptability across different AI
 * app implementations.
 *
 * @param <TReq>      the generic type of the request to the AI app
 * @param <TResChunk> the generic type of a single item in the streaming response from the
 *                    AI app
 * @author Cgy
 * @since 0.1.0
 */
public interface StreamingApp<TReq extends AppRequest<?>, TResChunk extends AppResponse<?>> {

    /**
     * Executes a method call to the AI app.
     *
     * @param request the request object to be sent to the AI app
     * @return the streaming response from the AI app
     */
    Flux<TResChunk> stream(TReq request);

}
