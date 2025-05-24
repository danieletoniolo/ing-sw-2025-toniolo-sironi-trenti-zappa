package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * Represents an event signaling the start of the game.
 *
 * This event is typically sent once all necessary players have set ready in the lobby.
 * It serves to notify relevant components or clients that the game has officially begun.
 *
 */
public record StartingGame() implements Event, Serializable {
}
