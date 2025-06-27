package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used when a player picks a tile from the board.
 * @param nickname is the nickname of the player who picked the tile.
 * @param tileID   is the ID of the tile being picked.
 */
public record PickedTileFromBoard(
        String nickname,
        int tileID
) implements Event, Serializable {
}
