package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player place a tile on the reserve.
 * @param nickname is the userID of the player who placed the tile.
 */
public record PlacedTileToReserve(
        String nickname
) implements Event, Serializable {}
