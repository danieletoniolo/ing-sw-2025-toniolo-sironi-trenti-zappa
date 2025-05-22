package event.game.serverToClient;

import event.eventType.Event;
import event.eventType.StatusEvent;

import java.io.Serializable;

/**
 * Pota = expression used to convey surprise, resignation, enthusiasm, or agreement
 * Event to send an error message to the client.
 * The error message is used to inform the user about an error that occurred in the server.
 *
 * @param eventType    The type of the event that caused the error
 * @param errorMessage The error message to be sent to the client
 */
public record Pota(
        Class<? extends Event> eventType,
        String errorMessage
) implements StatusEvent, Serializable  {
}
