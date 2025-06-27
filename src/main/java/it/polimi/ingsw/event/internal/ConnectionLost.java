package it.polimi.ingsw.event.internal;

import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.UUID;

/**
 * Event representing a connection loss for a specific user.
 * This event is triggered when a user's connection to the system is lost.
 *
 * @param userID the unique identifier of the user who lost connection
 * @author Daniele Toniolo
 */
public record ConnectionLost(
        UUID userID
) implements Event, Serializable {
    /**
     * Registers an event handler for ConnectionLost events.
     *
     * @param transceiver the event transceiver to register the handler with
     * @param listener the event listener that will handle ConnectionLost events
     */
    public static void registerHandler(EventTransceiver transceiver, EventListener<ConnectionLost> listener) {
        new CastEventReceiver<ConnectionLost>(transceiver).registerListener(listener);
    }
}
