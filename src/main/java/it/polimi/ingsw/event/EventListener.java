package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;

/**
 * Represents a listener capable of handling a specific type of it.polimi.ingsw.event.
 *
 * Classes implementing this interface define logic to handle events of the specified type.
 * It is meant to be registered with a component that supports it.polimi.ingsw.event propagation,
 * such as {@link NetworkTransceiver}.
 *
 * @param <T> The type of it.polimi.ingsw.event this listener is capable of handling. Must extend {@link Event}.
 */
public interface EventListener<T extends Event> {
    /**
     * It will handle the it.polimi.ingsw.event that the server receive from the client
     * It is a callback that will be invoked by the {@link NetworkTransceiver}
     * @param event the it.polimi.ingsw.event to handle
     */
    void handle(T event);
}
