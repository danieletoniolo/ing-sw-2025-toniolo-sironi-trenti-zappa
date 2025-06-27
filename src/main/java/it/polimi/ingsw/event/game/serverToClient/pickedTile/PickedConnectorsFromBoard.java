package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This record represents an event where a player selects specific connectors on a tile
 * from the game board. It notifies the system and other players about the action taken.
 *
 * @param nickname          The username or identifier of the player picking the connectors.
 * @param tileID            The unique identifier of the tile from which the connectors are picked.
 * @param clockwiseRotation The clockwise rotation of the connectors tile, represented as an integer.
 * @param connectors        A list of integers representing the type of the connectors associated to the connectors tile.
 * @author Vittorio Sironi
 */
public record PickedConnectorsFromBoard(
        String nickname,
        int tileID,
        int clockwiseRotation,
        int[] connectors
) implements Event, Serializable {
}
