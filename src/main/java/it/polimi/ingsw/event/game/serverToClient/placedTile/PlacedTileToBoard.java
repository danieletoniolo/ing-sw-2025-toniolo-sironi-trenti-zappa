package it.polimi.ingsw.event.game.serverToClient.placedTile;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player place a tile on the board.
 * @param nickname is the userID of the player who placed the tile.
 * @author Daniele Toniolo
 */
public record PlacedTileToBoard(
        String nickname
) implements Event, Serializable {}
