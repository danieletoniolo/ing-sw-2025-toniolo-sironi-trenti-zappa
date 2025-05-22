package event;

import event.eventType.Event;

/**
 * Represents a listener capable of handling a specific type of event.
 *
 * Classes implementing this interface define logic to handle events of the specified type.
 * It is meant to be registered with a component that supports event propagation,
 * such as {@link NetworkTransceiver}.
 *
 * @param <T> The type of event this listener is capable of handling. Must extend {@link Event}.
 */
public interface EventListener<T extends Event> {
    /**
     * It will handle the event that the server receive from the client
     * It is a callback that will be invoked by the {@link NetworkTransceiver}
     * @param event the event to handle
     */
    void handle(T event);
}
