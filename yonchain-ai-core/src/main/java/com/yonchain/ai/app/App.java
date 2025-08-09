package com.yonchain.ai.app;

/**
 * The App interface provides a generic API for invoking AI apps. It is designed to
 * handle the interaction with various types of AI models by abstracting the process of
 * sending requests and receiving responses. The interface uses Java generics to
 * accommodate different types of requests and responses, enhancing flexibility and
 * adaptability across different AI app implementations.
 *
 * @param <TReq> the generic type of the request to the AI app
 * @param <TRes> the generic type of the response from the AI app
 * @author Cgy
 * @since 0.1.0
 */
public interface App<TReq extends AppRequest<?>, TRes extends AppResponse<?>> {

    /**
     * Executes a method call to the AI app.
     *
     * @param request the request object to be sent to the AI app
     * @return the response from the AI app
     */
    TRes call(TReq request);
}
