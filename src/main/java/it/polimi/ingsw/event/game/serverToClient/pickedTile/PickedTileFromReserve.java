package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player pick a tile from the reserve.
 * @param nickname          is the userID of the player who picked the tile.
 * @param tileID            The ID of the tile being picked.
 * @param clockwiseRotation The rotation of the tile in a clockwise direction, represented as an integer.
 */
public record PickedTileFromReserve(
        String nickname,
        int tileID,
        int clockwiseRotation
) implements Event, Serializable {}
