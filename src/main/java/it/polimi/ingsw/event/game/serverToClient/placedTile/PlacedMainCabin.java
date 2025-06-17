package it.polimi.ingsw.event.game.serverToClient.placedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used to notify the server that a player has placed their main cabin on the board.
 * @param nickname   is the user ID of the player who placed the main cabin. Only the user knows their ID, so the event is not faked.
 * @param tileID     is the ID of the tile representing the main cabin.
 * @param connectors is an array of integers representing the connectors associated with the main cabin.
 */
public record PlacedMainCabin(
        String nickname,
        int tileID,
        int[] connectors
) implements Event, Serializable {
}
