package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This record represents an event that is used to indicate a player is currently engaged in playing.
 * It serves as a means to notify the system or other components about the player who is active in the game.
 *
 * @param nickname The username or identifier of the player who is currently playing.
 * @author Vittorio Sironi
 */
public record CurrentPlayer(
        String nickname
) implements Event, Serializable {
}
