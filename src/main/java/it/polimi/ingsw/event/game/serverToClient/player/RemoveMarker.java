package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used when a player has to remove their marker from the board.
 * @param nickname The nickname of the player that has to remove the marker.
 * @author Vittorio Sironi
 */
public record RemoveMarker(
    String nickname
) implements Event, Serializable {
}
