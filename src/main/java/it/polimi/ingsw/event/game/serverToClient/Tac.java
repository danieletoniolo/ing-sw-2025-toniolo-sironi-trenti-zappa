package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;

/**
 * Represents a successful occurrence of a particular event.
 * The {@code Success} record is used to encapsulate the type of the event
 * that has successfully occurred within the system.
 * @param userID       The user ID of the player who has done the request
 *  *                  We can put it in the event because this event will be sent only to the client who have made the request
 * @param eventType The class type of the event that completed successfully.
 */
public record Tac(
        String userID,
        Class<? extends Event> eventType
) implements StatusEvent, Serializable {
    @Override
    public String get() {
        return "TAC";
    }

    @Override
    public String getUserID() {
        return userID;
    }
}
