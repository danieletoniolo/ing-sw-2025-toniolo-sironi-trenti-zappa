package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.spaceship.Component;

import java.io.Serializable;

/**
 * This event is used when a player pick a tile from the board.
 * @param nickname  is the userID of the player who picked the tile.
 * @param component the component that has been picked from the board.
 */
public record PickedTileFromBoard(
        String nickname,
        // TODO: to be changed to a more specific type
        Component component
) implements Event, Serializable {}
