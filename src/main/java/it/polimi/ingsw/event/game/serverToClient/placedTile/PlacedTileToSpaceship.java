package it.polimi.ingsw.event.game.serverToClient.placedTile;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player place a tile on the spaceship.
 * @param nickname is the userID of the player that placed the tile.
 * @param row      is the row of the tile on the spaceship.
 * @param column   is the column of the tile on the spaceship.
 */
public record PlacedTileToSpaceship(
        String nickname,
        int row,
        int column
) implements Event, Serializable {}
