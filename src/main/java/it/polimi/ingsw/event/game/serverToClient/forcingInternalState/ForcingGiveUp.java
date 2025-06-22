package it.polimi.ingsw.event.game.serverToClient.forcingInternalState;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player has lost the game, in order to notify the client that he has lost.
 * It is sent ONLY to the player that has lost, so it is not broadcast to all the players.
 * For this reason, it does not contain any information about the player who lost.
 * @param nickname the nickname of the player who has lost
 * @param message the message to be displayed to the player who has lost
 */
public record ForcingGiveUp(
        String nickname,
        String message
) implements Event, Serializable {
}
