package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This record represents an event where a player selects specific connectors on a tile
 * from the game board. It notifies the system and other players about the action taken.
 *
 * @param nickname  The username or identifier of the player picking the connectors.
 * @param tileID    The unique identifier of the tile from which the connectors are picked.
 * @param connectors A list of integers representing the type of the connectors associated to the connectors tile.
 */
public record PickedConnectorsFromBoard(
        String nickname,
        int tileID,
        List<Integer> connectors
) implements Event, Serializable {
}
