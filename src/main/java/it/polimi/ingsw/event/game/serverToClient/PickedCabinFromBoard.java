package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an event where a player selects a cabin from the game board.
 * This event is used to notify the system or other players about the chosen cabin and its associated connectors.
 *
 * @param nickname   The userID of the player who picked the cabin.
 * @param tileID     The unique identifier of the selected cabin tile.
 * @param connectors A list of integers representing the connectors type associated with the selected cabin.
 */
public record PickedCabinFromBoard(
    String nickname,
    int tileID,
    List<Integer> connectors
) implements Event, Serializable {
}
