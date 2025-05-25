package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;
import java.util.List;

/**
 * This event is sent when the user rotates a tile.
 * @param nickname          is the userID of the player that has rotated the tile.
 * @param tileID            is the ID of the tile that has been rotated.
 * @param clockwiseRotation is the number of clockwise rotations applied to the tile.
 * @param connectors        is a list of integers representing the connectors of the tile after rotation.
 */
public record TileRotated(
        String nickname,
        int tileID,
        int clockwiseRotation,
        List<Integer> connectors
) implements Event, Serializable {}
