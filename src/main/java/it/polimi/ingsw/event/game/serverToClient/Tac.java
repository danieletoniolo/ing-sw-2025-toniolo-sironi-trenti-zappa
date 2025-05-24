package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;

/**
 * Represents a successful occurrence of a particular event.
 * The {@code Success} record is used to encapsulate the type of the event
 * that has successfully occurred within the system.
 *
 * @param eventType The class type of the event that completed successfully.
 */
public record Tac(
        Class<? extends Event> eventType
) implements StatusEvent, Serializable {
    @Override
    public String get() {
        return "TAC";
    }
}
