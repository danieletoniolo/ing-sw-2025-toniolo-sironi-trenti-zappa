package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This it.polimi.ingsw.event is used when a player has lost the game.
 * It is used to notify the other players that the player has lost.
 * @param nickname is the nickname of the player that has lost.
 */
public record PlayerLost(
        String nickname
) implements Event, Serializable {
}
