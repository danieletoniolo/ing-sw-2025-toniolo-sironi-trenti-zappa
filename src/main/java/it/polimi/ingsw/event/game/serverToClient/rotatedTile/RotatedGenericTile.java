package it.polimi.ingsw.event.game.serverToClient.rotatedTile;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is sent when the user rotates a tile.
 * @param nickname          is the userID of the player that has rotated the tile.
 * @param tileID            is the ID of the tile that has been rotated.
 * @param connectors        is a list of integers representing the connectors of the tile after rotation.
 */
public record RotatedGenericTile(
        String nickname,
        int tileID,
        Integer[] connectors
) implements Event, Serializable {}
