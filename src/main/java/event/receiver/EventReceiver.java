package event.receiver;

import event.eventType.Event;
import event.EventListener;

/**
 * Represents a generic mechanism to register and unregister listeners for specific types of events.
 * A class implementing this interface must define how event listeners are managed and associated
 * with a specific type of event.
 *
 * @param <T> the specific type of {@link Event} this receiver will handle
 */
public interface EventReceiver<T extends Event> {
    /**
     * Registers an event listener to handle events of the specified type. The listener will
     * be invoked whenever an event of the corresponding type is received.
     *
     * @param listener the event listener to register; it should define how the events of type {@code T}
     *                 are processed via its {@code handle} method
     */
    void registerListener(EventListener<T> listener);

    /**
     * Unregisters a given event listener, ensuring it will no longer receive events of type {@code T}.
     * This method removes the listener from the list of registered listeners maintained by the event receiver.
     *
     * @param listener the {@link EventListener} to be unregistered. This listener will stop receiving
     *                 notifications for events of the corresponding type.
     */
    void unregisterListener(EventListener<T> listener);
}
