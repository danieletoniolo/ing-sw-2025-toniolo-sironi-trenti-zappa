package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player have to move the marker on the board.
 * @param nickname The userID of the player that has to move the marker.
 * @param steps    The new position of the marker on the board. It is not the number of the position to add to the current position
 */
public record MoveMarker(
        String nickname,
        int steps
) implements Event, Serializable {
}
