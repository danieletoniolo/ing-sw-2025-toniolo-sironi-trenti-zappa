package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event that notifies a change in the state of the game or system.
 * This record encapsulates the new state that has been reached.
 *
 * @param newState The identifier of the new state after the change has occurred.
 */
public record StateChanged(
    Integer newState
) implements Event, Serializable {
}
