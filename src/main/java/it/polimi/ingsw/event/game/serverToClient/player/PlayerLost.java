package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player has lost the game, in order to notify the client that he has lost.
 * It is sent ONLY to the player that has lost, so it is not broadcast to all the players.
 * For this reason, it does not contain any information about the player who lost.
 */
public record PlayerLost(
) implements Event, Serializable {
}
