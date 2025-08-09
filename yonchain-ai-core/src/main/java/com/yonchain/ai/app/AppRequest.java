package com.yonchain.ai.app;

/**
 * Interface representing a request to an AI app. This interface encapsulates the
 * necessary information required to interact with an AI app, including instructions or
 * inputs (of generic type T) and additional app options. It provides a standardized way
 * to send requests to AI apps, ensuring that all necessary details are included and can
 * be easily managed.
 *
 * @param <T> the type of instructions or input required by the AI app
 * @author Cgy
 * @since 0.1.0
 */
public interface AppRequest<T> {

    /**
     * Retrieves the instructions or input required by the AI app.
     *
     * @return the instructions or input required by the AI app
     */
    T getInstructions(); // required input

    /**
     * Retrieves the customizable options for AI app interactions.
     *
     * @return the customizable options for AI app interactions
     */
    AppOptions getOptions();

}
