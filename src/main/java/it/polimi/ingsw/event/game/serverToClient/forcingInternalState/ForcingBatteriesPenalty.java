package it.polimi.ingsw.event.game.serverToClient.forcingInternalState;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to notify the client that the player has to discard batteries.
 */
public record ForcingBatteriesPenalty(
        String nickname
) implements Event, Serializable {
}
