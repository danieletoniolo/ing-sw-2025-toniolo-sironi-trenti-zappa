package event.game;

import event.Event;

import java.io.Serializable;

/**
 * Event to send an error message to the client.
 * The error message is used to inform the user about an error that occurred in the server.
 *
 * @param eventType   The type of the event that caused the error
 * @param errorMessage The error message to be sent to the client
 */
public record Error(
        Class<? extends Event> eventType,
        String errorMessage
) implements Event, Serializable  {
}
