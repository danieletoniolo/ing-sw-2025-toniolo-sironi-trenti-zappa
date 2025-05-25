package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is sent when the user rotates a tile.
 * @param nickname is the userID of the player that has rotated the tile.
 * @param tileID   is the ID of the tile that has been rotated.
 */
public record TileRotated(
        String nickname,
        int tileID
) implements Event, Serializable {}
