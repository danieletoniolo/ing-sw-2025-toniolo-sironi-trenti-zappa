package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event where a player selects a cabin from the game board.
 * This event is used to notify the system or other players about the chosen cabin and its associated connectors.
 *
 * @param nickname          The userID of the player who picked the cabin.
 * @param tileID            The unique identifier of the selected cabin tile.
 * @param clockwiseRotation The rotation of the cabin in a clockwise direction, represented as an integer.
 * @param connectors        A list of integers representing the connectors type associated with the selected cabin.
 * @author Vittorio Sironi
 */
public record PickedCabinFromBoard(
    String nickname,
    int tileID,
    int clockwiseRotation,
    int[] connectors
) implements Event, Serializable {
}
