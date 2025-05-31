package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player pick a tile from the spaceship.
 * @param nickname is the userID of the player that pick the tile.
 */
public record PickedTileFromSpaceship(
        String nickname
) implements Event, Serializable {}
