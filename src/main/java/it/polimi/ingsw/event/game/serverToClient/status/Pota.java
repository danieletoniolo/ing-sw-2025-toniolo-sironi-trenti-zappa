package it.polimi.ingsw.event.game.serverToClient.status;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;

/**
 * Pota = expression used to convey surprise, resignation, enthusiasm, or agreement
 * Pota = "Panic Occurred, Try Again"
 * Pota = "Please, Observe This Anomaly"
 * Pota = "Problem? Oh, That's Awkward"
 * Event to send an error message to the client.
 * The error message is used to inform the user about an error that occurred in the server.
 *
 * @param userID       The user ID of the player who has done the request
 *                     We can put it in the event because this event will be sent only to the client who have made the request
 * @param eventType    The type of the event that caused the error
 * @param errorMessage The error message to be sent to the client
 * @author Vittorio Sironi
 */
public record Pota(
        String userID,
        Class<? extends Event> eventType,
        String errorMessage
) implements StatusEvent, Serializable  {
    @Override
    public String get() {
        return "POTA";
    }

    @Override
    public String getUserID() {
        return userID;
    }
}
