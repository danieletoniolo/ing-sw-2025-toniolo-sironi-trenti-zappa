package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This it.polimi.ingsw.event is used when a player place a tile on the board.
 * @param nickname is the nickname of the player who placed the tile.
 */
public record PlacedTileToBoard(
        String nickname
) implements Event, Serializable {}
