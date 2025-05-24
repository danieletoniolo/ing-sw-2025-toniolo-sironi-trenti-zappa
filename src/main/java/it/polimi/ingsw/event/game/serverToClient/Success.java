package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;

/**
 * Represents a successful occurrence of a particular it.polimi.ingsw.event.
 * The {@code Success} record is used to encapsulate the type of the it.polimi.ingsw.event
 * that has successfully occurred within the system.
 *
 * @param eventType The class type of the it.polimi.ingsw.event that completed successfully.
 */
public record Success(
        Class<? extends Event> eventType
) implements StatusEvent, Serializable {
}
