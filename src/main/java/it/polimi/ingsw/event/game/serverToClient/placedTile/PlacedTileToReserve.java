package it.polimi.ingsw.event.game.serverToClient.placedTile;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player place a tile on the reserve.
 * @param nickname is the userID of the player who placed the tile.
 * @author Daniele Toniolo
 */
public record PlacedTileToReserve(
        String nickname
) implements Event, Serializable {}
