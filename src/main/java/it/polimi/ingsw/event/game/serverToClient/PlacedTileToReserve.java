package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This it.polimi.ingsw.event is used when a player place a tile on the reserve.
 * @param nickname is the nickname of the player who placed the tile.
 */
public record PlacedTileToReserve(
    String nickname
) implements Event, Serializable {}
