package event.game.serverToClient;

import event.eventType.Event;
import event.eventType.StatusEvent;

import java.io.Serializable;

/**
 * Represents a successful occurrence of a particular event.
 * The {@code Success} record is used to encapsulate the type of the event
 * that has successfully occurred within the system.
 *
 * @param eventType The class type of the event that completed successfully.
 */
public record Success(
        Class<? extends Event> eventType
) implements StatusEvent, Serializable {
}
