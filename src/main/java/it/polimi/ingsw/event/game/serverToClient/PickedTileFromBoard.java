package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player pick a tile from the board.
 * @param nickname is the userID of the player who picked the tile.
 * @param tileID   The ID of the tile being picked.
 */
public record PickedTileFromBoard(
        String nickname,
        int tileID
) implements Event, Serializable {}
