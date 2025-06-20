package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is triggered when a player picks hidden tiles from the board.
 * It is used to notify the number of hidden tiles that remain on the board.
 */
public record NumberHiddenTiles(
        int hiddenTilesCount
) implements Event, Serializable {
}
