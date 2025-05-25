package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventListener;

/**
 * Represents a generic mechanism to register and unregister listeners for specific types of events.
 * A class implementing this interface must define how it.polimi.ingsw.event listeners are managed and associated
 * with a specific type of it.polimi.ingsw.event.
 *
 * @param <T> the specific type of {@link Event} this receiver will handle
 */
public interface EventReceiver<T extends Event> {
    /**
     * Registers an it.polimi.ingsw.event listener to handle events of the specified type. The listener will
     * be invoked whenever an it.polimi.ingsw.event of the corresponding type is received.
     *
     * @param listener the it.polimi.ingsw.event listener to register; it should define how the events of type {@code T}
     *                 are processed via its {@code handle} method
     */
    void registerListener(EventListener<T> listener);

    /**
     * Unregisters a given it.polimi.ingsw.event listener, ensuring it will no longer receive events of type {@code T}.
     * This method removes the listener from the list of registered listeners maintained by the it.polimi.ingsw.event receiver.
     *
     * @param listener the {@link EventListener} to be unregistered. This listener will stop receiving
     *                 notifications for events of the corresponding type.
     */
    void unregisterListener(EventListener<T> listener);
}
