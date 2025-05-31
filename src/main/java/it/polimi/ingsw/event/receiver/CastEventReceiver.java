package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CastEventReceiver} class provides a mechanism to register and unregister it.polimi.ingsw.event listeners for a specific
 * it.polimi.ingsw.event type, while ensuring type safety through type casting. It wraps a generic {@link EventReceiver} instance,
 * delegating the actual registration and unregistration logic to it, and facilitates the casting of {@link Event}
 * objects to their specific types before handling them.
 *
 * This class ensures thread-safe listener management using a synchronization lock, allowing multiple threads to
 * perform operations like listener registration and unregistration safely. Additionally, it maintains a mapping
 * of specific it.polimi.ingsw.event listeners in order to associate the listener from the register, to the listener on the actual transceiver
 *
 * @param <T> the specific type of {@link Event} this receiver will handle
 */
public class CastEventReceiver<T extends Event> implements EventReceiver<T> {
    /**
     * Represents a generic it.polimi.ingsw.event receiver that is used to handle events conforming to the {@link Event} interface.
     * This is a final instance used as a delegation mechanism for managing it.polimi.ingsw.event listeners or handling events of a
     * generic type {@link Event}. It serves as the underlying it.polimi.ingsw.event receiver for delegating operations like
     * registration and unregistration of it.polimi.ingsw.event listeners.
     *
     * The {@code receiver} is central to the functionality of {@link CastEventReceiver}, where it is used
     * for managing the lifecycle of it.polimi.ingsw.event listeners as well as propagating events to the appropriate listeners.
     */
    private final EventReceiver<Event> receiver;

    /**
     * A synchronization lock used to ensure thread safety when accessing or modifying shared resources within the class.
     */
    private final Object lock = new Object();

    /**
     * A mapping that associates specific it.polimi.ingsw.event listeners with their corresponding generic it.polimi.ingsw.event listeners.
     * This map is used to maintain a reference between the strongly-typed it.polimi.ingsw.event listener ({@code EventListener<T>})
     * and the type-erased generic it.polimi.ingsw.event listener ({@code EventListener<Event>}) wrapped around it.
     *
     * The key represents the specific listener that processes events of type {@code T}.
     * The value represents a generic listener that acts as a wrapper around the specific listener,
     * facilitating type-safe it.polimi.ingsw.event handling for events conforming to {@link Event}.
     *
     * This mapping is central to enabling type-safe listener registration and unregistration in the
     * {@link CastEventReceiver} class, as it ensures that listeners are correctly linked to their
     * generic counterparts while maintaining type integrity.
     */
    private final Map<EventListener<T>, EventListener<Event>> listeners = new HashMap<>();

    public CastEventReceiver(EventReceiver<Event> receiver) {
        this.receiver = receiver;
    }

    /**
     * Registers a listener to handle events of a specific type. The listener will be triggered
     * whenever an it.polimi.ingsw.event of the corresponding type is received. This method ensures that the listener
     * is synchronized with the internal lock and properly registered to the receiver.
     *
     * @param listener               the listener that will handle the incoming events, with a specific type parameter.
     *                               The listener's {@code handle} method should be implemented to define how the
     *                               events are processed.
     * @throws IllegalStateException if the it.polimi.ingsw.event type does not match the expected concrete type.
     */
    @Override
    public void registerListener(EventListener<T> listener) {
        synchronized (lock) {
            listeners.put(listener, data -> {
                try {
                    listener.handle((T) data);
                    Logger.getInstance().log(Logger.LogLevel.INFO, "Event handled by listener: " + listener.getClass().getName(), false);
                } catch (ClassCastException e) {
                    // Logger.getInstance().log(Logger.LogLevel.ERROR, "Event " + data.getClass().getName() + " cannot be handled by listener: " + listener.getClass().getName(), false);
                }
            });

            //Logger.getInstance().log(Logger.LogLevel.INFO, "Listener registered: " + listener.getClass().getName(), false);
            receiver.registerListener(listeners.get(listener));
        }
    }

    /**
     * Unregisters a given listener from the it.polimi.ingsw.event receiver. Once unregistered, the listener
     * will no longer receive events of the corresponding type. This method ensures
     * thread-safety during the removal process and properly unregisters the listener
     * from the underlying receiver.
     *
     * @param listener the {@link EventListener} to be removed from the list of registered listeners.
     */
    @Override
    public void unregisterListener(EventListener<T> listener) {
        synchronized (lock) {
            receiver.unregisterListener(listeners.remove(listener));
        }
    }
}
