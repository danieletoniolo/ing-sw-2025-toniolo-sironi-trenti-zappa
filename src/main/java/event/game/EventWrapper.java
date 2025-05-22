package event.game;

import event.eventType.Event;

import java.io.Serializable;

/**
 * This class is used to wrap an event with an ID. It is used to send events
 * @param ID the ID of the event
 * @param event the event to be sent
 * @param <T> the type of the event
 */
public record EventWrapper<T extends Event>(
        int ID,
        T event
) implements Event, Serializable {
}
