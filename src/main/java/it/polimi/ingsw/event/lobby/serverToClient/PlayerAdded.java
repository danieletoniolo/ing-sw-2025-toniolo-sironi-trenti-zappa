package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event indicating that a player has been added to a lobby.
 *
 * This event provides the userID of the player and the color associated with them.
 * It is used to inform relevant components or clients about the addition of a new
 * player to a specific context, such as a game lobby.
 *
 * @param nickname the userID of the player being added
 * @param color    the color assigned to the player
 * @author Vittorio Sironi
 */
public record PlayerAdded(
    String nickname,
    Integer color
) implements Event, Serializable {
}
